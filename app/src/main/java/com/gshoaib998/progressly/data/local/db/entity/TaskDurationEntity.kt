package com.gshoaib998.progressly.data.local.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "task_durations",
    indices = [Index(value = ["dailyTaskId"])],
    foreignKeys = [ForeignKey(
        entity = DailyTaskEntity::class,
        parentColumns = ["id"],
        childColumns = ["dailyTaskId"],
        onDelete = ForeignKey.Companion.CASCADE
    )]
)
data class TaskDurationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val dailyTaskId: Long,
    val startTime: Long,
    val endTime: Long,
    val durationTime: Long,
    val dateEpoch: Long = 0L
)