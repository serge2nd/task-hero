package io.serge2nd.taskherodb.service

import arrow.core.flatMap
import com.linecorp.kotlinjdsl.support.spring.data.jpa.repository.KotlinJdslJpqlExecutor
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.matchers.collections.shouldBeSingleton
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.comparables.shouldBeGreaterThanOrEqualTo
import io.kotest.matchers.shouldBe
import io.mockk.mockk
import io.serge2nd.taskhero.db.*
import io.serge2nd.taskhero.dto.*
import io.serge2nd.taskhero.enums.TaskStatus.Open
import io.serge2nd.taskhero.enums.TaskStatus.Work
import io.serge2nd.taskhero.service.ServiceError.Lack
import io.serge2nd.taskhero.service.TaskServiceImpl
import io.serge2nd.taskherodb.*
import io.serge2nd.taskherodb.config.JpaAppTest
import jakarta.persistence.EntityManager
import org.springframework.test.util.ReflectionTestUtils.setField
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.support.TransactionOperations
import java.time.Duration.*

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
    txOps: TransactionOperations,
    jdsl: KotlinJdslJpqlExecutor,
) : EntityManager by em, KotlinJdslJpqlExecutor by jdsl, JpaTestSpec({

    val srv = TaskServiceImpl(accRepo, teamRepo, taskRepo, txOps, mockk())

    test("get task") {
        // GIVEN
        val team = Team::title.eq("bandits")
        val users = Account::userName.run { listOf(eq("serge"), eq("vi")) }
        val heroes = users.map { it.hero(team) ?: error(it.userName) }
        val target = taskDto(team.dto, users[0].hero, users[1].hero).also {
            merge(it.toTask(team, heroes[0], heroes[1]))
        }

        // WHEN
        val actual = srv.getTask(GetTaskDto(target.title, team.title))

        // THEN
        actual shouldBeRight target
    }

    test("get task, no such task") {
        // GIVEN
        val taskTitle = rndStr()
        val team = merge(Team(0, rnd()))

        // WHEN
        val actual = srv.getTask(GetTaskDto(taskTitle, team.title))

        // THEN
        actual shouldBeLeft Lack("Task.title", taskTitle, "not found")
    }

    test("get task, no such team") {
        // WHEN
        val teamTitle = rndStr()
        val actual = srv.getTask(GetTaskDto(rnd(), teamTitle))

        // THEN
        actual shouldBeLeft Lack("Team.title", teamTitle, "not found")
    }

    test("create task") {
        // GIVEN
        val team = Team::title.eq("bandits")
        val (chief, hero) = "serge" to "vi"
        val target = taskDto(TeamDto(team.title), HeroDto(chief), HeroDto(hero), Open, ZERO)

        // WHEN
        val actual = srv.createTask(target.toCreateTaskDto(team, chief, hero))

        // THEN
        actual.shouldBeRight().let {
            it.createdAt shouldBeGreaterThanOrEqualTo target.createdAt
            setField(it, "createdAt", target.createdAt)
            it shouldBe target
        }
    }

    test("update task status") {
        // GIVEN
        val team = Team::title.eq("police")
        val hero = Account::userName.eq("alex").hero(team) ?: error("alex")
        val task = merge(task(team, hero, hero, Open))
        reset()

        // WHEN
        val actual = srv.updateTaskStatus(task.title, UpdateTaskStatusDto(team.title, Work))

        // THEN
        actual shouldBeRight Unit
        all<Task>().shouldBeSingleton { it.status shouldBe Work }
    }

    test("log spent") {
        // GIVEN
        val team = Team::title.eq("bandits")
        val users = Account::userName.run { listOf(eq("serge"), eq("vi")) }
        val heroes = users.map { it.hero(team) ?: error(it.userName) }
        val (spentTotal, spent1, spent2) = Triple(ofHours(2),  ofMinutes(45), ofMinutes(33))
        val task = merge(task(team, heroes[0], heroes[1], Work, spentTotal - spent1 - spent2))
        reset()

        // WHEN
        val actual = srv.logSpent(task.title, LogSpentDto(team.title, users[0].userName, "$spent1"))
            .onRight { reset() }
            .flatMap { srv.logSpent(task.title, LogSpentDto(team.title, users[1].userName, "$spent2")) }
            .onRight { reset() }

        // THEN
        actual shouldBeRight Unit
        all<Task>(Task::log).shouldBeSingleton {
            it.spentTotal shouldBe spentTotal
            it.log.map { it.spent }.shouldContainExactly(spent1, spent2)
        }
    }

    // TODO more tests
})
