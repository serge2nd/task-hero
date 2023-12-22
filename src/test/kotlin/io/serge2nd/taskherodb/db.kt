package io.serge2nd.taskherodb

import io.serge2nd.taskhero.db.Hero
import io.serge2nd.taskhero.db.Task
import io.serge2nd.taskhero.db.Team
import io.serge2nd.taskhero.dto.TaskDto
import io.serge2nd.taskhero.enums.TaskStatus
import java.time.Duration

fun task(
    team: Team,
    chief: Hero,
    hero: Hero,
    status: TaskStatus = rnd(),
    spentTotal: Duration = rnd()
) = Task(0, rnd(), rnd(), rnd(), rnd(), rnd(), team, chief, hero, status, spentTotal)

fun TaskDto.toTask(team: Team, chief: Hero, hero: Hero) =
    Task(0, title, details, dueDate, cost, priority, team, chief, hero, status, spentTotal, createdAt)
