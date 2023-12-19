package io.serge2nd.taskherodb.service

import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldBeSingleton
import io.kotest.matchers.comparables.shouldBeGreaterThanOrEqualTo
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.serge2nd.taskhero.db.*
import io.serge2nd.taskhero.dto.*
import io.serge2nd.taskhero.enums.TaskPriority
import io.serge2nd.taskhero.enums.TaskPriority.Low
import io.serge2nd.taskhero.enums.TaskStatus
import io.serge2nd.taskhero.service.ServiceError.NotFound
import io.serge2nd.taskhero.service.TaskServiceImpl
import io.serge2nd.taskherodb.config.JpaAppTest
import jakarta.persistence.EntityManager
import org.springframework.test.util.ReflectionTestUtils.setField
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.support.TransactionOperations
import java.time.Duration
import java.time.Duration.ZERO
import java.time.LocalDate
import java.time.OffsetDateTime

/**
 * Strictly, that's not a "canonical" unit test,
 * but it verifies a service as a unit covering transactional query accuracy as well
 * and being not so trivial as it would be with repository mocks.
 */
@JpaAppTest
@Transactional
internal class TaskServiceImplTest(
    accRepo: Accounts,
    teamRepo: Teams,
    taskRepo: Tasks,
    em: EntityManager,
    txOps: TransactionOperations
) : FunSpec({

    val srv = TaskServiceImpl(accRepo, teamRepo, taskRepo, txOps)

    fun EntityManager.reset() { flush(); clear() }

    test("get task") {
        // GIVEN
        val team = teamRepo.findByTitle("police")!!
        val acc = accRepo.findByUserName("alex")!!
        val hero = acc.hero(team)!!
        val expected = TaskDto(
            "wake up",
            "just wake up",
            LocalDate.now(),
            Duration.ofHours(3),
            TaskPriority.High,
            TeamDto(team.title),
            HeroDto(acc.userName),
            HeroDto(acc.userName),
            TaskStatus.Work,
            Duration.ofHours(2),
            OffsetDateTime.now(),
            emptyList()
        ).apply { em.persist(Task(0, title, details, dueDate, cost, priority, team, hero, hero, status, spentTotal, createdAt)) }

        // WHEN
        val actual = srv.getTask(GetTaskDto(expected.title, team.title))

        // THEN
        actual shouldBeRight expected
    }

    test("get task, no such task") {
        // GIVEN
        em.persist(Team(0, "team9"))

        // WHEN
        val actual = srv.getTask(GetTaskDto("task9", "team9"))

        // THEN
        actual.shouldBeLeft().shouldBeInstanceOf<NotFound>().run {
            prop shouldBe "Task.title"
            value shouldBe "task9"
        }
    }

    test("get task, no such team") {
        // WHEN
        val actual = srv.getTask(GetTaskDto("task9", "team9"))

        // THEN
        actual.shouldBeLeft().shouldBeInstanceOf<NotFound>().run {
            prop shouldBe "Team.title"
            value shouldBe "team9"
        }
    }

    test("create task") {
        // GIVEN
        val team = teamRepo.findByTitle("bandits")!!
        val chiefUser = accRepo.findByUserName("serge")!!.userName
        val heroUser = accRepo.findByUserName("vi")!!.userName
        val expected = TaskDto(
            "wake up",
            "just wake up",
            LocalDate.now(),
            Duration.ofHours(3),
            TaskPriority.High,
            TeamDto(team.title),
            HeroDto(chiefUser),
            HeroDto(heroUser),
            TaskStatus.Open,
            ZERO,
            OffsetDateTime.now(),
            emptyList()
        )

        // WHEN
        val actual = expected.run {
            srv.createTask(CreateTaskDto(title, details, dueDate, "$cost", priority, team.title, chiefUser, heroUser))
        }

        // THEN
        actual.shouldBeRight().let {
            it.createdAt shouldBeGreaterThanOrEqualTo expected.createdAt
            setField(it, "createdAt", expected.createdAt)
            it shouldBe expected
        }
    }

    test("update task status") {
        // GIVEN
        val team = teamRepo.findByTitle("police")!!
        val hero = accRepo.findByUserName("alex")!!.hero(team)!!
        em.persist(Task(0, "", "", LocalDate.now(), ZERO, Low, team, hero, hero))
        em.reset()

        // WHEN
        val actual = srv.updateTaskStatus("", UpdateTaskStatusDto(team.title, TaskStatus.Work))

        // THEN
        actual shouldBeRight Unit
        em.createQuery("from Task", Task::class.java)
            .resultList.shouldBeSingleton { it.status shouldBe TaskStatus.Work }
    }

    test("log spent") {
        // GIVEN
        val team = teamRepo.findByTitle("police")!!
        val hero = accRepo.findByUserName("alex")!!.hero(team)!!
        val spentTotal = Duration.ofHours(1)
        val spent = Duration.ofMinutes(45)
        em.persist(Task(0, "", "", LocalDate.now(), ZERO, Low, team, hero, hero, TaskStatus.Work, spentTotal - spent))
        em.reset()

        // WHEN
        val actual = srv.logSpent("", LogSpentDto(team.title, "alex", "$spent"))
        em.reset()

        // THEN
        actual shouldBeRight Unit
        em.createQuery("from Task t join fetch t.log", Task::class.java)
            .resultList.shouldBeSingleton {
                it.spentTotal shouldBe spentTotal
                it.log.shouldBeSingleton { it.spent shouldBe spent }
            }
    }

    // TODO more tests
})
