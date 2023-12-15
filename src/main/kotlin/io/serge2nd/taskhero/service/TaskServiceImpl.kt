package io.serge2nd.taskhero.service

import io.serge2nd.taskhero.db.*
import io.serge2nd.taskhero.dto.*
import io.serge2nd.taskhero.enums.TaskStatus.*
import io.serge2nd.taskhero.etc.invoke
import io.serge2nd.taskhero.service.ServiceError.Companion.badRequest
import io.serge2nd.taskhero.service.ServiceError.Companion.conflict
import io.serge2nd.taskhero.service.ServiceError.Companion.notFound
import io.serge2nd.taskhero.service.ServiceError.Companion.serviceScope
import org.hibernate.Hibernate.isInitialized
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.Duration.ZERO
import io.serge2nd.taskhero.dto.AccountTaskLogDto.Spent as LogEntry
import io.serge2nd.taskhero.spi.IoScoped as Repository

@Service
class TaskServiceImpl(
    private val accountRepo: Repository<Accounts>,
    private val teamRepo: Repository<Teams>,
    private val taskRepo: Repository<Tasks>,
) : TaskService {

    override suspend fun getAccountTaskLog(rq: GetAccountTaskLogDto) = serviceScope {
        val acc = accountRepo { findByUserName(rq.userName) } ?: notFound(Account::userName, rq.userName)
        val tasks = taskRepo { acc.heroes.flatMap { findTaskLog(it, rq.logFrom) } }
        AccountTaskLogDto(
            log = tasks
                .flatMap { t -> t.log.map { LogEntry(t.title, t.team.title, it.spent, it.addedAt) } }
                .sortedByDescending { it.addedAt },
            tasks = tasks.map {
                AccountTaskLogDto.TaskExpanded(
                    title = it.title,
                    dueDate = it.dueDate,
                    cost = it.cost,
                    priority = it.priority,
                    team = TeamDto(it.team.title),
                    status = it.status,
                    spentTotal = it.spentTotal,
                    createdAt = it.createdAt
                )
            }.sortedByDescending { it.createdAt }
        )
    }

    override suspend fun getTask(rq: GetTaskDto) = serviceScope {
        val team = teamRepo { findByTitle(rq.team) } ?: notFound(Team::title, rq.team)
        val task = taskRepo { findByTeamAndTitle(team, rq.title) } ?: notFound(Task::title, rq.title.trim())
        task.toDto()
    }

    override suspend fun listTasks(rq: ListTasksDto) = serviceScope {
        taskRepo { findByTeamTitleIn(rq.teams) }.map(Task::toDto).sortedByDescending { it.createdAt }
    }

    override suspend fun createTask(rq: CreateTaskDto) = serviceScope {
        val team = teamRepo { findByTitle(rq.team) }
            ?: notFound(Team::title, rq.team)
        val chiefAcc = accountRepo { findByUserName(rq.chiefUser) }
            ?: notFound(Account::userName, rq.chiefUser)
        val heroAcc = accountRepo { findByUserName(rq.heroUser) }
            ?: notFound(Account::userName, rq.heroUser)
        val chief = chiefAcc.heroesByTeamId[team.id]
            ?: notFound("chief ${rq.chiefUser} not in team", rq.team)
        val hero = heroAcc.heroesByTeamId[team.id]
            ?: notFound("hero ${rq.heroUser} not in team", rq.team)
        if (taskRepo { findByTeamAndTitle(team, rq.title) } != null)
            conflict(Task::title, rq.title.trim())
        val cost = Duration.parse(rq.cost)
            .also { if (it <= ZERO) badRequest("cost must be positive") }
        taskRepo {
            Task(
                title = rq.title,
                details = rq.details,
                dueDate = rq.dueDate,
                cost = cost,
                priority = rq.priority,
                team = team,
                chief = chief,
                hero = hero
            ).let(::save)
        }.let(Task::toDto)
    }

    override suspend fun updateTaskStatus(title: String, rq: UpdateTaskStatusDto) = serviceScope {
        val team = teamRepo { findByTitle(rq.team) } ?: notFound(Team::title, rq.team)
        taskRepo {
            findByTeamAndTitle(team, title)?.apply {
                if (
                    status == Open && rq.status != Work ||
                    status == Work && rq.status != Done ||
                    status == Done
                ) badRequest("$status=>${rq.status} illegal transition")
                save(apply { status = rq.status })
            } ?: notFound(Task::title, title.trim())
        }.let {}
    }

    override suspend fun logSpent(title: String, rq: LogSpentDto) = serviceScope {
        val team = teamRepo { findByTitle(rq.team) } ?: notFound(Team::title, rq.team)
        val acc = accountRepo { findByUserName(rq.userName) } ?: notFound(Account::userName, rq.userName)
        taskRepo {
            val task = findByTeamAndTitle(team, title) ?: notFound(Task::title, title.trim())
            if (task.status != Work)
                badRequest("task '${task.title}' status ${task.status} != ${Work}")
            if (acc.id != task.chief.userId && acc.id != task.hero.userId)
                badRequest("only chief or assignee can log on task")
            val spent = Duration.parse(rq.spent)
                .also { if (it <= ZERO) badRequest("spent must be positive") }
            task.spentTotal += spent
            task.logSpent(Task.Spent(acc.heroesByTeamId[team.id]!!, spent))
            save(task)
        }.let {}
    }
}

private fun Task.toDto() = TaskDto(
    title = title,
    details = details,
    dueDate = dueDate,
    cost = cost,
    priority = priority,
    team = TeamDto(team.title),
    chief = HeroDto(chief.userName),
    hero = HeroDto(hero.userName),
    status = status,
    spentTotal = spentTotal,
    createdAt = createdAt,
    log = if (isInitialized(log)) log.map {
        TaskSpentDto(HeroDto(it.hero.userName), it.spent, it.addedAt)
    } else emptyList()
)
