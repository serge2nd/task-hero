package io.serge2nd.taskhero.dto

import io.serge2nd.taskhero.enums.TaskPriority
import io.serge2nd.taskhero.etc.Iso8601HoursMinutes
import jakarta.validation.constraints.Future
import jakarta.validation.constraints.NotBlank
import java.time.LocalDate

data class CreateTaskDto(
    @get:NotBlank val title: String,
    @get:NotBlank val details: String,
    @get:Future val dueDate: LocalDate,
    @get:Iso8601HoursMinutes val cost: String,
    val priority: TaskPriority,
    @get:NotBlank val team: String,
    @get:NotBlank val chiefUser: String,
    @get:NotBlank val heroUser: String,
)
