package io.serge2nd.taskhero.dto

import io.serge2nd.taskhero.enums.TaskPriority
import io.serge2nd.taskhero.enums.TaskStatus
import java.time.Duration
import java.time.LocalDate
import java.time.OffsetDateTime

data class TaskDto(
    val title: String,
    val details: String,
    val dueDate: LocalDate,
    val cost: Duration,
    val priority: TaskPriority,
    val team: TeamDto,
    val chief: HeroDto,
    val hero: HeroDto,
    val status: TaskStatus,
    val spentTotal: Duration,
    val createdAt: OffsetDateTime,
    val log: List<TaskSpentDto>,
)
