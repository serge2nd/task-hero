package io.serge2nd.taskherodb

import io.serge2nd.taskhero.db.Account
import io.serge2nd.taskhero.db.Team
import io.serge2nd.taskhero.dto.*
import io.serge2nd.taskhero.enums.TaskStatus
import java.time.Duration
import java.time.OffsetDateTime.now

val Team.dto get() = TeamDto(title)

val Account.hero get() = HeroDto(userName)

fun taskDto(
    team: TeamDto = rnd(),
    chief: HeroDto = rnd(),
    hero: HeroDto = rnd(),
    status: TaskStatus = rnd(),
    spentTotal: Duration = rnd()
) = TaskDto(rnd(), rnd(), rnd(), rnd(), rnd(), team, chief, hero, status, spentTotal, now(), emptyList())

fun TaskDto.toCreateTaskDto(team: Team, chief: String, hero: String) =
    CreateTaskDto(title, details, dueDate, "$cost", priority, team.title, chief, hero)
