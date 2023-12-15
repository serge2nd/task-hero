package io.serge2nd.taskhero.service

import arrow.core.Either
import io.serge2nd.taskhero.dto.*

interface TaskService {

    /**
     * Gets a task spent time log for the given user starting from the given time instant
     */
    suspend fun getAccountTaskLog(rq: GetAccountTaskLogDto): Either<ServiceError, AccountTaskLogDto>

    /**
     * Finds a task by the given team title & task title
     */
    suspend fun getTask(rq: GetTaskDto): Either<ServiceError, TaskDto>

    /**
     * Finds tasks by the given team titles
     */
    suspend fun listTasks(rq: ListTasksDto): Either<ServiceError, List<TaskDto>>

    /**
     * Creates a task with the given parameters
     */
    suspend fun createTask(rq: CreateTaskDto): Either<ServiceError, TaskDto>

    /**
     * Updates task status to
     * [Work][io.serge2nd.taskhero.enums.TaskStatus.Work]
     * or [Done][io.serge2nd.taskhero.enums.TaskStatus.Done]
     */
    suspend fun updateTaskStatus(title: String, rq: UpdateTaskStatusDto): Either<ServiceError, Unit>

    /**
     * Logs time spent on the given task by the given user
     */
    suspend fun logSpent(title: String, rq: LogSpentDto): Either<ServiceError, Unit>
}
