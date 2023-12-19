package io.serge2nd.taskhero.dto

import io.serge2nd.taskhero.enums.TaskStatus
import io.serge2nd.taskhero.etc.Iso8601HoursMinutesPositive
import io.serge2nd.taskhero.etc.NonNull
import io.serge2nd.taskhero.etc.none
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Past
import java.time.Duration
import java.time.OffsetDateTime

data class AccountDto(val userName: String)

data class HeroDto(val account: AccountDto) { constructor(userName: String) : this(AccountDto(userName)) }

data class TeamDto(val title: String)

data class TaskSpentDto(val hero: HeroDto, val spent: Duration, val addedAt: OffsetDateTime)

data class GetAccountTaskLogDto(
    @get:NotBlank @NonNull val userName: String = none(GetAccountTaskLogDto::userName),
    @get:Past @NonNull val logFrom: OffsetDateTime = none(GetAccountTaskLogDto::logFrom),
)

data class GetTaskDto(
    @get:NotBlank @NonNull val title: String = none(GetTaskDto::title),
    @get:NotBlank @NonNull val team: String = none(GetTaskDto::team),
)

data class ListTasksDto(
    @get:NotEmpty @NonNull val teams: Set<@NotBlank String> = none(ListTasksDto::teams)
)

data class UpdateTaskStatusDto(
    @get:NotBlank val team: String = none(UpdateTaskStatusDto::team),
    val status: TaskStatus = none(UpdateTaskStatusDto::status),
)

data class LogSpentDto(
    @get:NotBlank val team: String = none(LogSpentDto::team),
    @get:NotBlank val userName: String = none(LogSpentDto::userName),
    @get:Iso8601HoursMinutesPositive val spent: String = none(LogSpentDto::spent),
)
