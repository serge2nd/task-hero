package io.serge2nd.taskhero.dto

import io.serge2nd.taskhero.enums.TaskPriority
import io.serge2nd.taskhero.etc.Iso8601HoursMinutesPositive
import io.serge2nd.taskhero.etc.none
import jakarta.validation.constraints.Future
import jakarta.validation.constraints.NotBlank
import java.time.LocalDate
import io.serge2nd.taskhero.dto.CreateTaskDto as T

data class CreateTaskDto(
    @get:NotBlank val title: String = none(T::title),
    @get:NotBlank val details: String = none(T::details),
    @get:Future val dueDate: LocalDate = none(T::dueDate),
    @get:Iso8601HoursMinutesPositive val cost: String = none(T::cost),
    val priority: TaskPriority = none(T::priority),
    @get:NotBlank val team: String = none(T::team),
    @get:NotBlank val chiefUser: String = none(T::chiefUser),
    @get:NotBlank val heroUser: String = none(T::heroUser),
)
