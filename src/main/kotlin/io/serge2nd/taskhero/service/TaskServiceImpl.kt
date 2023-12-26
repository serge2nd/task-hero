package io.serge2nd.taskhero.service

import io.serge2nd.taskhero.db.*
import io.serge2nd.taskhero.dto.*
import io.serge2nd.taskhero.enums.TaskStatus.*
import io.serge2nd.taskhero.etc.invoke
import io.serge2nd.taskhero.service.ServiceError.Companion.clash
import io.serge2nd.taskhero.service.ServiceError.Companion.fault
import io.serge2nd.taskhero.service.ServiceError.Companion.lack
import io.serge2nd.taskhero.service.ServiceError.Companion.serviceScope
import io.serge2nd.taskhero.service.ServiceError.Companion.wrong
import io.serge2nd.taskhero.service.assist.TaskAudit
import org.hibernate.Hibernate.isInitialized
import org.springframework.stereotype.Service
import org.springframework.transaction.support.TransactionOperations
import java.time.Duration
import io.serge2nd.taskhero.dto.AccountTaskLogDto.Spent as LogEntry
import io.serge2nd.taskhero.spi.IoScoped as Repository

@Service
class TaskServiceImpl(
    private val accountRepo: Repository<Accounts>,
    private val teamRepo: Repository<Teams>,
    private val taskRepo: Repository<Tasks>,
    private val txOps: TransactionOperations,
    private val audit: TaskAudit,
) : TaskService {

    override suspend fun getAccountTaskLog(rq: GetAccountTaskLogDto) = serviceScope {
        val acc = accountRepo { findByUserName(rq.userName) } ?: lack(Account::userName, rq.userName)
        val tasks = taskRepo { findTaskLog(acc.heroes, rq.logFrom) }
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
        val team = teamRepo { findByTitle(rq.team) } ?: lack(Team::title, rq.team)
        val task = taskRepo { findByTeamAndTitleHeavy(team, rq.title) } ?: lack(Task::title, rq.title.trim())
        task.toDto()
    }

    override suspend fun listTasks(rq: ListTasksDto) = serviceScope {
        taskRepo { findByTeamTitleIn(rq.teams) }.map(Task::toDto).sortedByDescending { it.createdAt }
    }

    override suspend fun getTaskStats(rq: ListTasksDto) = listTasks(rq).map(audit::buildStats)

    override suspend fun createTask(rq: CreateTaskDto) = serviceScope {
        val team = teamRepo { findByTitle(rq.team) } ?: lack(Team::title, rq.team)
        val chiefAcc = accountRepo { findByUserName(rq.chiefUser) } ?: lack(Account::userName, rq.chiefUser)
        val heroAcc = accountRepo { findByUserName(rq.heroUser) } ?: lack(Account::userName, rq.heroUser)
        val chief = chiefAcc.hero(team) ?: lack("not in team ${rq.team}", rq.chiefUser)
        val hero = heroAcc.hero(team) ?: lack("not in team ${rq.team}", rq.heroUser)
        if (taskRepo { findByTeamAndTitle(team, rq.title) } != null) clash(Task::title, rq.title.trim())
        taskRepo {
            Task(
                title = rq.title,
                details = rq.details,
                dueDate = rq.dueDate,
                cost = Duration.parse(rq.cost),
                priority = rq.priority,
                team = team.ref,
                chief = chief.ref,
                hero = hero.ref
            ).let(::save)
        }.run { toDto(team, chief, hero) }
    }

    override suspend fun updateTaskStatus(title: String, rq: UpdateTaskStatusDto) = serviceScope {
        val team = teamRepo { findByTitle(rq.team) } ?: lack(Team::title, rq.team)
        txOps(taskRepo) {
            findByTeamAndTitle(team, title)?.apply {
                if (
                    status == Open && rq.status != Work ||
                    status == Work && rq.status != Done ||
                    status == Done
                ) wrong("$status=>${rq.status} illegal transition")
                save(apply { status = rq.status })
            } ?: lack(Task::title, title.trim())
        }.let {}
    }

    override suspend fun logSpent(title: String, rq: LogSpentDto) = serviceScope {
        val team = teamRepo { findByTitle(rq.team) } ?: lack(Team::title, rq.team)
        val acc = accountRepo { findByUserName(rq.userName) } ?: lack(Account::userName, rq.userName)
        txOps(taskRepo) {
            val task = findByTeamAndTitleAndStatus(team, title, Work)
                ?: lack("[Task in Work].title", title.trim())
            if (acc.id != task.chief.userId && acc.id != task.hero.userId)
                wrong("only chief or assignee can log on task")
            val hero = acc.hero(team) ?: fault("Account.heroes broken")
            task.logSpent(Task.Spent(hero, Duration.parse(rq.spent)))
        }.let {}
    }
}

private fun Task.toDto(team: Team? = null, chief: Hero? = null, hero: Hero? = null) = TaskDto(
    title = title,
    details = details,
    dueDate = dueDate,
    cost = cost,
    priority = priority,
    team = TeamDto((team ?: this.team).title),
    chief = HeroDto((chief ?: this.chief).userName),
    hero = HeroDto((hero ?: this.hero).userName),
    status = status,
    spentTotal = spentTotal,
    createdAt = createdAt,
    log = log.takeIf(::isInitialized)?.map {
        TaskSpentDto(HeroDto(it.hero.userName), it.spent, it.addedAt)
    }
)
