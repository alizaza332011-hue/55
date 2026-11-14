package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.HudAudioEngine
import com.example.data.AchievementDefinition
import com.example.data.AgeGroup
import com.example.data.AppDatabase
import com.example.data.ExerciseInfo
import com.example.data.FitnessCatalog
import com.example.data.FitnessLevel
import com.example.data.FullAppSnapshot
import com.example.data.LevelingRepository
import com.example.data.ProgressionGrantResult
import com.example.data.RewardItemDefinition
import com.example.data.StatType
import com.example.data.UserPreferencesStore
import com.example.data.VideoSectionCategory
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

sealed interface HudFeedbackEvent {
    data class QuestCompleted(val xp: Int, val coins: Int, val label: String) : HudFeedbackEvent
    data class LevelUp(val newLevel: Int) : HudFeedbackEvent
    data class AchievementUnlocked(val achievement: AchievementDefinition) : HudFeedbackEvent
    data class ToastMessage(val message: String) : HudFeedbackEvent
}

data class WorkoutSessionState(
    val queue: List<ExerciseInfo> = emptyList(),
    val currentIndex: Int = 0,
    val currentSet: Int = 1,
    val totalSets: Int = 3,
    val targetRepsOrSeconds: Int = 12,
    val isTimedExercise: Boolean = false,
    val isResting: Boolean = false,
    val remainingSeconds: Int = 0,
    val timerRunning: Boolean = false,
    val completedExercisesCount: Int = 0,
    val totalXpEarned: Int = 0,
    val sessionFinished: Boolean = false
)

data class PomodoroState(
    val focusMinutes: Int = 25,
    val breakMinutes: Int = 5,
    val isBreakPhase: Boolean = false,
    val remainingSeconds: Int = 25 * 60,
    val isRunning: Boolean = false,
    val completedSessions: Int = 0
)

sealed interface UiLoadState {
    data object Loading : UiLoadState
    data class Ready(val snapshot: FullAppSnapshot) : UiLoadState
}

class LevelingViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getInstance(application)
    private val prefsStore = UserPreferencesStore(application)
    val repository = LevelingRepository(db, prefsStore)
    val audioEngine = HudAudioEngine()

    private val _events = MutableSharedFlow<HudFeedbackEvent>(extraBufferCapacity = 16)
    val events: SharedFlow<HudFeedbackEvent> = _events.asSharedFlow()

    val uiState: StateFlow<UiLoadState> = repository.appSnapshotFlow
        .onEach { snapshot ->
            audioEngine.updateSettings(
                muted = snapshot.preferences.soundMuted,
                musicEnabled = snapshot.preferences.musicEnabled,
                volume = snapshot.preferences.volume
            )
        }
        .let { flow ->
            val mapped = MutableStateFlow<UiLoadState>(UiLoadState.Loading)
            viewModelScope.launch {
                flow.collect { mapped.value = UiLoadState.Ready(it) }
            }
            mapped
        }

    private val _workoutState = MutableStateFlow<WorkoutSessionState?>(null)
    val workoutState: StateFlow<WorkoutSessionState?> = _workoutState.asStateFlow()
    private var workoutTimerJob: Job? = null

    private val _pomodoroState = MutableStateFlow(PomodoroState())
    val pomodoroState: StateFlow<PomodoroState> = _pomodoroState.asStateFlow()
    private var pomodoroJob: Job? = null

    init {
        viewModelScope.launch {
            repository.ensureSeeded()
        }
    }

    fun completeAwakening(name: String, ageGroup: AgeGroup, fitnessLevel: FitnessLevel) {
        viewModelScope.launch {
            val res = repository.completeAwakening(name, ageGroup, fitnessLevel)
            audioEngine.playLevelUp()
            handleProgressionResult(res, "أيقظت نظامك الشخصي بنجاح!")
        }
    }

    fun updateProfileMeta(name: String, ageGroup: AgeGroup, fitnessLevel: FitnessLevel) {
        viewModelScope.launch {
            repository.updateProfileMeta(name, ageGroup, fitnessLevel)
            _events.tryEmit(HudFeedbackEvent.ToastMessage("تم تحديث بيانات اللاعب بنجاح"))
        }
    }

    fun startQuest(questId: String) {
        viewModelScope.launch { repository.startQuest(questId) }
    }

    fun postponeQuest(questId: String) {
        viewModelScope.launch {
            repository.postponeQuest(questId)
            _events.tryEmit(HudFeedbackEvent.ToastMessage("تم تأجيل المهمة برفق دون خصم أي نقاط"))
        }
    }

    fun completeQuest(questId: String) {
        viewModelScope.launch {
            val res = repository.completeQuest(questId) ?: return@launch
            audioEngine.playQuestCompleted()
            handleProgressionResult(res, "QUEST COMPLETED")
        }
    }

    fun resetDailyQuests() {
        viewModelScope.launch {
            repository.resetDailyQuestsForNewDay()
            _events.tryEmit(HudFeedbackEvent.ToastMessage("تم تجديد المهام اليومية بنجاح"))
        }
    }

    fun addCustomQuest(
        title: String,
        description: String,
        category: String,
        estimatedMinutes: Int,
        xpReward: Int,
        statType: StatType
    ) {
        if (title.isBlank()) return
        viewModelScope.launch {
            repository.addCustomQuest(title, description, category, estimatedMinutes, xpReward, statType)
            _events.tryEmit(HudFeedbackEvent.ToastMessage("تمت إضافة المهمة الجديدة"))
        }
    }

    fun deleteQuest(questId: String) {
        viewModelScope.launch { repository.deleteQuest(questId) }
    }

    // --- Interactive Workout Mode ---

    fun startWorkout(exercises: List<ExerciseInfo>, fitnessLevel: FitnessLevel) {
        if (exercises.isEmpty()) return
        val first = exercises.first()
        val sets = computeAdaptedSets(first, fitnessLevel)
        val reps = computeAdaptedRepsOrSeconds(first, fitnessLevel)
        workoutTimerJob?.cancel()
        _workoutState.value = WorkoutSessionState(
            queue = exercises,
            currentIndex = 0,
            currentSet = 1,
            totalSets = sets,
            targetRepsOrSeconds = reps,
            isTimedExercise = first.isTimedSeconds,
            isResting = false,
            remainingSeconds = if (first.isTimedSeconds) reps else 0,
            timerRunning = false,
            completedExercisesCount = 0,
            totalXpEarned = 0,
            sessionFinished = false
        )
    }

    fun startSingleExerciseWorkout(exercise: ExerciseInfo, fitnessLevel: FitnessLevel) {
        val related = FitnessCatalog.exercisesForCategory(exercise.category)
        val queue = listOf(exercise) + related.filter { it.id != exercise.id }.take(2)
        startWorkout(queue, fitnessLevel)
    }

    fun toggleWorkoutTimer() {
        val state = _workoutState.value ?: return
        if (state.timerRunning) {
            workoutTimerJob?.cancel()
            _workoutState.value = state.copy(timerRunning = false)
            return
        }
        val secondsToRun = if (state.remainingSeconds > 0) state.remainingSeconds else {
            val ex = state.queue[state.currentIndex]
            if (state.isResting) ex.restSeconds else state.targetRepsOrSeconds
        }
        _workoutState.value = state.copy(remainingSeconds = secondsToRun, timerRunning = true)
        workoutTimerJob?.cancel()
        workoutTimerJob = viewModelScope.launch {
            while (isActive) {
                delay(1000L)
                val curr = _workoutState.value ?: break
                if (!curr.timerRunning) break
                val next = curr.remainingSeconds - 1
                if (next <= 0) {
                    audioEngine.playTimerDone()
                    if (curr.isResting) {
                        val ex = curr.queue[curr.currentIndex]
                        _workoutState.value = curr.copy(
                            isResting = false,
                            timerRunning = false,
                            remainingSeconds = if (ex.isTimedSeconds) curr.targetRepsOrSeconds else 0
                        )
                    } else {
                        _workoutState.value = curr.copy(
                            remainingSeconds = 0,
                            timerRunning = false
                        )
                    }
                    break
                } else {
                    _workoutState.value = curr.copy(remainingSeconds = next)
                }
            }
        }
    }

    fun completeCurrentSet(fitnessLevel: FitnessLevel) {
        val state = _workoutState.value ?: return
        val ex = state.queue[state.currentIndex]
        workoutTimerJob?.cancel()
        if (state.currentSet < state.totalSets) {
            _workoutState.value = state.copy(
                currentSet = state.currentSet + 1,
                isResting = true,
                remainingSeconds = ex.restSeconds,
                timerRunning = false
            )
            toggleWorkoutTimer()
        } else {
            // Completed all sets for current exercise
            viewModelScope.launch {
                val res = repository.recordWorkoutSession(ex, state.totalSets, state.targetRepsOrSeconds)
                audioEngine.playQuestCompleted()
                handleProgressionResult(res, "أنهيت تمرين ${ex.titleAr}")
                val curr = _workoutState.value ?: return@launch
                if (curr.currentIndex + 1 < curr.queue.size) {
                    moveToExerciseIndex(curr.currentIndex + 1, fitnessLevel, addCompleted = true, earnedXp = res.xpAdded)
                } else {
                    _workoutState.value = curr.copy(
                        completedExercisesCount = curr.completedExercisesCount + 1,
                        totalXpEarned = curr.totalXpEarned + res.xpAdded,
                        sessionFinished = true,
                        isResting = false,
                        timerRunning = false
                    )
                }
            }
        }
    }

    fun skipRest() {
        val state = _workoutState.value ?: return
        val ex = state.queue[state.currentIndex]
        workoutTimerJob?.cancel()
        _workoutState.value = state.copy(
            isResting = false,
            timerRunning = false,
            remainingSeconds = if (ex.isTimedSeconds) state.targetRepsOrSeconds else 0
        )
    }

    fun nextExerciseInWorkout(fitnessLevel: FitnessLevel) {
        val state = _workoutState.value ?: return
        if (state.currentIndex + 1 < state.queue.size) {
            moveToExerciseIndex(state.currentIndex + 1, fitnessLevel, addCompleted = false, earnedXp = 0)
        } else {
            finishWorkoutSessionEarly()
        }
    }

    fun previousExerciseInWorkout(fitnessLevel: FitnessLevel) {
        val state = _workoutState.value ?: return
        if (state.currentIndex > 0) {
            moveToExerciseIndex(state.currentIndex - 1, fitnessLevel, addCompleted = false, earnedXp = 0)
        }
    }

    fun finishWorkoutSessionEarly() {
        workoutTimerJob?.cancel()
        val state = _workoutState.value ?: return
        _workoutState.value = state.copy(sessionFinished = true, timerRunning = false, isResting = false)
    }

    fun closeWorkoutSession() {
        workoutTimerJob?.cancel()
        _workoutState.value = null
    }

    private fun moveToExerciseIndex(
        targetIndex: Int,
        fitnessLevel: FitnessLevel,
        addCompleted: Boolean,
        earnedXp: Int
    ) {
        val state = _workoutState.value ?: return
        workoutTimerJob?.cancel()
        val ex = state.queue[targetIndex]
        val sets = computeAdaptedSets(ex, fitnessLevel)
        val reps = computeAdaptedRepsOrSeconds(ex, fitnessLevel)
        _workoutState.value = state.copy(
            currentIndex = targetIndex,
            currentSet = 1,
            totalSets = sets,
            targetRepsOrSeconds = reps,
            isTimedExercise = ex.isTimedSeconds,
            isResting = false,
            remainingSeconds = if (ex.isTimedSeconds) reps else 0,
            timerRunning = false,
            completedExercisesCount = state.completedExercisesCount + if (addCompleted) 1 else 0,
            totalXpEarned = state.totalXpEarned + earnedXp
        )
    }

    private fun computeAdaptedSets(ex: ExerciseInfo, level: FitnessLevel): Int {
        return when (level) {
            FitnessLevel.BEGINNER -> ex.defaultSets.coerceAtMost(2)
            FitnessLevel.INTERMEDIATE -> ex.defaultSets
            FitnessLevel.ADVANCED -> (ex.defaultSets + 1).coerceAtMost(4)
        }
    }

    private fun computeAdaptedRepsOrSeconds(ex: ExerciseInfo, level: FitnessLevel): Int {
        return (ex.defaultRepsOrSeconds * level.repScale).toInt().coerceAtLeast(5)
    }

    // --- Glow Up, Sleep & Mind ---

    fun toggleChecklistItem(itemKey: String, checked: Boolean, xpValue: Int) {
        viewModelScope.launch {
            val res = repository.toggleChecklistItem(itemKey, checked, xpValue)
            if (res != null) {
                audioEngine.playQuestCompleted()
                handleProgressionResult(res, "تقدم في العناية الشخصية")
            }
        }
    }

    fun recordSleepLog(
        dateLabel: String,
        bedTimeText: String,
        wakeTimeText: String,
        durationMinutes: Int,
        qualityRating: Int
    ) {
        viewModelScope.launch {
            val res = repository.recordSleepLog(dateLabel, bedTimeText, wakeTimeText, durationMinutes, qualityRating)
            audioEngine.playQuestCompleted()
            handleProgressionResult(res, "تم تسجيل سجل النوم")
        }
    }

    fun deleteSleepLog(id: Long) {
        viewModelScope.launch { repository.deleteSleepLog(id) }
    }

    fun updateBedtimeReminder(enabled: Boolean, hour: Int, minute: Int) {
        viewModelScope.launch {
            repository.updateBedtimeReminder(enabled, hour, minute)
            _events.tryEmit(HudFeedbackEvent.ToastMessage("تم حفظ إعدادات تذكير النوم"))
        }
    }

    // --- Pomodoro Timer ---

    fun updatePomodoroConfig(focusMinutes: Int, breakMinutes: Int) {
        pomodoroJob?.cancel()
        val f = focusMinutes.coerceIn(10, 60)
        val b = breakMinutes.coerceIn(3, 20)
        _pomodoroState.value = _pomodoroState.value.copy(
            focusMinutes = f,
            breakMinutes = b,
            isBreakPhase = false,
            remainingSeconds = f * 60,
            isRunning = false
        )
    }

    fun togglePomodoroRun() {
        val curr = _pomodoroState.value
        if (curr.isRunning) {
            pomodoroJob?.cancel()
            _pomodoroState.value = curr.copy(isRunning = false)
            return
        }
        _pomodoroState.value = curr.copy(isRunning = true)
        pomodoroJob?.cancel()
        pomodoroJob = viewModelScope.launch {
            while (isActive) {
                delay(1000L)
                val s = _pomodoroState.value
                if (!s.isRunning) break
                val next = s.remainingSeconds - 1
                if (next <= 0) {
                    audioEngine.playTimerDone()
                    if (!s.isBreakPhase) {
                        val res = repository.completePomodoroSession(s.focusMinutes)
                        handleProgressionResult(res, "اكتملت جلسة التركيز")
                        _pomodoroState.value = s.copy(
                            isBreakPhase = true,
                            remainingSeconds = s.breakMinutes * 60,
                            isRunning = false,
                            completedSessions = s.completedSessions + 1
                        )
                    } else {
                        _pomodoroState.value = s.copy(
                            isBreakPhase = false,
                            remainingSeconds = s.focusMinutes * 60,
                            isRunning = false
                        )
                    }
                    break
                } else {
                    _pomodoroState.value = s.copy(remainingSeconds = next)
                }
            }
        }
    }

    fun resetPomodoro() {
        pomodoroJob?.cancel()
        val s = _pomodoroState.value
        _pomodoroState.value = s.copy(
            isBreakPhase = false,
            remainingSeconds = s.focusMinutes * 60,
            isRunning = false
        )
    }

    // --- Rewards, Bosses, Audio, Video Admin, Backup ---

    fun toggleBossChallenge(id: String) {
        viewModelScope.launch { repository.toggleJoinBossChallenge(id) }
    }

    fun purchaseOrEquipReward(item: RewardItemDefinition) {
        viewModelScope.launch {
            val err = repository.purchaseOrEquipReward(item)
            if (err != null) {
                _events.tryEmit(HudFeedbackEvent.ToastMessage(err))
            } else {
                audioEngine.playAchievementUnlocked()
                _events.tryEmit(HudFeedbackEvent.ToastMessage("تم تفعيل: ${item.titleAr}"))
            }
        }
    }

    fun setAudioSettings(muted: Boolean, musicEnabled: Boolean, volume: Float) {
        viewModelScope.launch {
            repository.updateAudioPreferences(muted, musicEnabled, volume)
        }
    }

    fun saveVideoItem(
        id: String?,
        title: String,
        category: VideoSectionCategory,
        linkedContentId: String,
        sourceUrl: String,
        description: String
    ) {
        if (title.isBlank()) return
        viewModelScope.launch {
            val saved = repository.saveVideoItem(id, title, category, linkedContentId, sourceUrl, description)
            val msg = if (saved.embedAllowed) {
                "تم حفظ الفيديو والتحقق من صلاحية التضمين"
            } else {
                "تم الحفظ مع تفعيل الدليل التفاعلي البديل (الرابط غير قابل للتضمين)"
            }
            _events.tryEmit(HudFeedbackEvent.ToastMessage(msg))
        }
    }

    fun deleteVideoItem(id: String) {
        viewModelScope.launch {
            repository.deleteVideoItem(id)
            _events.tryEmit(HudFeedbackEvent.ToastMessage("تم حذف عنصر الفيديو"))
        }
    }

    suspend fun exportBackupJson(): String = repository.exportJsonBackup()

    fun importBackupJson(json: String) {
        viewModelScope.launch {
            val ok = repository.importJsonBackup(json)
            _events.tryEmit(
                HudFeedbackEvent.ToastMessage(
                    if (ok) "تم استيراد بيانات اللاعب بنجاح" else "ملف النسخة الاحتياطية غير صالح"
                )
            )
        }
    }

    fun resetAllData() {
        viewModelScope.launch {
            repository.resetAllData()
            _events.tryEmit(HudFeedbackEvent.ToastMessage("تمت إعادة تعيين جميع البيانات إلى المستوى الأول"))
        }
    }

    private fun handleProgressionResult(res: ProgressionGrantResult, label: String) {
        if (res.xpAdded > 0) {
            _events.tryEmit(HudFeedbackEvent.QuestCompleted(res.xpAdded, res.coinsAdded, label))
        }
        if (res.newLevel > res.previousLevel) {
            audioEngine.playLevelUp()
            _events.tryEmit(HudFeedbackEvent.LevelUp(res.newLevel))
        }
        res.newlyUnlockedAchievements.forEach { ach ->
            audioEngine.playAchievementUnlocked()
            _events.tryEmit(HudFeedbackEvent.AchievementUnlocked(ach))
        }
    }

    override fun onCleared() {
        super.onCleared()
        workoutTimerJob?.cancel()
        pomodoroJob?.cancel()
        audioEngine.release()
    }
}
