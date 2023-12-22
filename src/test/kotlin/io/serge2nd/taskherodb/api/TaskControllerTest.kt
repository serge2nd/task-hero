package io.serge2nd.taskherodb.api

import arrow.core.right
import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.serge2nd.taskhero.api.ErrorHandler.FormatError
import io.serge2nd.taskhero.dto.*
import io.serge2nd.taskhero.service.TaskService
import io.serge2nd.taskherodb.asList
import io.serge2nd.taskherodb.config.WebFluxAppTest
import io.serge2nd.taskherodb.rndStr
import io.serge2nd.taskherodb.taskDto
import io.serge2nd.taskherodb.url
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.OK
import org.springframework.test.web.reactive.server.WebTestClient

@WebFluxAppTest
internal class TaskControllerTest(
    web: WebTestClient,
    taskSrvMock: TaskService,
) : FunSpec({

    test("list tasks") {
        // GIVEN
        val teams = listOf(rndStr(), rndStr()).mapTo(HashSet()) { it.replace('+', ' ') }
        val tasks = listOf(taskDto(), taskDto())
        coEvery { taskSrvMock.listTasks(ListTasksDto(teams)) } returns tasks.right()

        // WHEN
        val rs = web.get().url("/tasks", "teams" to teams).asList<TaskDto>()

        /* THEN */ assertSoftly(rs) {
            status shouldBe OK
            responseBody shouldBe tasks
        }
    }

    test("list tasks, null teams") {
        // WHEN
        val rs = web.get().uri("/tasks").asList<FormatError>()

        /* THEN */ assertSoftly(rs) {
            status shouldBe BAD_REQUEST
            responseBody shouldBe FormatError("teams", null, "must not be null").let(::listOf)
        }
    }

    test("list tasks, empty teams") {
        // WHEN
        val rs = web.get().uri("/tasks?teams=").asList<FormatError>()

        /* THEN */ assertSoftly(rs) {
            status shouldBe BAD_REQUEST
            responseBody shouldBe FormatError("teams", emptyList<Any>(), "must not be empty").let(::listOf)
        }
    }

    test("list tasks, blank team") {
        // WHEN
        val rs = web.get().uri("/tasks?teams=++").asList<FormatError>()

        /* THEN */ assertSoftly(rs) {
            status shouldBe BAD_REQUEST
            responseBody shouldBe FormatError("teams[]", "", "must not be blank").let(::listOf)
        }
    }

    // TODO more tests
})
