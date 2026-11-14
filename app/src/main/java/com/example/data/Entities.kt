package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "player_profile")
data class PlayerProfileEntity(
    @PrimaryKey val id: Int = 1,
    val playerName: String = "اللاعب",
    val ageGroupName: String = AgeGroup.YOUNG_ADULT_18_25.name,
    val fitnessLevelName: String = FitnessLevel.BEGINNER.name,
    val level: Int = 1,
    val currentXp: Int = 0,
    val coins: Int = 50,
    val activeDays: Int = 1,
    val dailyStreak: Int = 1,
    val lastActiveEpochDay: Long = 0L,
    val completedQuestsCount: Int = 0,
    val strength: Int = 10,
    val stamina: Int = 10,
    val mobility: Int = 10,
    val discipline: Int = 10,
    val focus: Int = 10,
    val knowledge: Int = 10,
    val recovery: Int = 10,
    val equippedThemeId: String = "theme_electric_blue",
    val equippedFrameId: String = "frame_cyber_cyan",
    val equippedTitleId: String = "title_awakened",
    val equippedBackgroundId: String = "bg_obsidian_grid",
    val equippedEffectId: String = "effect_cyan_pulse",
    val awakeningDone: Boolean = false
)

@Entity(tableName = "quests")
data class QuestEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val category: String,
    val estimatedMinutes: Int,
    val xpReward: Int,
    val coinsReward: Int,
    val statTypeName: String,
    val statusName: String = QuestStatus.PENDING.name,
    val isCustom: Boolean = false,
    val completedAt: Long = 0L
)

@Entity(tableName = "sleep_logs")
data class SleepLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val dateLabel: String,
    val bedTimeText: String,
    val wakeTimeText: String,
    val durationMinutes: Int,
    val qualityRating: Int,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "workout_logs")
data class WorkoutLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val exerciseId: String,
    val exerciseTitleAr: String,
    val setsCompleted: Int,
    val repsOrSeconds: Int,
    val xpEarned: Int,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "checklist_progress")
data class ChecklistProgressEntity(
    @PrimaryKey val itemKey: String,
    val isChecked: Boolean
)

@Entity(tableName = "video_library")
data class VideoItemEntity(
    @PrimaryKey val id: String,
    val title: String,
    val categoryName: String,
    val linkedContentId: String,
    val sourceUrl: String,
    val playerTypeName: String,
    val thumbnailLabel: String,
    val description: String,
    val verificationStatusName: String,
    val embedAllowed: Boolean
)

@Entity(tableName = "reward_unlocks")
data class RewardUnlockEntity(
    @PrimaryKey val id: String,
    val isUnlocked: Boolean
)

@Entity(tableName = "achievement_unlocks")
data class AchievementUnlockEntity(
    @PrimaryKey val id: String,
    val unlockedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "boss_challenges")
data class BossChallengeEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val targetCount: Int,
    val currentCount: Int,
    val xpReward: Int,
    val coinsReward: Int,
    val joined: Boolean = true,
    val completed: Boolean = false
)
