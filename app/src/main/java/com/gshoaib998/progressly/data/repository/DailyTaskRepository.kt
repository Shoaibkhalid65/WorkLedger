package com.gshoaib998.progressly.data.repository

import com.gshoaib998.progressly.data.local.db.dao.DailyTaskDao
import com.gshoaib998.progressly.data.local.db.dao.TaskDurationDao
import com.gshoaib998.progressly.data.mapper.toEntity
import com.gshoaib998.progressly.data.mapper.toModel
import com.gshoaib998.progressly.di.ApplicationScope
import com.gshoaib998.progressly.di.DefaultDispatcher
import com.gshoaib998.progressly.model.DailyTask
import com.gshoaib998.progressly.model.SatisfyPercentage
import com.gshoaib998.progressly.model.TaskDuration
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DailyTaskRepository @Inject constructor(
    private val dailyTaskDao: DailyTaskDao,
    private val taskDurationDao: TaskDurationDao,
    @param:ApplicationScope private val scope: CoroutineScope,
    @param:DefaultDispatcher private val dispatcher: CoroutineDispatcher,
) {


    // duration methods
    suspend fun createOrUpdateTaskDuration(taskDuration: TaskDuration): Result<Long> {
        return try {
            var id = -1L
            val maxTaskId = getMaxTaskId().first()
            val lastStoredTask = dailyTaskDao.getDailyTaskById(maxTaskId)
            if (lastStoredTask == null || lastStoredTask.title.isNotEmpty()) {
                dailyTaskDao.upsertDailyTask(
                    DailyTask(
                        id = 0L,
                        title = "",
                        description = "",
                        remarks = "",
                        satisfyPercentage = SatisfyPercentage.PER_0,
                        durations = emptyList(),
                        englishDate = 0L
                    ).toEntity()
                )
            }

            id =
                taskDurationDao.upsertTaskDuration(taskDuration.toEntity(getMaxTaskId().first()))

            if (id == -1L) {
                Result.failure(Throwable("Duration not created"))
            } else {
                Result.success(id)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteTaskDuration(taskDuration: TaskDuration): Result<Int> {
        return try {
            val maxId = getMaxTaskId().first()
            val id = taskDurationDao.deleteTaskDuration(taskDuration.toEntity(maxId))
            Result.success(id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteAllTaskDurationsOfMaxId(): Result<Int> {
        val maxId = getMaxTaskId().first()
        return try {
            val rowsDeleted = taskDurationDao.deleteDurationsByTaskId(maxId)
            Result.success(rowsDeleted)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun observeAllDurations(): Flow<List<TaskDuration>> {
        return taskDurationDao.getAllDurations()
            .map { it.toModel() }
            .flowOn(dispatcher)
    }

    fun observeDurationById(durationId: Long): Flow<TaskDuration> {
        return taskDurationDao.getDurationById(durationId).map { it.toModel() }
    }

    fun observeDurationsByTaskId(taskId: Long): Flow<List<TaskDuration>> {
        return taskDurationDao.getAllDurationsByTaskId(taskId)
            .map { it.toModel() }
            .flowOn(dispatcher)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun observeDurationsForMaxTaskIdToShow(): Flow<List<TaskDuration>> {
        return dailyTaskDao.getMaxId().flatMapLatest { taskId ->
            taskDurationDao.getAllDurationsByTaskId(taskId)
                .map { entities ->
                    val dailyTask = dailyTaskDao.getDailyTaskById(taskId)
                    if (dailyTask != null && dailyTask.englishDate == 0L) {
                        withContext(dispatcher) { entities.toModel() }
                    } else {
                        emptyList()
                    }
                }
        }
    }


    //    daily task methods

    fun observeAllRealTasks(): Flow<List<DailyTask>> {
        return combine(
            dailyTaskDao.getRealDailyTasks(),
            taskDurationDao.getAllDurationsForRealTasks()
        ) { tasks, durations ->
            val durationsByTaskId = durations.groupBy { it.dailyTaskId }
            tasks.map { taskEntity ->
                taskEntity.toModel(durationsByTaskId[taskEntity.id] ?: emptyList())
            }
        }.flowOn(dispatcher)
    }

    suspend fun getDailyTaskById(taskId: Long): DailyTask? {
        return dailyTaskDao.getDailyTaskById(taskId)?.let { entity ->
            entity.toModel(observeDurationsByTaskId(taskId).map { durations ->
                durations.map {
                    it.toEntity(taskId)
                }
            }.first())
        }
    }

    suspend fun createOrUpdateDailyTask(dailyTask: DailyTask): Result<Long> {
        return try {
            val id = dailyTaskDao.upsertDailyTask(dailyTask.toEntity())
            Result.success(id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

//    suspend fun createOrUpdateDailyTaskWithDurations(dailyTask: DailyTask): Result<Long> {
//        return try {
//            var id = -1L
//            val maxTaskId = getMaxTaskId().first()
//            val lastStoredTask = dailyTaskDao.getDailyTaskById(maxTaskId)
//            id = if (lastStoredTask != null && lastStoredTask.title.isEmpty()) {
//                dailyTaskDao.upsertDailyTask(
//                    dailyTask.toEntity().copy(id = maxTaskId)
//                )
//                maxTaskId
//            } else if (dailyTask.id != 0L) {
//                dailyTaskDao.upsertDailyTask(dailyTask.toEntity())
//                dailyTask.id
//            } else {
//                dailyTaskDao.upsertDailyTask(dailyTask.toEntity())
//            }
//            taskDurationDao.deleteDurationsByTaskId(id)
//
//            val durationsToCreate = dailyTask.durations
//                .filter { it.startTime != 0L && it.endTime != 0L }
//            if (durationsToCreate.isNotEmpty()) {
//                taskDurationDao.upsertAllTaskDurations(
//                    durationsToCreate.map { it.toEntity(id) }
//                )
//            }
//            Result.success(id)
//        } catch (e: Exception) {
//            Result.failure(e)
//        }
//    }
suspend fun createOrUpdateDailyTaskWithDurations(dailyTask: DailyTask): Result<Long> {
    return try {
        val taskId: Long

        if (dailyTask.id != 0L) {
            // ── EDIT PATH ────────────────────────────────────────────
            // This is an existing real task. Upsert it directly.
            // Never look at maxTaskId — the dummy task is irrelevant here.
            dailyTaskDao.upsertDailyTask(dailyTask.toEntity())
            taskId = dailyTask.id
        } else {
            // ── CREATE PATH ──────────────────────────────────────────
            // New task being created. Check if there is a dummy task
            // waiting to be claimed (created by a foreground duration session).
            val maxTaskId = getMaxTaskId().first()
            val lastStoredTask = dailyTaskDao.getDailyTaskById(maxTaskId)

            taskId = if (lastStoredTask != null && lastStoredTask.title.isEmpty()) {
                // Claim the dummy task — overwrite it with the real task data
                dailyTaskDao.upsertDailyTask(dailyTask.toEntity().copy(id = maxTaskId))
                maxTaskId
            } else {
                // No dummy task exists — insert as a brand new task
                dailyTaskDao.upsertDailyTask(dailyTask.toEntity())
            }
        }

        // Delete old durations for this task, then re-insert current ones.
        // On the EDIT path this correctly removes orphaned durations.
        // On the CREATE path the dummy task had durations we must NOT delete
        // if we claimed it — so only delete/reinsert if the task has UI durations.
        if (dailyTask.id != 0L) {
            // Edit: always delete and reinsert to handle removed durations
            taskDurationDao.deleteDurationsByTaskId(taskId)
            val durationsToSave = dailyTask.durations
                .filter { it.startTime != 0L && it.endTime != 0L }
            if (durationsToSave.isNotEmpty()) {
                taskDurationDao.upsertAllTaskDurations(
                    durationsToSave.map { it.toEntity(taskId) }
                )
            }
        } else {
            // Create: only upsert UI durations on top of any existing
            // foreground-service durations already attached to this taskId
            val durationsToSave = dailyTask.durations
                .filter { it.startTime != 0L && it.endTime != 0L }
            if (durationsToSave.isNotEmpty()) {
                taskDurationDao.upsertAllTaskDurations(
                    durationsToSave.map { it.toEntity(taskId) }
                )
            }
        }

        Result.success(taskId)
    } catch (e: Exception) {
        Result.failure(e)
    }
}

    suspend fun deleteDailyTask(dailyTask: DailyTask): Result<Int> {
        return try {
            val rowsDeleted = dailyTaskDao.deleteDailyTask(dailyTask.toEntity())
            Result.success(rowsDeleted)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteAllDailyTasks(): Result<Int> {
        return try {
            val rowsDeleted = dailyTaskDao.deleteAllDailyTasks()
            Result.success(rowsDeleted)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    fun getMaxTaskId(): Flow<Long> {
        return dailyTaskDao.getMaxId()
    }

}