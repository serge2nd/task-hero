package io.serge2nd.taskhero.dto

import io.serge2nd.taskhero.enums.TaskStatus
import io.serge2nd.taskhero.etc.Iso8601HoursMinutes
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Past
import org.springframework.lang.NonNull
import java.time.Duration
import java.time.OffsetDateTime

data class AccountDto(val userName: String)

data class HeroDto(val account: AccountDto) { constructor(userName: String) : this(AccountDto(userName)) }

data class TeamDto(val title: String)

data class TaskSpentDto(val hero: HeroDto, val spent: Duration, val addedAt: OffsetDateTime)

data class GetAccountTaskLogDto(
    @get:NotBlank @field:NonNull val userName: String,
    @get:Past @field:NonNull val logFrom: OffsetDateTime,
)

data class GetTaskDto(
    @get:NotBlank @field:NonNull val title: String,
    @get:NotBlank @field:NonNull val team: String,
)

data class ListTasksDto(
    @get:NotEmpty @field:NonNull val teams: Set<@NotBlank String>
)

data class UpdateTaskStatusDto(
    @get:NotBlank @field:NonNull val team: String,
    @field:NonNull val status: TaskStatus,
)

data class LogSpentDto(
    @get:NotBlank @field:NonNull val team: String,
    @get:NotBlank @field:NonNull val userName: String,
    @get:Iso8601HoursMinutes @field:NonNull val spent: String,
)
