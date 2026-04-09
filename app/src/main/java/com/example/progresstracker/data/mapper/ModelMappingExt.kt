package com.example.progresstracker.data.mapper

import com.example.progresstracker.data.local.db.entity.DailyTaskEntity
import com.example.progresstracker.data.local.db.entity.GoalEntity
import com.example.progresstracker.data.local.db.entity.TaskDurationEntity
import com.example.progresstracker.model.DailyTask
import com.example.progresstracker.model.DifficultyLevel
import com.example.progresstracker.model.Goal
import com.example.progresstracker.model.ImportanceLevel
import com.example.progresstracker.model.SatisfyPercentage
import com.example.progresstracker.model.TaskDuration
import com.example.progresstracker.model.UrgencyLevel
import com.example.progresstracker.utils.DateTimeUtils

fun DailyTaskEntity.toModel(durations: List<TaskDurationEntity>): DailyTask = DailyTask(
    id = id,
    title = title,
    description = description,
    remarks = remarks,
    satisfyPercentage = when (satisfyPercentage) {
        0 -> SatisfyPercentage.PER_0
        10 -> SatisfyPercentage.PER_10
        20 -> SatisfyPercentage.PER_20
        30 -> SatisfyPercentage.PER_30
        40 -> SatisfyPercentage.PER_40
        50 -> SatisfyPercentage.PER_50
        60 -> SatisfyPercentage.PER_60
        70 -> SatisfyPercentage.PER_70
        80 -> SatisfyPercentage.PER_80
        90 -> SatisfyPercentage.PER_90
        else -> SatisfyPercentage.PER_100
    },
    englishDate = englishDate,
    durations = durations.map { it.toModel() }
)


fun DailyTask.toEntity() = DailyTaskEntity(
    id = id,
    title = title,
    description = description,
    remarks = remarks,
    satisfyPercentage = satisfyPercentage.text,
    englishDate = englishDate,
)


fun TaskDuration.toEntity(dailyTaskId: Long) = TaskDurationEntity(
    id = id,
    dailyTaskId = dailyTaskId,
    startTime = startTime,
    endTime = endTime,
    durationTime = durationTime,
    dateEpoch = DateTimeUtils.toMidnightEpoch(startTime)
)

fun TaskDurationEntity.toModel() = TaskDuration(
    id = id,
    startTime = startTime,
    endTime = endTime
)

@JvmName("durationEntitiesToDurations")
fun List<TaskDurationEntity>.toModel()= map {
    it.toModel()
}



fun Goal.toEntity() = GoalEntity(
    id = id,
    createdAt = createdAt,
    title = title,
    description = description,
    isCompleted = isCompleted,
    expectedCompletionDate = expectedCompletionDate,
    completionDate = completionDate,
    difficultyLevel = difficultyLevel.name,
    importanceLevel = importanceLevel.name,
    urgencyLevel = urgencyLevel.name
)

fun GoalEntity.toModel() = Goal(
    id = id,
    createdAt = createdAt,
    title = title,
    description = description,
    isCompleted = isCompleted,
    expectedCompletionDate = expectedCompletionDate,
    completionDate = completionDate,
    difficultyLevel = when (difficultyLevel.uppercase()) {
        "HARD" -> DifficultyLevel.HARD
        "MEDIUM" -> DifficultyLevel.MEDIUM
        "EASY" -> DifficultyLevel.EASY
        else -> DifficultyLevel.EASY
    },
    importanceLevel = when (importanceLevel.uppercase()) {
        "VERY_IMP" -> ImportanceLevel.VERY_IMP
        "IMPORTANT" -> ImportanceLevel.IMPORTANT
        "AVERAGE" -> ImportanceLevel.AVERAGE
        else -> ImportanceLevel.AVERAGE
    },
    urgencyLevel = when (urgencyLevel.uppercase()) {
        "URGENT" -> UrgencyLevel.URGENT
        "AVERAGE" -> UrgencyLevel.AVERAGE
        "NOT_URG" -> UrgencyLevel.NOT_URG
        else -> UrgencyLevel.NOT_URG
    }
)

@JvmName("goalEntitiesToGoals")
fun List<GoalEntity>.toModel()= map {
    it.toModel()
}

