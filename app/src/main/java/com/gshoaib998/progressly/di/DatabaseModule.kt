package com.gshoaib998.progressly.di

import android.content.Context
import androidx.room.Room
import com.gshoaib998.progressly.data.local.db.dao.DailyTaskDao
import com.gshoaib998.progressly.data.local.db.dao.GoalDao
import com.gshoaib998.progressly.data.local.db.dao.TaskDurationDao
import com.gshoaib998.progressly.data.local.db.database.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room
            .databaseBuilder(
                context,
                AppDatabase::class.java,
                name = "task_database_0"
            )
            .addMigrations(AppDatabase.MIGRATION_1_2)
            .build()
    }

    @Provides
    @Singleton
    fun provideDailyTaskDao(appDatabase: AppDatabase): DailyTaskDao {
        return appDatabase.dailyTaskDao
    }

    @Provides
    @Singleton
    fun provideTaskDurationDao(appDatabase: AppDatabase): TaskDurationDao {
        return appDatabase.durationDao
    }

    @Provides
    @Singleton
    fun provideGoalDao(appDatabase: AppDatabase): GoalDao {
        return appDatabase.goalDao
    }


}