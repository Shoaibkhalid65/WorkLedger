package com.example.progresstracker.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.example.progresstracker.data.local.entity.GoalEntity
import com.example.progresstracker.model.Goal
import kotlinx.coroutines.flow.Flow

@Dao
interface GoalDao {

    @Upsert
    suspend fun upsertGoal(goalEntity: GoalEntity): Long

    @Delete
    suspend fun deleteGoal(goal: GoalEntity): Int

    @Query("Delete from goals")
    suspend fun deleteAllGoals(): Int

    @Query("Select * from goals where id=:goalId")
    fun getGoalById(goalId: Long): Flow<GoalEntity>

    @Query("SELECT * FROM goals ORDER BY createdAt DESC")
    fun getAllGoals(): Flow<List<GoalEntity>>


}