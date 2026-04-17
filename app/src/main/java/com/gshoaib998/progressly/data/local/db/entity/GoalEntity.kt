package com.gshoaib998.progressly.data.local.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "goals")
data class GoalEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val createdAt: Long,
    val title: String,
    val description: String,
    val isCompleted: Boolean,
    val expectedCompletionDate: Long,
    val completionDate: Long,
    val difficultyLevel: String,
    val importanceLevel: String,
    val urgencyLevel: String

)