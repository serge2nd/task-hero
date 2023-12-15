package io.serge2nd.taskherodb.service

import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.kotest.matchers.types.shouldNotBeInstanceOf
import io.serge2nd.taskhero.db.*
import io.serge2nd.taskhero.dto.GetTaskDto
import io.serge2nd.taskhero.dto.HeroDto
import io.serge2nd.taskhero.dto.TaskDto
import io.serge2nd.taskhero.dto.TeamDto
import io.serge2nd.taskhero.enums.TaskPriority
import io.serge2nd.taskhero.enums.TaskStatus
import io.serge2nd.taskhero.service.ServiceError
import io.serge2nd.taskhero.service.ServiceError.NotFound
import io.serge2nd.taskhero.service.TaskServiceImpl
import io.serge2nd.taskherodb.config.JpaAppTest
import jakarta.persistence.EntityManager
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional
import java.time.Duration
import java.time.LocalDate
import java.time.OffsetDateTime

/**
 * Strictly, that's not a "canonical" unit test,
 * but it verifies a service as a unit covering transactional query accuracy as well
 * and being not so trivial as it would be with repository mocks.
 */
@JpaAppTest
@Transactional
@ActiveProfiles("dev", "test")
internal class TaskServiceImplTest(
    val accRepo: Accounts,
    val teamRepo: Teams,
    val taskRepo: Tasks,
    val em: EntityManager,
) : FunSpec({

    val srv = TaskServiceImpl(accRepo, teamRepo, taskRepo)

    test("get task") {
        // GIVEN
        val team = teamRepo.findByTitle("police")!!
        val acc = accRepo.findByUserName("alex")!!
        val hero = acc.heroesByTeamId[team.id]!!
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

    // TODO other tests
})
