package com.gshoaib998.progressly.model

data class Goal(
    val id: Long = 0L,
    val createdAt: Long,
    val title: String,
    val description: String,
    val isCompleted: Boolean,
    val expectedCompletionDate: Long,
    val completionDate: Long,
    val difficultyLevel: DifficultyLevel,
    val importanceLevel: ImportanceLevel,
    val urgencyLevel: UrgencyLevel
)

enum class DifficultyLevel{
    HARD, MEDIUM, EASY
}

enum class ImportanceLevel {
    VERY_IMP, IMPORTANT, AVERAGE
}

enum class UrgencyLevel {
    URGENT, AVERAGE, NOT_URG
}

