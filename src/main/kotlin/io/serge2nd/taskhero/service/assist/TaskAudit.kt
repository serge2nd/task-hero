package io.serge2nd.taskhero.service.assist

import io.serge2nd.taskhero.dto.TaskDto
import io.serge2nd.taskhero.dto.TaskStatsDto
import io.serge2nd.taskhero.enums.TaskStatus.Done
import io.serge2nd.taskhero.enums.TaskStatus.Open
import io.serge2nd.taskhero.etc.durationSum
import org.springframework.stereotype.Component
import java.time.Duration.ofMinutes

@Component
class TaskAudit {

    fun buildStats(tasks: List<TaskDto>) = run {
        val open = tasks.filter { it.status == Open }
        val done = tasks.filter { it.status == Done }
        val doneSpent = done.map { it.spentTotal }
        val doneSpentSum = doneSpent.durationSum()
        TaskStatsDto(
            count = tasks.size,
            countByStatus = tasks.groupingBy { it.status }.eachCount(),
            doneTasksRatio = if (done.isNotEmpty()) done.size / tasks.size.toDouble() else 0.0,
            openCost = open.map { it.cost }.durationSum(),
            timeRemaining = tasks.map { it.cost }.durationSum() - tasks.map { it.spentTotal }.durationSum(),
            spentToFinish = doneSpentSum,
            spentToFinishMean = ofMinutes(if (doneSpent.isNotEmpty()) doneSpentSum.toMinutes() / doneSpent.size else 0),
            underCostRatioMean = done.map { it.spentTotal.toMinutes() / it.cost.toMinutes().toDouble() }.average()
        )
    }
}
