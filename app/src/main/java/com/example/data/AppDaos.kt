package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PlayerDao {
    @Query("SELECT * FROM player_profile WHERE id = 1")
    fun observeProfile(): Flow<PlayerProfileEntity?>

    @Query("SELECT * FROM player_profile WHERE id = 1")
    suspend fun getProfileOnce(): PlayerProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertProfile(profile: PlayerProfileEntity)
}

@Dao
interface QuestDao {
    @Query("SELECT * FROM quests ORDER BY isCustom DESC, id ASC")
    fun observeQuests(): Flow<List<QuestEntity>>

    @Query("SELECT * FROM quests")
    suspend fun getQuestsOnce(): List<QuestEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertQuest(quest: QuestEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertQuests(quests: List<QuestEntity>)

    @Query("DELETE FROM quests WHERE id = :id")
    suspend fun deleteQuest(id: String)

    @Query("DELETE FROM quests")
    suspend fun clearAll()
}

@Dao
interface SleepDao {
    @Query("SELECT * FROM sleep_logs ORDER BY timestamp DESC")
    fun observeSleepLogs(): Flow<List<SleepLogEntity>>

    @Query("SELECT * FROM sleep_logs ORDER BY timestamp DESC")
    suspend fun getSleepLogsOnce(): List<SleepLogEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSleepLog(log: SleepLogEntity)

    @Query("DELETE FROM sleep_logs WHERE id = :id")
    suspend fun deleteSleepLog(id: Long)

    @Query("DELETE FROM sleep_logs")
    suspend fun clearAll()
}

@Dao
interface WorkoutDao {
    @Query("SELECT * FROM workout_logs ORDER BY timestamp DESC")
    fun observeWorkoutLogs(): Flow<List<WorkoutLogEntity>>

    @Query("SELECT * FROM workout_logs ORDER BY timestamp DESC")
    suspend fun getWorkoutLogsOnce(): List<WorkoutLogEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkoutLog(log: WorkoutLogEntity)

    @Query("DELETE FROM workout_logs")
    suspend fun clearAll()
}

@Dao
interface ChecklistDao {
    @Query("SELECT * FROM checklist_progress")
    fun observeChecklist(): Flow<List<ChecklistProgressEntity>>

    @Query("SELECT * FROM checklist_progress")
    suspend fun getChecklistOnce(): List<ChecklistProgressEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(item: ChecklistProgressEntity)

    @Query("DELETE FROM checklist_progress")
    suspend fun clearAll()
}

@Dao
interface VideoDao {
    @Query("SELECT * FROM video_library ORDER BY categoryName ASC, title ASC")
    fun observeVideos(): Flow<List<VideoItemEntity>>

    @Query("SELECT * FROM video_library")
    suspend fun getVideosOnce(): List<VideoItemEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertVideo(video: VideoItemEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertVideos(videos: List<VideoItemEntity>)

    @Query("DELETE FROM video_library WHERE id = :id")
    suspend fun deleteVideo(id: String)

    @Query("DELETE FROM video_library")
    suspend fun clearAll()
}

@Dao
interface ProgressionDao {
    @Query("SELECT * FROM reward_unlocks")
    fun observeRewards(): Flow<List<RewardUnlockEntity>>

    @Query("SELECT * FROM reward_unlocks")
    suspend fun getRewardsOnce(): List<RewardUnlockEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertReward(item: RewardUnlockEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertRewards(items: List<RewardUnlockEntity>)

    @Query("SELECT * FROM achievement_unlocks")
    fun observeAchievements(): Flow<List<AchievementUnlockEntity>>

    @Query("SELECT * FROM achievement_unlocks")
    suspend fun getAchievementsOnce(): List<AchievementUnlockEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAchievement(item: AchievementUnlockEntity)

    @Query("SELECT * FROM boss_challenges")
    fun observeBossChallenges(): Flow<List<BossChallengeEntity>>

    @Query("SELECT * FROM boss_challenges")
    suspend fun getBossChallengesOnce(): List<BossChallengeEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertBossChallenge(item: BossChallengeEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertBossChallenges(items: List<BossChallengeEntity>)

    @Query("DELETE FROM reward_unlocks")
    suspend fun clearRewards()

    @Query("DELETE FROM achievement_unlocks")
    suspend fun clearAchievements()

    @Query("DELETE FROM boss_challenges")
    suspend fun clearBossChallenges()
}
