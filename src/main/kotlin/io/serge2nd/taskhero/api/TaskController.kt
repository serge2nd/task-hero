package io.serge2nd.taskhero.api

import io.serge2nd.taskhero.dto.*
import io.serge2nd.taskhero.service.TaskService
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.enums.ParameterIn.PATH
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import org.springdoc.core.annotations.ParameterObject
import org.springframework.http.HttpStatus.CREATED
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE as JSON

@RestController
class TaskController(private val service: TaskService) {

    @GetMapping("/accounts/{userName}/task-log", produces = [JSON])
    @Parameter(name = "userName", `in` = PATH)
    suspend fun getAccountTaskLog(@Valid @ParameterObject rq: GetAccountTaskLogDto) =
        service.getAccountTaskLog(rq).toResponse()

    @GetMapping("/tasks/{title}", produces = [JSON])
    @Parameter(name = "title", `in` = PATH)
    suspend fun getTask(@Valid @ParameterObject rq: GetTaskDto) =
        service.getTask(rq).toResponse()

    @GetMapping("/tasks", produces = [JSON])
    suspend fun listTasks(@Valid @ParameterObject rq: ListTasksDto) =
        service.listTasks(rq).toResponse()

    @GetMapping("/task-stats", produces = [JSON])
    suspend fun getTaskStats(@Valid @ParameterObject rq: ListTasksDto) =
        service.getTaskStats(rq).toResponse()

    @PostMapping("/tasks", consumes = [JSON], produces = [JSON])
    suspend fun createTask(@Valid @RequestBody rq: CreateTaskDto) =
        service.createTask(rq).toResponse(CREATED)

    @PutMapping("/tasks/{title}/status", consumes = [JSON])
    suspend fun updateTaskStatus(
        @NotBlank @PathVariable title: String,
        @Valid @RequestBody rq: UpdateTaskStatusDto
    ) = service.updateTaskStatus(title, rq).toResponse()

    @PutMapping("/task/{title}/log-spent", consumes = [JSON])
    suspend fun logSpent(
        @NotBlank @PathVariable title: String,
        @Valid @RequestBody rq: LogSpentDto
    ) = service.logSpent(title, rq).toResponse()
}
