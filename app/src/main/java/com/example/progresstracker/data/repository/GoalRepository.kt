package com.example.progresstracker.data.repository

import com.example.progresstracker.data.local.dao.GoalDao
import com.example.progresstracker.data.mapper.toEntity
import com.example.progresstracker.data.mapper.toModel
import com.example.progresstracker.di.ApplicationScope
import com.example.progresstracker.di.DefaultDispatcher
import com.example.progresstracker.model.Goal
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GoalRepository @Inject constructor(
    private val goalDao: GoalDao,
    @param:ApplicationScope private val scope: CoroutineScope,
    @param:DefaultDispatcher private val dispatcher: CoroutineDispatcher
) {
    suspend fun createOrUpdateGoal(goal: Goal): Result<Long> {
        return try {
            val id = goalDao.upsertGoal(goal.toEntity())
            Result.success(id)
        } catch (e: Exception) {
            Result.failure(e)
        }

    }

    suspend fun deleteGoal(goal: Goal): Result<Int> {
        return try {
            val rowsDeleted = goalDao.deleteGoal(goal.toEntity())
            Result.success(rowsDeleted)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteAllGoals(): Result<Int> {
        return try {
            val rowsDeleted = goalDao.deleteAllGoals()
            Result.success(rowsDeleted)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getGoalById(goalId: Long): Flow<Goal> {
        return goalDao.getGoalById(goalId).map { it.toModel() }
    }


    @OptIn(ExperimentalCoroutinesApi::class)
    fun observeAllGoals(): Flow<List<Goal>> {
        return goalDao.getAllGoals().map { goals ->
            withContext(dispatcher) {
                goals.toModel()
            }
        }
    }
}