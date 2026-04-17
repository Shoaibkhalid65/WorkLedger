package com.gshoaib998.progressly.data.repository

import com.gshoaib998.progressly.data.local.db.dao.GoalDao
import com.gshoaib998.progressly.data.mapper.toEntity
import com.gshoaib998.progressly.data.mapper.toModel
import com.gshoaib998.progressly.di.ApplicationScope
import com.gshoaib998.progressly.di.DefaultDispatcher
import com.gshoaib998.progressly.model.Goal
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
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


    fun observeAllGoals(): Flow<List<Goal>> {
        return goalDao.getAllGoals()
            .map { goals -> goals.toModel() }
            .flowOn(dispatcher)
    }
}