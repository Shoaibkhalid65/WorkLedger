package com.gshoaib998.progressly.data.local.db.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.gshoaib998.progressly.data.local.db.dao.DailyTaskDao
import com.gshoaib998.progressly.data.local.db.dao.GoalDao
import com.gshoaib998.progressly.data.local.db.dao.TaskDurationDao
import com.gshoaib998.progressly.data.local.db.entity.DailyTaskEntity
import com.gshoaib998.progressly.data.local.db.entity.GoalEntity
import com.gshoaib998.progressly.data.local.db.entity.TaskDurationEntity


@Database(
    entities = [DailyTaskEntity::class, TaskDurationEntity::class, GoalEntity::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract val dailyTaskDao: DailyTaskDao
    abstract val durationDao: TaskDurationDao
    abstract val goalDao: GoalDao

    companion object{
        val MIGRATION_1_2= object : Migration(1,2){
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "ALTER TABLE task_durations ADD COLUMN dateEpoch INTEGER NOT NULL DEFAULT 0"
                )
            }
        }
    }
}