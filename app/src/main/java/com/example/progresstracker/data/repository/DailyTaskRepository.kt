package com.example.progresstracker.data.repository

import com.example.progresstracker.data.local.db.dao.DailyTaskDao
import com.example.progresstracker.data.local.db.dao.TaskDurationDao
import com.example.progresstracker.data.mapper.toEntity
import com.example.progresstracker.data.mapper.toModel
import com.example.progresstracker.di.ApplicationScope
import com.example.progresstracker.di.DefaultDispatcher
import com.example.progresstracker.model.DailyTask
import com.example.progresstracker.model.SatisfyPercentage
import com.example.progresstracker.model.TaskDuration
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
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
        return taskDurationDao.getAllDurations().map { withContext(dispatcher) { it.toModel() } }
    }

    fun observeDurationById(durationId: Long): Flow<TaskDuration> {
        return taskDurationDao.getDurationById(durationId).map { it.toModel() }
    }

    fun observeDurationsByTaskId(taskId: Long): Flow<List<TaskDuration>> {
        return taskDurationDao.getAllDurationsByTaskId(taskId)
            .map { withContext(dispatcher) { it.toModel() } }
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
        return dailyTaskDao.getRealDailyTasks().map { dailyTasks ->
            dailyTasks.map { dailyTaskEntity ->
                val durations = taskDurationDao.getAllDurationsByTaskId(dailyTaskEntity.id).first()
                dailyTaskEntity.toModel(durations)
            }
        }
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

    suspend fun createOrUpdateDailyTaskWithDurations(dailyTask: DailyTask): Result<Long> {
        return try {
            var id = -1L
            val maxTaskId = getMaxTaskId().first()
            val lastStoredTask = dailyTaskDao.getDailyTaskById(maxTaskId)
            id = if (lastStoredTask != null && lastStoredTask.title.isEmpty()) {
                dailyTaskDao.upsertDailyTask(
                    dailyTask.toEntity().copy(id = maxTaskId)
                )
                maxTaskId
            } else if (dailyTask.id != 0L) {
                dailyTaskDao.upsertDailyTask(dailyTask.toEntity())
                dailyTask.id
            } else {
                dailyTaskDao.upsertDailyTask(dailyTask.toEntity())
            }
            taskDurationDao.deleteDurationsByTaskId(id)

            val durationsToCreate = dailyTask.durations
                .filter { it.startTime != 0L && it.endTime != 0L }
            if (durationsToCreate.isNotEmpty()) {
                taskDurationDao.upsertAllTaskDurations(
                    durationsToCreate.map { it.toEntity(id) }
                )
            }
            Result.success(id)
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