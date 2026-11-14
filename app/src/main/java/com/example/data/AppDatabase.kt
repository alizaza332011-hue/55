package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        PlayerProfileEntity::class,
        QuestEntity::class,
        SleepLogEntity::class,
        WorkoutLogEntity::class,
        ChecklistProgressEntity::class,
        VideoItemEntity::class,
        RewardUnlockEntity::class,
        AchievementUnlockEntity::class,
        BossChallengeEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun playerDao(): PlayerDao
    abstract fun questDao(): QuestDao
    abstract fun sleepDao(): SleepDao
    abstract fun workoutDao(): WorkoutDao
    abstract fun checklistDao(): ChecklistDao
    abstract fun videoDao(): VideoDao
    abstract fun progressionDao(): ProgressionDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "real_life_leveling.db"
                ).build().also { INSTANCE = it }
            }
        }
    }
}
