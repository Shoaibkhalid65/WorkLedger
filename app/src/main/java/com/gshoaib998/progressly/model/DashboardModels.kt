package com.gshoaib998.progressly.model

data class DailyDurationTotal(
    val dateEpoch: Long,
    val totalMillis: Long
)

data class DailySatisfactionAvg(
    val dateEpoch: Long,
    val avgPercent: Float
)

data class TodaySummary(
    val totalHours: Float,       // e.g. 2.5
    val tasksDone: Int,
    val avgSatisfaction: Float   // 0..100
)

