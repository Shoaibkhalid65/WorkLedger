package com.gshoaib998.progressly.data.repository

import com.gshoaib998.progressly.data.local.db.dao.DailyTaskDao
import com.gshoaib998.progressly.data.local.db.dao.TaskDurationDao
import com.gshoaib998.progressly.model.DailyDurationTotal
import com.gshoaib998.progressly.model.DailySatisfactionAvg
import com.gshoaib998.progressly.model.TodaySummary
import com.gshoaib998.progressly.utils.DateTimeUtils
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

