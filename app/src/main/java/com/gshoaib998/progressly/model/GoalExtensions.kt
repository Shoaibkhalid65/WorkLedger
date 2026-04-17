package com.gshoaib998.progressly.model

fun DifficultyLevel.toLabel() = when (this) {
    DifficultyLevel.EASY   -> "Easy"
    DifficultyLevel.MEDIUM -> "Medium"
    DifficultyLevel.HARD   -> "Hard"
}

fun ImportanceLevel.toLabel() = when (this) {
    ImportanceLevel.AVERAGE        -> "Average importance"
    ImportanceLevel.IMPORTANT      -> "Important"
    ImportanceLevel.VERY_IMP -> "Very important"
}

fun UrgencyLevel.toLabel() = when (this) {
    UrgencyLevel.NOT_URG -> "Not urgent"
    UrgencyLevel.AVERAGE    -> "Average urgency"
    UrgencyLevel.URGENT     -> "Urgent"
}

// Maps ordinal 0 (highest severity) → red, 2 (lowest) → green
fun DifficultyLevel.badgeColor(): GoalBadgeColor = when (this) {
    DifficultyLevel.HARD   -> GoalBadgeColor.RED
    DifficultyLevel.MEDIUM -> GoalBadgeColor.AMBER
    DifficultyLevel.EASY   -> GoalBadgeColor.GREEN
}

fun ImportanceLevel.badgeColor(): GoalBadgeColor = when (this) {
    ImportanceLevel.VERY_IMP-> GoalBadgeColor.RED
    ImportanceLevel.IMPORTANT      -> GoalBadgeColor.AMBER
    ImportanceLevel.AVERAGE        -> GoalBadgeColor.GRAY
}

fun UrgencyLevel.badgeColor(): GoalBadgeColor = when (this) {
    UrgencyLevel.URGENT     -> GoalBadgeColor.RED
    UrgencyLevel.AVERAGE    -> GoalBadgeColor.AMBER
    UrgencyLevel.NOT_URG-> GoalBadgeColor.BLUE
}

enum class GoalBadgeColor { RED, AMBER, GREEN, BLUE, GRAY }