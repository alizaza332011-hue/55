package com.example.data

import androidx.room.withTransaction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

data class ProgressionGrantResult(
    val xpAdded: Int,
    val coinsAdded: Int,
    val previousLevel: Int,
    val newLevel: Int,
    val newlyUnlockedAchievements: List<AchievementDefinition>
)

data class FullAppSnapshot(
    val profile: PlayerProfileEntity,
    val quests: List<QuestEntity>,
    val sleepLogs: List<SleepLogEntity>,
    val workoutLogs: List<WorkoutLogEntity>,
    val checklistMap: Map<String, Boolean>,
    val videos: List<VideoItemEntity>,
    val unlockedRewards: Set<String>,
    val unlockedAchievements: Map<String, Long>,
    val bossChallenges: List<BossChallengeEntity>,
    val preferences: SystemPreferences
)

class LevelingRepository(
    private val db: AppDatabase,
    private val prefsStore: UserPreferencesStore
) {
    private val playerDao = db.playerDao()
    private val questDao = db.questDao()
    private val sleepDao = db.sleepDao()
    private val workoutDao = db.workoutDao()
    private val checklistDao = db.checklistDao()
    private val videoDao = db.videoDao()
    private val progressionDao = db.progressionDao()

    val appSnapshotFlow: Flow<FullAppSnapshot> = combine(
        combine(
            playerDao.observeProfile(),
            questDao.observeQuests(),
            sleepDao.observeSleepLogs(),
            workoutDao.observeWorkoutLogs(),
            checklistDao.observeChecklist()
        ) { profile, quests, sleepLogs, workoutLogs, checklist ->
            PartialCoreData(profile, quests, sleepLogs, workoutLogs, checklist)
        },
        combine(
            videoDao.observeVideos(),
            progressionDao.observeRewards(),
            progressionDao.observeAchievements(),
            progressionDao.observeBossChallenges(),
            prefsStore.preferencesFlow
        ) { videos, rewards, achievements, bosses, prefs ->
            PartialSystemData(videos, rewards, achievements, bosses, prefs)
        }
    ) { core, sys ->
        val profile = core.profile ?: PlayerProfileEntity()
        val unlockedRewards = sys.rewards.filter { it.isUnlocked }.map { it.id }.toSet() +
            ProgressionCatalog.rewardItems.filter { it.costCoins == 0 }.map { it.id }
        FullAppSnapshot(
            profile = profile,
            quests = core.quests,
            sleepLogs = core.sleepLogs,
            workoutLogs = core.workoutLogs,
            checklistMap = core.checklist.associate { it.itemKey to it.isChecked },
            videos = sys.videos,
            unlockedRewards = unlockedRewards,
            unlockedAchievements = sys.achievements.associate { it.id to it.unlockedAt },
            bossChallenges = sys.bosses,
            preferences = sys.prefs
        )
    }

    suspend fun ensureSeeded() {
        db.withTransaction {
            val existingProfile = playerDao.getProfileOnce()
            if (existingProfile == null) {
                playerDao.upsertProfile(
                    PlayerProfileEntity(
                        lastActiveEpochDay = System.currentTimeMillis() / 86_400_000L
                    )
                )
            } else {
                val today = System.currentTimeMillis() / 86_400_000L
                if (existingProfile.lastActiveEpochDay != today && existingProfile.awakeningDone) {
                    val isConsecutive = (today - existingProfile.lastActiveEpochDay) == 1L
                    playerDao.upsertProfile(
                        existingProfile.copy(
                            activeDays = existingProfile.activeDays + 1,
                            dailyStreak = if (isConsecutive) existingProfile.dailyStreak + 1 else 1,
                            lastActiveEpochDay = today
                        )
                    )
                }
            }
            if (questDao.getQuestsOnce().isEmpty()) {
                questDao.upsertQuests(ProgressionCatalog.defaultDailyQuests())
            }
            if (videoDao.getVideosOnce().isEmpty()) {
                videoDao.upsertVideos(ProgressionCatalog.defaultVideoLibrary())
            }
            if (progressionDao.getBossChallengesOnce().isEmpty()) {
                progressionDao.upsertBossChallenges(ProgressionCatalog.defaultBossChallenges())
            }
            if (progressionDao.getRewardsOnce().isEmpty()) {
                val defaults = ProgressionCatalog.rewardItems
                    .filter { it.costCoins == 0 }
                    .map { RewardUnlockEntity(it.id, true) }
                progressionDao.upsertRewards(defaults)
            }
        }
    }

    suspend fun completeAwakening(
        playerName: String,
        ageGroup: AgeGroup,
        fitnessLevel: FitnessLevel
    ): ProgressionGrantResult {
        return db.withTransaction {
            val cleanName = playerName.trim().ifEmpty { "اللاعب" }
            val current = playerDao.getProfileOnce() ?: PlayerProfileEntity()
            val today = System.currentTimeMillis() / 86_400_000L
            val updated = current.copy(
                playerName = cleanName,
                ageGroupName = ageGroup.name,
                fitnessLevelName = fitnessLevel.name,
                awakeningDone = true,
                lastActiveEpochDay = today
            )
            playerDao.upsertProfile(updated)
            val unlocked = unlockAchievementIfNeeded("ach_awakened")
            val bonusXp = unlocked?.xpBonus ?: 0
            if (bonusXp > 0) {
                grantXpInternal(bonusXp, 20, null)
            } else {
                ProgressionGrantResult(0, 0, updated.level, updated.level, emptyList())
            }
        }
    }

    suspend fun updateProfileMeta(
        playerName: String,
        ageGroup: AgeGroup,
        fitnessLevel: FitnessLevel
    ) {
        val current = playerDao.getProfileOnce() ?: return
        playerDao.upsertProfile(
            current.copy(
                playerName = playerName.trim().ifEmpty { current.playerName },
                ageGroupName = ageGroup.name,
                fitnessLevelName = fitnessLevel.name
            )
        )
    }

    suspend fun startQuest(questId: String) {
        val quest = questDao.getQuestsOnce().find { it.id == questId } ?: return
        if (quest.statusName != QuestStatus.COMPLETED.name) {
            questDao.upsertQuest(quest.copy(statusName = QuestStatus.IN_PROGRESS.name))
        }
    }

    suspend fun postponeQuest(questId: String) {
        val quest = questDao.getQuestsOnce().find { it.id == questId } ?: return
        if (quest.statusName != QuestStatus.COMPLETED.name) {
            questDao.upsertQuest(quest.copy(statusName = QuestStatus.POSTPONED.name))
        }
    }

    suspend fun resetDailyQuestsForNewDay() {
        val quests = questDao.getQuestsOnce()
        val reset = quests.map { it.copy(statusName = QuestStatus.PENDING.name, completedAt = 0L) }
        questDao.upsertQuests(reset)
    }

    suspend fun completeQuest(questId: String): ProgressionGrantResult? {
        return db.withTransaction {
            val quest = questDao.getQuestsOnce().find { it.id == questId } ?: return@withTransaction null
            if (quest.statusName == QuestStatus.COMPLETED.name) return@withTransaction null

            questDao.upsertQuest(
                quest.copy(
                    statusName = QuestStatus.COMPLETED.name,
                    completedAt = System.currentTimeMillis()
                )
            )
            val stat = StatType.entries.find { it.name == quest.statTypeName } ?: StatType.DISCIPLINE
            val current = playerDao.getProfileOnce() ?: PlayerProfileEntity()
            val newCompletedCount = current.completedQuestsCount + 1
            playerDao.upsertProfile(current.copy(completedQuestsCount = newCompletedCount))

            val newlyUnlocked = mutableListOf<AchievementDefinition>()
            if (newCompletedCount >= 1) unlockAchievementIfNeeded("ach_first_quest")?.let { newlyUnlocked.add(it) }
            if (newCompletedCount >= 5) unlockAchievementIfNeeded("ach_five_quests")?.let { newlyUnlocked.add(it) }
            if (newCompletedCount >= 10) unlockAchievementIfNeeded("ach_ten_quests")?.let { newlyUnlocked.add(it) }

            val bossBonus = advanceBossChallenges(questCompleted = true, recoveryAction = stat == StatType.RECOVERY)
            if (bossBonus.second != null) newlyUnlocked.add(bossBonus.second!!)

            val bonusAchievementXp = newlyUnlocked.sumOf { it.xpBonus }
            val result = grantXpInternal(
                xp = quest.xpReward + bonusAchievementXp + bossBonus.first.first,
                coins = quest.coinsReward + bossBonus.first.second,
                statType = stat,
                statDelta = 2
            )
            result.copy(newlyUnlockedAchievements = newlyUnlocked + result.newlyUnlockedAchievements)
        }
    }

    suspend fun addCustomQuest(
        title: String,
        description: String,
        category: String,
        estimatedMinutes: Int,
        xpReward: Int,
        statType: StatType
    ) {
        val quest = QuestEntity(
            id = "custom_${UUID.randomUUID().toString().take(8)}",
            title = title.trim(),
            description = description.trim().ifEmpty { "مهمة شخصية لتطوير الذات والالتزام." },
            category = category,
            estimatedMinutes = estimatedMinutes.coerceIn(2, 180),
            xpReward = xpReward.coerceIn(10, 100),
            coinsReward = (xpReward / 2).coerceIn(5, 50),
            statTypeName = statType.name,
            statusName = QuestStatus.PENDING.name,
            isCustom = true
        )
        questDao.upsertQuest(quest)
    }

    suspend fun deleteQuest(questId: String) {
        questDao.deleteQuest(questId)
    }

    suspend fun recordWorkoutSession(
        exercise: ExerciseInfo,
        setsCompleted: Int,
        repsOrSeconds: Int
    ): ProgressionGrantResult {
        return db.withTransaction {
            workoutDao.insertWorkoutLog(
                WorkoutLogEntity(
                    exerciseId = exercise.id,
                    exerciseTitleAr = exercise.titleAr,
                    setsCompleted = setsCompleted,
                    repsOrSeconds = repsOrSeconds,
                    xpEarned = exercise.xpReward
                )
            )
            val newlyUnlocked = mutableListOf<AchievementDefinition>()
            unlockAchievementIfNeeded("ach_first_workout")?.let { newlyUnlocked.add(it) }
            val bonusXp = newlyUnlocked.sumOf { it.xpBonus }
            val res = grantXpInternal(
                xp = exercise.xpReward + bonusXp,
                coins = (exercise.xpReward * 0.7f).toInt().coerceAtLeast(15),
                statType = exercise.statReward,
                statDelta = 3
            )
            res.copy(newlyUnlockedAchievements = newlyUnlocked + res.newlyUnlockedAchievements)
        }
    }

    suspend fun toggleChecklistItem(itemKey: String, checked: Boolean, xpValue: Int): ProgressionGrantResult? {
        return db.withTransaction {
            val prev = checklistDao.getChecklistOnce().find { it.itemKey == itemKey }
            val wasChecked = prev?.isChecked == true
            checklistDao.upsert(ChecklistProgressEntity(itemKey, checked))
            if (checked && !wasChecked && xpValue > 0) {
                val newlyUnlocked = mutableListOf<AchievementDefinition>()
                val totalChecked = checklistDao.getChecklistOnce().count { it.isChecked }
                if (totalChecked >= 5) {
                    unlockAchievementIfNeeded("ach_glow_care")?.let { newlyUnlocked.add(it) }
                }
                val recoveryAction = itemKey.startsWith("sleep_")
                val bossBonus = advanceBossChallenges(questCompleted = false, recoveryAction = recoveryAction)
                if (bossBonus.second != null) newlyUnlocked.add(bossBonus.second!!)
                val bonusXp = newlyUnlocked.sumOf { it.xpBonus } + bossBonus.first.first
                val res = grantXpInternal(
                    xp = xpValue + bonusXp,
                    coins = 10 + bossBonus.first.second,
                    statType = if (recoveryAction) StatType.RECOVERY else StatType.DISCIPLINE,
                    statDelta = 1
                )
                res.copy(newlyUnlockedAchievements = newlyUnlocked + res.newlyUnlockedAchievements)
            } else {
                null
            }
        }
    }

    suspend fun recordSleepLog(
        dateLabel: String,
        bedTimeText: String,
        wakeTimeText: String,
        durationMinutes: Int,
        qualityRating: Int
    ): ProgressionGrantResult {
        return db.withTransaction {
            sleepDao.insertSleepLog(
                SleepLogEntity(
                    dateLabel = dateLabel,
                    bedTimeText = bedTimeText,
                    wakeTimeText = wakeTimeText,
                    durationMinutes = durationMinutes.coerceIn(60, 960),
                    qualityRating = qualityRating.coerceIn(1, 5)
                )
            )
            val newlyUnlocked = mutableListOf<AchievementDefinition>()
            unlockAchievementIfNeeded("ach_sleep_tracker")?.let { newlyUnlocked.add(it) }
            val bossBonus = advanceBossChallenges(questCompleted = false, recoveryAction = true)
            if (bossBonus.second != null) newlyUnlocked.add(bossBonus.second!!)

            // Reward logging sleep & resting adequately (never punish short sleep or late nights!)
            val baseXp = if (durationMinutes >= 420) 30 else 20
            val res = grantXpInternal(
                xp = baseXp + newlyUnlocked.sumOf { it.xpBonus } + bossBonus.first.first,
                coins = 15 + bossBonus.first.second,
                statType = StatType.RECOVERY,
                statDelta = 2
            )
            res.copy(newlyUnlockedAchievements = newlyUnlocked + res.newlyUnlockedAchievements)
        }
    }

    suspend fun deleteSleepLog(id: Long) {
        sleepDao.deleteSleepLog(id)
    }

    suspend fun completePomodoroSession(focusMinutes: Int): ProgressionGrantResult {
        return db.withTransaction {
            val newlyUnlocked = mutableListOf<AchievementDefinition>()
            unlockAchievementIfNeeded("ach_focus_master")?.let { newlyUnlocked.add(it) }
            val xp = (focusMinutes * 1.5f).toInt().coerceIn(20, 90)
            val res = grantXpInternal(
                xp = xp + newlyUnlocked.sumOf { it.xpBonus },
                coins = (xp / 2).coerceAtLeast(15),
                statType = StatType.FOCUS,
                statDelta = 3
            )
            // Also nudge knowledge slightly
            val p = playerDao.getProfileOnce()
            if (p != null) {
                playerDao.upsertProfile(p.copy(knowledge = p.knowledge + 1))
            }
            res.copy(newlyUnlockedAchievements = newlyUnlocked + res.newlyUnlockedAchievements)
        }
    }

    suspend fun toggleJoinBossChallenge(challengeId: String) {
        val current = progressionDao.getBossChallengesOnce().find { it.id == challengeId } ?: return
        progressionDao.upsertBossChallenge(current.copy(joined = !current.joined))
    }

    suspend fun purchaseOrEquipReward(item: RewardItemDefinition): String? {
        return db.withTransaction {
            val profile = playerDao.getProfileOnce() ?: return@withTransaction "تعذر تحميل ملف اللاعب"
            val unlockedSet = progressionDao.getRewardsOnce().filter { it.isUnlocked }.map { it.id }.toSet()
            val alreadyUnlocked = item.costCoins == 0 || item.id in unlockedSet

            if (!alreadyUnlocked) {
                if (profile.coins < item.costCoins) {
                    return@withTransaction "العملات غير كافية (${item.costCoins} عملة مطلوبة)"
                }
                playerDao.upsertProfile(profile.copy(coins = profile.coins - item.costCoins))
                progressionDao.upsertReward(RewardUnlockEntity(item.id, true))
            }

            val latestProfile = playerDao.getProfileOnce() ?: return@withTransaction null
            val equipped = when (item.category) {
                RewardCategory.THEME -> latestProfile.copy(equippedThemeId = item.id)
                RewardCategory.BACKGROUND -> latestProfile.copy(equippedBackgroundId = item.id)
                RewardCategory.FRAME -> latestProfile.copy(equippedFrameId = item.id)
                RewardCategory.TITLE -> latestProfile.copy(equippedTitleId = item.id)
                RewardCategory.EFFECT -> latestProfile.copy(equippedEffectId = item.id)
            }
            playerDao.upsertProfile(equipped)
            null
        }
    }

    suspend fun saveVideoItem(
        id: String?,
        title: String,
        category: VideoSectionCategory,
        linkedContentId: String,
        sourceUrl: String,
        description: String
    ): VideoItemEntity {
        val validation = validateVideoSource(sourceUrl)
        val item = VideoItemEntity(
            id = id ?: "vid_${UUID.randomUUID().toString().take(8)}",
            title = title.trim(),
            categoryName = category.name,
            linkedContentId = linkedContentId.trim(),
            sourceUrl = sourceUrl.trim(),
            playerTypeName = validation.playerType.name,
            thumbnailLabel = "${category.titleAr} • ${validation.playerType.titleAr}",
            description = description.trim(),
            verificationStatusName = validation.status.name,
            embedAllowed = validation.embedAllowed
        )
        videoDao.upsertVideo(item)
        return item
    }

    suspend fun deleteVideoItem(id: String) {
        videoDao.deleteVideo(id)
    }

    data class VideoValidationResult(
        val playerType: VideoPlayerType,
        val status: VideoVerificationStatus,
        val embedAllowed: Boolean
    )

    fun validateVideoSource(rawUrl: String): VideoValidationResult {
        val url = rawUrl.trim()
        if (url.isEmpty()) {
            return VideoValidationResult(
                playerType = VideoPlayerType.INTERACTIVE_GUIDE_ONLY,
                status = VideoVerificationStatus.EMBED_RESTRICTED,
                embedAllowed = false
            )
        }
        val lower = url.lowercase()
        val ytId = extractYoutubeVideoId(url)
        return when {
            ytId != null -> VideoValidationResult(
                playerType = VideoPlayerType.YOUTUBE_EMBED,
                status = VideoVerificationStatus.VERIFIED,
                embedAllowed = true
            )
            (lower.startsWith("https://") || lower.startsWith("http://")) &&
                (lower.endsWith(".mp4") || lower.contains(".mp4?") || lower.endsWith(".webm")) -> VideoValidationResult(
                playerType = VideoPlayerType.DIRECT_MP4,
                status = VideoVerificationStatus.VERIFIED,
                embedAllowed = true
            )
            else -> VideoValidationResult(
                playerType = VideoPlayerType.INTERACTIVE_GUIDE_ONLY,
                status = VideoVerificationStatus.EMBED_RESTRICTED,
                embedAllowed = false
            )
        }
    }

    fun extractYoutubeVideoId(url: String): String? {
        val trimmed = url.trim()
        if (trimmed.isEmpty()) return null
        val regexes = listOf(
            Regex("""(?:youtube\.com/watch\?v=|youtube\.com/embed/|youtu\.be/|youtube\.com/shorts/)([A-Za-z0-9_-]{11})""")
        )
        for (regex in regexes) {
            val match = regex.find(trimmed)
            if (match != null) return match.groupValues[1]
        }
        return null
    }

    suspend fun updateAudioPreferences(muted: Boolean, musicEnabled: Boolean, volume: Float) {
        prefsStore.setSoundMuted(muted)
        prefsStore.setMusicEnabled(musicEnabled)
        prefsStore.setVolume(volume)
    }

    suspend fun updateBedtimeReminder(enabled: Boolean, hour: Int, minute: Int) {
        prefsStore.setBedtimeReminder(enabled, hour, minute)
    }

    suspend fun exportJsonBackup(): String {
        val profile = playerDao.getProfileOnce() ?: PlayerProfileEntity()
        val quests = questDao.getQuestsOnce()
        val sleepLogs = sleepDao.getSleepLogsOnce()
        val workoutLogs = workoutDao.getWorkoutLogsOnce()
        val checklist = checklistDao.getChecklistOnce()
        val videos = videoDao.getVideosOnce()
        val rewards = progressionDao.getRewardsOnce()
        val achievements = progressionDao.getAchievementsOnce()
        val bosses = progressionDao.getBossChallengesOnce()

        val root = JSONObject()
        root.put("version", 1)
        root.put("exportedAt", System.currentTimeMillis())

        val pJson = JSONObject().apply {
            put("playerName", profile.playerName)
            put("ageGroupName", profile.ageGroupName)
            put("fitnessLevelName", profile.fitnessLevelName)
            put("level", profile.level)
            put("currentXp", profile.currentXp)
            put("coins", profile.coins)
            put("activeDays", profile.activeDays)
            put("dailyStreak", profile.dailyStreak)
            put("lastActiveEpochDay", profile.lastActiveEpochDay)
            put("completedQuestsCount", profile.completedQuestsCount)
            put("strength", profile.strength)
            put("stamina", profile.stamina)
            put("mobility", profile.mobility)
            put("discipline", profile.discipline)
            put("focus", profile.focus)
            put("knowledge", profile.knowledge)
            put("recovery", profile.recovery)
            put("equippedThemeId", profile.equippedThemeId)
            put("equippedFrameId", profile.equippedFrameId)
            put("equippedTitleId", profile.equippedTitleId)
            put("equippedBackgroundId", profile.equippedBackgroundId)
            put("equippedEffectId", profile.equippedEffectId)
            put("awakeningDone", profile.awakeningDone)
        }
        root.put("profile", pJson)

        val qArr = JSONArray()
        quests.forEach { q ->
            qArr.put(JSONObject().apply {
                put("id", q.id)
                put("title", q.title)
                put("description", q.description)
                put("category", q.category)
                put("estimatedMinutes", q.estimatedMinutes)
                put("xpReward", q.xpReward)
                put("coinsReward", q.coinsReward)
                put("statTypeName", q.statTypeName)
                put("statusName", q.statusName)
                put("isCustom", q.isCustom)
                put("completedAt", q.completedAt)
            })
        }
        root.put("quests", qArr)

        val sArr = JSONArray()
        sleepLogs.forEach { s ->
            sArr.put(JSONObject().apply {
                put("dateLabel", s.dateLabel)
                put("bedTimeText", s.bedTimeText)
                put("wakeTimeText", s.wakeTimeText)
                put("durationMinutes", s.durationMinutes)
                put("qualityRating", s.qualityRating)
                put("timestamp", s.timestamp)
            })
        }
        root.put("sleepLogs", sArr)

        val wArr = JSONArray()
        workoutLogs.forEach { w ->
            wArr.put(JSONObject().apply {
                put("exerciseId", w.exerciseId)
                put("exerciseTitleAr", w.exerciseTitleAr)
                put("setsCompleted", w.setsCompleted)
                put("repsOrSeconds", w.repsOrSeconds)
                put("xpEarned", w.xpEarned)
                put("timestamp", w.timestamp)
            })
        }
        root.put("workoutLogs", wArr)

        val cArr = JSONArray()
        checklist.forEach { c ->
            cArr.put(JSONObject().apply {
                put("itemKey", c.itemKey)
                put("isChecked", c.isChecked)
            })
        }
        root.put("checklist", cArr)

        val vArr = JSONArray()
        videos.forEach { v ->
            vArr.put(JSONObject().apply {
                put("id", v.id)
                put("title", v.title)
                put("categoryName", v.categoryName)
                put("linkedContentId", v.linkedContentId)
                put("sourceUrl", v.sourceUrl)
                put("playerTypeName", v.playerTypeName)
                put("thumbnailLabel", v.thumbnailLabel)
                put("description", v.description)
                put("verificationStatusName", v.verificationStatusName)
                put("embedAllowed", v.embedAllowed)
            })
        }
        root.put("videos", vArr)

        val rArr = JSONArray()
        rewards.forEach { r -> rArr.put(JSONObject().put("id", r.id).put("isUnlocked", r.isUnlocked)) }
        root.put("rewards", rArr)

        val aArr = JSONArray()
        achievements.forEach { a -> aArr.put(JSONObject().put("id", a.id).put("unlockedAt", a.unlockedAt)) }
        root.put("achievements", aArr)

        val bArr = JSONArray()
        bosses.forEach { b ->
            bArr.put(JSONObject().apply {
                put("id", b.id)
                put("title", b.title)
                put("description", b.description)
                put("targetCount", b.targetCount)
                put("currentCount", b.currentCount)
                put("xpReward", b.xpReward)
                put("coinsReward", b.coinsReward)
                put("joined", b.joined)
                put("completed", b.completed)
            })
        }
        root.put("bossChallenges", bArr)

        return root.toString(2)
    }

    suspend fun importJsonBackup(jsonText: String): Boolean {
        return try {
            val root = JSONObject(jsonText)
            val pJson = root.getJSONObject("profile")
            db.withTransaction {
                playerDao.upsertProfile(
                    PlayerProfileEntity(
                        id = 1,
                        playerName = pJson.optString("playerName", "اللاعب"),
                        ageGroupName = pJson.optString("ageGroupName", AgeGroup.TEEN_13_17.name),
                        fitnessLevelName = pJson.optString("fitnessLevelName", FitnessLevel.BEGINNER.name),
                        level = pJson.optInt("level", 1),
                        currentXp = pJson.optInt("currentXp", 0),
                        coins = pJson.optInt("coins", 50),
                        activeDays = pJson.optInt("activeDays", 1),
                        dailyStreak = pJson.optInt("dailyStreak", 1),
                        lastActiveEpochDay = pJson.optLong("lastActiveEpochDay", System.currentTimeMillis() / 86_400_000L),
                        completedQuestsCount = pJson.optInt("completedQuestsCount", 0),
                        strength = pJson.optInt("strength", 10),
                        stamina = pJson.optInt("stamina", 10),
                        mobility = pJson.optInt("mobility", 10),
                        discipline = pJson.optInt("discipline", 10),
                        focus = pJson.optInt("focus", 10),
                        knowledge = pJson.optInt("knowledge", 10),
                        recovery = pJson.optInt("recovery", 10),
                        equippedThemeId = pJson.optString("equippedThemeId", "theme_electric_blue"),
                        equippedFrameId = pJson.optString("equippedFrameId", "frame_cyber_cyan"),
                        equippedTitleId = pJson.optString("equippedTitleId", "title_awakened"),
                        equippedBackgroundId = pJson.optString("equippedBackgroundId", "bg_obsidian_grid"),
                        equippedEffectId = pJson.optString("equippedEffectId", "effect_cyan_pulse"),
                        awakeningDone = pJson.optBoolean("awakeningDone", true)
                    )
                )

                val qArr = root.optJSONArray("quests")
                if (qArr != null) {
                    questDao.clearAll()
                    val list = buildList {
                        for (i in 0 until qArr.length()) {
                            val o = qArr.getJSONObject(i)
                            add(
                                QuestEntity(
                                    id = o.getString("id"),
                                    title = o.getString("title"),
                                    description = o.optString("description"),
                                    category = o.optString("category"),
                                    estimatedMinutes = o.optInt("estimatedMinutes", 10),
                                    xpReward = o.optInt("xpReward", 20),
                                    coinsReward = o.optInt("coinsReward", 10),
                                    statTypeName = o.optString("statTypeName", StatType.DISCIPLINE.name),
                                    statusName = o.optString("statusName", QuestStatus.PENDING.name),
                                    isCustom = o.optBoolean("isCustom", false),
                                    completedAt = o.optLong("completedAt", 0L)
                                )
                            )
                        }
                    }
                    questDao.upsertQuests(list)
                }

                val sArr = root.optJSONArray("sleepLogs")
                if (sArr != null) {
                    sleepDao.clearAll()
                    for (i in 0 until sArr.length()) {
                        val o = sArr.getJSONObject(i)
                        sleepDao.insertSleepLog(
                            SleepLogEntity(
                                dateLabel = o.getString("dateLabel"),
                                bedTimeText = o.getString("bedTimeText"),
                                wakeTimeText = o.getString("wakeTimeText"),
                                durationMinutes = o.getInt("durationMinutes"),
                                qualityRating = o.optInt("qualityRating", 4),
                                timestamp = o.optLong("timestamp", System.currentTimeMillis())
                            )
                        )
                    }
                }

                val wArr = root.optJSONArray("workoutLogs")
                if (wArr != null) {
                    workoutDao.clearAll()
                    for (i in 0 until wArr.length()) {
                        val o = wArr.getJSONObject(i)
                        workoutDao.insertWorkoutLog(
                            WorkoutLogEntity(
                                exerciseId = o.getString("exerciseId"),
                                exerciseTitleAr = o.getString("exerciseTitleAr"),
                                setsCompleted = o.optInt("setsCompleted", 1),
                                repsOrSeconds = o.optInt("repsOrSeconds", 10),
                                xpEarned = o.optInt("xpEarned", 30),
                                timestamp = o.optLong("timestamp", System.currentTimeMillis())
                            )
                        )
                    }
                }

                val cArr = root.optJSONArray("checklist")
                if (cArr != null) {
                    checklistDao.clearAll()
                    for (i in 0 until cArr.length()) {
                        val o = cArr.getJSONObject(i)
                        checklistDao.upsert(ChecklistProgressEntity(o.getString("itemKey"), o.getBoolean("isChecked")))
                    }
                }

                val vArr = root.optJSONArray("videos")
                if (vArr != null) {
                    videoDao.clearAll()
                    val vids = buildList {
                        for (i in 0 until vArr.length()) {
                            val o = vArr.getJSONObject(i)
                            add(
                                VideoItemEntity(
                                    id = o.getString("id"),
                                    title = o.getString("title"),
                                    categoryName = o.getString("categoryName"),
                                    linkedContentId = o.optString("linkedContentId"),
                                    sourceUrl = o.optString("sourceUrl"),
                                    playerTypeName = o.getString("playerTypeName"),
                                    thumbnailLabel = o.optString("thumbnailLabel"),
                                    description = o.optString("description"),
                                    verificationStatusName = o.getString("verificationStatusName"),
                                    embedAllowed = o.optBoolean("embedAllowed", false)
                                )
                            )
                        }
                    }
                    videoDao.upsertVideos(vids)
                }

                val rArr = root.optJSONArray("rewards")
                if (rArr != null) {
                    progressionDao.clearRewards()
                    val list = buildList {
                        for (i in 0 until rArr.length()) {
                            val o = rArr.getJSONObject(i)
                            add(RewardUnlockEntity(o.getString("id"), o.getBoolean("isUnlocked")))
                        }
                    }
                    progressionDao.upsertRewards(list)
                }

                val aArr = root.optJSONArray("achievements")
                if (aArr != null) {
                    progressionDao.clearAchievements()
                    for (i in 0 until aArr.length()) {
                        val o = aArr.getJSONObject(i)
                        progressionDao.upsertAchievement(AchievementUnlockEntity(o.getString("id"), o.getLong("unlockedAt")))
                    }
                }

                val bArr = root.optJSONArray("bossChallenges")
                if (bArr != null) {
                    progressionDao.clearBossChallenges()
                    val list = buildList {
                        for (i in 0 until bArr.length()) {
                            val o = bArr.getJSONObject(i)
                            add(
                                BossChallengeEntity(
                                    id = o.getString("id"),
                                    title = o.getString("title"),
                                    description = o.optString("description"),
                                    targetCount = o.optInt("targetCount", 10),
                                    currentCount = o.optInt("currentCount", 0),
                                    xpReward = o.optInt("xpReward", 100),
                                    coinsReward = o.optInt("coinsReward", 80),
                                    joined = o.optBoolean("joined", true),
                                    completed = o.optBoolean("completed", false)
                                )
                            )
                        }
                    }
                    progressionDao.upsertBossChallenges(list)
                }
            }
            true
        } catch (_: Throwable) {
            false
        }
    }

    suspend fun resetAllData() {
        db.withTransaction {
            questDao.clearAll()
            sleepDao.clearAll()
            workoutDao.clearAll()
            checklistDao.clearAll()
            videoDao.clearAll()
            progressionDao.clearRewards()
            progressionDao.clearAchievements()
            progressionDao.clearBossChallenges()
            playerDao.upsertProfile(
                PlayerProfileEntity(
                    lastActiveEpochDay = System.currentTimeMillis() / 86_400_000L
                )
            )
            questDao.upsertQuests(ProgressionCatalog.defaultDailyQuests())
            videoDao.upsertVideos(ProgressionCatalog.defaultVideoLibrary())
            progressionDao.upsertBossChallenges(ProgressionCatalog.defaultBossChallenges())
            progressionDao.upsertRewards(
                ProgressionCatalog.rewardItems
                    .filter { it.costCoins == 0 }
                    .map { RewardUnlockEntity(it.id, true) }
            )
        }
        prefsStore.resetDefaults()
    }

    private suspend fun grantXpInternal(
        xp: Int,
        coins: Int,
        statType: StatType?,
        statDelta: Int = 1
    ): ProgressionGrantResult {
        val current = playerDao.getProfileOnce() ?: PlayerProfileEntity()
        val prevLevel = current.level
        val newXp = (current.currentXp + xp).coerceAtLeast(0)
        val newLevel = ProgressionCatalog.calculateLevelForXp(newXp)
        var updated = current.copy(
            currentXp = newXp,
            level = newLevel,
            coins = current.coins + coins
        )
        updated = when (statType) {
            StatType.STRENGTH -> updated.copy(strength = updated.strength + statDelta)
            StatType.STAMINA -> updated.copy(stamina = updated.stamina + statDelta)
            StatType.MOBILITY -> updated.copy(mobility = updated.mobility + statDelta)
            StatType.DISCIPLINE -> updated.copy(discipline = updated.discipline + statDelta)
            StatType.FOCUS -> updated.copy(focus = updated.focus + statDelta)
            StatType.KNOWLEDGE -> updated.copy(knowledge = updated.knowledge + statDelta)
            StatType.RECOVERY -> updated.copy(recovery = updated.recovery + statDelta)
            null -> updated
        }
        playerDao.upsertProfile(updated)

        val extraAchievements = mutableListOf<AchievementDefinition>()
        if (newLevel >= 3) {
            unlockAchievementIfNeeded("ach_level_3")?.let { ach ->
                extraAchievements.add(ach)
                val withBonus = playerDao.getProfileOnce()
                if (withBonus != null) {
                    val boostedXp = withBonus.currentXp + ach.xpBonus
                    playerDao.upsertProfile(
                        withBonus.copy(
                            currentXp = boostedXp,
                            level = ProgressionCatalog.calculateLevelForXp(boostedXp)
                        )
                    )
                }
            }
        }
        val finalLevel = playerDao.getProfileOnce()?.level ?: newLevel
        return ProgressionGrantResult(xp, coins, prevLevel, finalLevel, extraAchievements)
    }

    private suspend fun unlockAchievementIfNeeded(id: String): AchievementDefinition? {
        val existing = progressionDao.getAchievementsOnce().any { it.id == id }
        if (existing) return null
        val def = ProgressionCatalog.achievements.find { it.id == id } ?: return null
        progressionDao.upsertAchievement(AchievementUnlockEntity(id))
        return def
    }

    private suspend fun advanceBossChallenges(
        questCompleted: Boolean,
        recoveryAction: Boolean
    ): Pair<Pair<Int, Int>, AchievementDefinition?> {
        val bosses = progressionDao.getBossChallengesOnce()
        var bonusXp = 0
        var bonusCoins = 0
        var unlockedAch: AchievementDefinition? = null

        for (boss in bosses) {
            if (!boss.joined || boss.completed) continue
            val shouldIncrement = when (boss.id) {
                "boss_discipline_week" -> questCompleted
                "boss_recovery_week" -> recoveryAction
                else -> questCompleted
            }
            if (shouldIncrement) {
                val nextCount = (boss.currentCount + 1).coerceAtMost(boss.targetCount)
                val isNowComplete = nextCount >= boss.targetCount
                progressionDao.upsertBossChallenge(
                    boss.copy(currentCount = nextCount, completed = isNowComplete)
                )
                if (isNowComplete) {
                    bonusXp += boss.xpReward
                    bonusCoins += boss.coinsReward
                    unlockedAch = unlockAchievementIfNeeded("ach_boss_clear")
                }
            }
        }
        return (bonusXp to bonusCoins) to unlockedAch
    }

    private data class PartialCoreData(
        val profile: PlayerProfileEntity?,
        val quests: List<QuestEntity>,
        val sleepLogs: List<SleepLogEntity>,
        val workoutLogs: List<WorkoutLogEntity>,
        val checklist: List<ChecklistProgressEntity>
    )

    private data class PartialSystemData(
        val videos: List<VideoItemEntity>,
        val rewards: List<RewardUnlockEntity>,
        val achievements: List<AchievementUnlockEntity>,
        val bosses: List<BossChallengeEntity>,
        val prefs: SystemPreferences
    )
}
