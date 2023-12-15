package io.serge2nd.taskhero.dto

import io.serge2nd.taskhero.enums.TaskStatus
import java.time.Duration

data class TaskStatsDto(
    val count: Int,
    val countByStatus: Map<TaskStatus, Int>,
    val doneTasksRatio: Double,
    /** The cost of opened tasks */
    val openCost: Duration,
    /** The diff between total cost and total spent */
    val timeRemaining: Duration,
    /** The time spent on finished tasks */
    val spentToFinish: Duration,
    /** The average time spent on finished tasks */
    val spentToFinishMean : Duration,
    /**
     * The average ratio between spent time and cost on a finished task
     * (below 1 - overestimate, above 1 - underestimate)
     */
    val underCostRatioMean: Double
)
