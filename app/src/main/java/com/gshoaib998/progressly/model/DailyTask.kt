package com.gshoaib998.progressly.model

import com.gshoaib998.progressly.utils.DateTimeUtils

data class DailyTask(
    val id: Long = 0L,
    val title: String,
    val description: String,
    val remarks: String,
    val satisfyPercentage: SatisfyPercentage,
    val englishDate: Long,
    val durations: List<TaskDuration>
) {
    val islamicDate: String
        get() = DateTimeUtils.calculateIslamicDate(englishDate)

    val totalTaskDuration =
        if(durations.isNotEmpty()) durations.map { it.durationTime }.reduce { acc, duration -> acc + duration } else 0
}

enum class SatisfyPercentage(val text: Int) {
    PER_0(0),
    PER_10(10),
    PER_20(20),
    PER_30(30),
    PER_40(40),
    PER_50(50),
    PER_60(60),
    PER_70(70),
    PER_80(80),
    PER_90(90),
    PER_100(100),
}








