package io.serge2nd.taskhero.dto

import io.serge2nd.taskhero.enums.TaskPriority
import io.serge2nd.taskhero.enums.TaskStatus
import java.time.Duration
import java.time.LocalDate
import java.time.OffsetDateTime

data class AccountTaskLogDto(
    val log: List<Spent>,
    val tasks: List<TaskExpanded>,
) {

    data class Spent(
        val task: Task,
        val spent: Duration,
        val addedAt: OffsetDateTime,
    ) {
        constructor(title: String, team: String, spent: Duration, addedAt: OffsetDateTime)
            : this(Task(title, TeamDto(team)), spent, addedAt)
    }

    data class Task(val title: String, val team: TeamDto)

    data class TaskExpanded(
        val title: String,
        val dueDate: LocalDate,
        val cost: Duration,
        val priority: TaskPriority,
        val team: TeamDto,
        val status: TaskStatus,
        val spentTotal: Duration,
        val createdAt: OffsetDateTime,
    )
}
