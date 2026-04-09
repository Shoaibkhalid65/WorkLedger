package com.example.progresstracker.data.repository

import com.example.progresstracker.data.local.db.dao.DailyTaskDao
import com.example.progresstracker.data.local.db.dao.TaskDurationDao
import com.example.progresstracker.model.DailyDurationTotal
import com.example.progresstracker.model.DailySatisfactionAvg
import com.example.progresstracker.model.TodaySummary
import com.example.progresstracker.utils.DateTimeUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DashboardRepository @Inject constructor(
    private val taskDurationDao: TaskDurationDao,
    private val dailyTaskDao: DailyTaskDao
) {

    fun observeDailyDurationTotals(): Flow<List<DailyDurationTotal>> =
        taskDurationDao.getDailyDurationTotals()

    fun observeDailySatisfactionAverages(): Flow<List<DailySatisfactionAvg>> =
        dailyTaskDao.getDailySatisfactionAverage()

    fun observeTodaySummary(): Flow<TodaySummary> {
        val todayEpoch = DateTimeUtils.toMidnightEpoch(System.currentTimeMillis())
        return combine(
            taskDurationDao.getDailyDurationTotals(),
            dailyTaskDao.getDailySatisfactionAverage()
        ) { durations, satisfaction ->
            val todayDuration = durations.firstOrNull { it.dateEpoch == todayEpoch }
            val todaySatisfaction = satisfaction.firstOrNull { it.dateEpoch == todayEpoch }
            val sessionCount = taskDurationDao.getSessionCountForDay(todayEpoch)
            TodaySummary(
                totalHours = ((todayDuration?.totalMillis ?: 0L) / 3_600_000f).coerceAtLeast(0f),
                tasksDone = sessionCount,  // ← actual session count now
                avgSatisfaction = todaySatisfaction?.avgPercent ?: 0f
            )
        }
    }
}

