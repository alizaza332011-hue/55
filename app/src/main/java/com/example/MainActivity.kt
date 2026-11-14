package com.example

import android.graphics.Color as AndroidColor
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.AssignmentTurnedIn
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.FitnessLevel
import com.example.ui.HudFeedbackEvent
import com.example.ui.LevelingViewModel
import com.example.ui.UiLoadState
import com.example.ui.components.HolographicBackground
import com.example.ui.components.LevelUpModalOverlay
import com.example.ui.components.QuestCompletedFloatingBanner
import com.example.ui.screens.AwakeningScreen
import com.example.ui.screens.EvolutionHubScreen
import com.example.ui.screens.FitnessAcademyScreen
import com.example.ui.screens.HomeHudScreen
import com.example.ui.screens.ProfileSystemScreen
import com.example.ui.screens.QuestsScreen
import com.example.ui.screens.VideoAdminScreen
import com.example.ui.theme.CharcoalDeep
import com.example.ui.theme.HudAccentTheme
import com.example.ui.theme.LocalHudPalette
import com.example.ui.theme.RealLifeLevelingTheme
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class MainNavTab(
    val route: String,
    val labelRes: Int,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    HOME("home", R.string.nav_home, Icons.Filled.Home, Icons.Outlined.Home),
    QUESTS("quests", R.string.nav_quests, Icons.Filled.AssignmentTurnedIn, Icons.Outlined.AssignmentTurnedIn),
    TRAINING("training", R.string.nav_training, Icons.Filled.FitnessCenter, Icons.Outlined.FitnessCenter),
    EVOLUTION("evolution", R.string.nav_evolution, Icons.Filled.AutoAwesome, Icons.Outlined.AutoAwesome),
    PROFILE("profile", R.string.nav_profile, Icons.Filled.Person, Icons.Outlined.Person)
}

class MainActivity : ComponentActivity() {
    private val viewModel: LevelingViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(AndroidColor.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(AndroidColor.TRANSPARENT)
        )
        setContent {
            RealLifeLevelingApp(viewModel = viewModel)
        }
    }
}

@Composable
fun RealLifeLevelingApp(viewModel: LevelingViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val workoutSession by viewModel.workoutState.collectAsStateWithLifecycle()
    val pomodoroState by viewModel.pomodoroState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val lifecycleOwner = LocalLifecycleOwner.current

    val snapshot = (uiState as? UiLoadState.Ready)?.snapshot
    val accentTheme = HudAccentTheme.fromId(snapshot?.profile?.equippedThemeId ?: "theme_electric_blue")
    val backgroundId = snapshot?.profile?.equippedBackgroundId ?: "bg_obsidian_grid"

    DisposableEffect(lifecycleOwner, snapshot?.preferences) {
        val observer = LifecycleEventObserver { _, event ->
            val prefs = snapshot?.preferences
            when (event) {
                Lifecycle.Event.ON_STOP -> viewModel.audioEngine.stopAmbientMusic()
                Lifecycle.Event.ON_START -> if (prefs != null) {
                    viewModel.audioEngine.updateSettings(prefs.soundMuted, prefs.musicEnabled, prefs.volume)
                }
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    RealLifeLevelingTheme(
        accentTheme = accentTheme,
        backgroundId = backgroundId
    ) {
        val hud = LocalHudPalette.current
        val snackbarHostState = remember { SnackbarHostState() }
        var questBannerData by remember { mutableStateOf<HudFeedbackEvent.QuestCompleted?>(null) }
        var levelUpDialogLevel by remember { mutableStateOf<Int?>(null) }

        LaunchedEffect(viewModel) {
            viewModel.events.collect { ev ->
                when (ev) {
                    is HudFeedbackEvent.QuestCompleted -> {
                        questBannerData = ev
                    }
                    is HudFeedbackEvent.LevelUp -> {
                        levelUpDialogLevel = ev.newLevel
                    }
                    is HudFeedbackEvent.AchievementUnlocked -> {
                        snackbarHostState.showSnackbar("إنجاز مفتوح: ${ev.achievement.titleAr} (+${ev.achievement.xpBonus} XP)")
                    }
                    is HudFeedbackEvent.ToastMessage -> {
                        snackbarHostState.showSnackbar(ev.message)
                    }
                }
            }
        }

        LaunchedEffect(questBannerData) {
            if (questBannerData != null) {
                delay(2400L)
                questBannerData = null
            }
        }

        val exportLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.CreateDocument("application/json")
        ) { uri ->
            if (uri != null) {
                scope.launch {
                    val json = viewModel.exportBackupJson()
                    runCatching {
                        context.contentResolver.openOutputStream(uri)?.use { out ->
                            out.write(json.toByteArray(Charsets.UTF_8))
                        }
                    }.onSuccess {
                        snackbarHostState.showSnackbar("تم تصدير ملف النسخة الاحتياطية بنجاح")
                    }.onFailure {
                        snackbarHostState.showSnackbar("تعذر حفظ ملف التصدير")
                    }
                }
            }
        }

        val importLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.OpenDocument()
        ) { uri ->
            if (uri != null) {
                scope.launch {
                    val text = runCatching {
                        context.contentResolver.openInputStream(uri)?.bufferedReader()?.use { it.readText() }
                    }.getOrNull()
                    if (text != null) {
                        viewModel.importBackupJson(text)
                    } else {
                        snackbarHostState.showSnackbar("تعذر قراءة الملف المختار")
                    }
                }
            }
        }

        when (val state = uiState) {
            UiLoadState.Loading -> {
                HolographicBackground {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = hud.accent)
                    }
                }
            }
            is UiLoadState.Ready -> {
                val data = state.snapshot
                if (!data.profile.awakeningDone) {
                    AwakeningScreen(
                        onAcceptAwakening = { name, ageGroup, fitnessLevel ->
                            viewModel.completeAwakening(name, ageGroup, fitnessLevel)
                        }
                    )
                } else {
                    var currentTab by rememberSaveable { mutableStateOf(MainNavTab.HOME) }
                    var evolutionSubTab by rememberSaveable { mutableStateOf("glowup") }
                    var showVideoAdmin by rememberSaveable { mutableStateOf(false) }

                    val fitnessLevel = FitnessLevel.entries.find { it.name == data.profile.fitnessLevelName }
                        ?: FitnessLevel.BEGINNER

                    HolographicBackground {
                        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                            val isWideScreen = maxWidth >= 600.dp

                            Scaffold(
                                containerColor = Color.Transparent,
                                contentWindowInsets = WindowInsets.safeDrawing,
                                snackbarHost = { SnackbarHost(snackbarHostState) },
                                bottomBar = {
                                    if (!isWideScreen && !showVideoAdmin && workoutSession == null) {
                                        NavigationBar(
                                            containerColor = CharcoalDeep.copy(alpha = 0.94f),
                                            tonalElevation = 8.dp
                                        ) {
                                            MainNavTab.entries.forEach { tab ->
                                                val selected = currentTab == tab
                                                val label = stringResource(tab.labelRes)
                                                NavigationBarItem(
                                                    selected = selected,
                                                    onClick = { currentTab = tab },
                                                    icon = {
                                                        Icon(
                                                            imageVector = if (selected) tab.selectedIcon else tab.unselectedIcon,
                                                            contentDescription = label
                                                        )
                                                    },
                                                    label = { Text(label, maxLines = 1) },
                                                    colors = NavigationBarItemDefaults.colors(
                                                        selectedIconColor = hud.accent,
                                                        selectedTextColor = hud.accent,
                                                        indicatorColor = hud.accent.copy(alpha = 0.16f),
                                                        unselectedIconColor = TextSecondary,
                                                        unselectedTextColor = TextSecondary
                                                    ),
                                                    modifier = Modifier.testTag("bottom_nav_${tab.route}")
                                                )
                                            }
                                        }
                                    }
                                }
                            ) { innerPadding ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(innerPadding)
                                ) {
                                    if (isWideScreen && !showVideoAdmin && workoutSession == null) {
                                        NavigationRail(
                                            containerColor = CharcoalDeep.copy(alpha = 0.92f),
                                            modifier = Modifier.fillMaxHeight()
                                        ) {
                                            Spacer(modifier = Modifier.height(16.dp))
                                            MainNavTab.entries.forEach { tab ->
                                                val selected = currentTab == tab
                                                val label = stringResource(tab.labelRes)
                                                NavigationRailItem(
                                                    selected = selected,
                                                    onClick = { currentTab = tab },
                                                    icon = {
                                                        Icon(
                                                            imageVector = if (selected) tab.selectedIcon else tab.unselectedIcon,
                                                            contentDescription = label
                                                        )
                                                    },
                                                    label = { Text(label) },
                                                    colors = NavigationRailItemDefaults.colors(
                                                        selectedIconColor = hud.accent,
                                                        selectedTextColor = hud.accent,
                                                        indicatorColor = hud.accent.copy(alpha = 0.16f),
                                                        unselectedIconColor = TextSecondary,
                                                        unselectedTextColor = TextSecondary
                                                    ),
                                                    modifier = Modifier.testTag("rail_nav_${tab.route}")
                                                )
                                            }
                                        }
                                    }

                                    Box(modifier = Modifier.weight(1f).fillMaxSize()) {
                                        if (showVideoAdmin) {
                                            VideoAdminScreen(
                                                videos = data.videos,
                                                onSaveVideo = { id, title, cat, linked, url, desc ->
                                                    viewModel.saveVideoItem(id, title, cat, linked, url, desc)
                                                },
                                                onDeleteVideo = { viewModel.deleteVideoItem(it) },
                                                onBack = { showVideoAdmin = false }
                                            )
                                        } else {
                                            when (currentTab) {
                                                MainNavTab.HOME -> HomeHudScreen(
                                                    snapshot = data,
                                                    onToggleMute = {
                                                        viewModel.setAudioSettings(
                                                            muted = !data.preferences.soundMuted,
                                                            musicEnabled = data.preferences.musicEnabled,
                                                            volume = data.preferences.volume
                                                        )
                                                    },
                                                    onToggleBossJoin = { viewModel.toggleBossChallenge(it) },
                                                    onNavigateToQuests = { currentTab = MainNavTab.QUESTS },
                                                    onNavigateToTraining = { currentTab = MainNavTab.TRAINING },
                                                    onNavigateToEvolutionSection = { section ->
                                                        evolutionSubTab = section
                                                        currentTab = MainNavTab.EVOLUTION
                                                    }
                                                )
                                                MainNavTab.QUESTS -> QuestsScreen(
                                                    quests = data.quests,
                                                    onStartQuest = { viewModel.startQuest(it) },
                                                    onCompleteQuest = { viewModel.completeQuest(it) },
                                                    onPostponeQuest = { viewModel.postponeQuest(it) },
                                                    onAddQuest = { t, d, c, m, x, s ->
                                                        viewModel.addCustomQuest(t, d, c, m, x, s)
                                                    },
                                                    onDeleteQuest = { viewModel.deleteQuest(it) },
                                                    onResetDailyQuests = { viewModel.resetDailyQuests() }
                                                )
                                                MainNavTab.TRAINING -> FitnessAcademyScreen(
                                                    videos = data.videos,
                                                    fitnessLevel = fitnessLevel,
                                                    workoutSession = workoutSession,
                                                    extractYoutubeId = { viewModel.repository.extractYoutubeVideoId(it) },
                                                    onStartExerciseWorkout = { viewModel.startSingleExerciseWorkout(it, fitnessLevel) },
                                                    onStartCategoryWorkout = { viewModel.startWorkout(it, fitnessLevel) },
                                                    onToggleWorkoutTimer = { viewModel.toggleWorkoutTimer() },
                                                    onCompleteSet = { viewModel.completeCurrentSet(fitnessLevel) },
                                                    onSkipRest = { viewModel.skipRest() },
                                                    onNextExercise = { viewModel.nextExerciseInWorkout(fitnessLevel) },
                                                    onPrevExercise = { viewModel.previousExerciseInWorkout(fitnessLevel) },
                                                    onFinishWorkoutEarly = { viewModel.finishWorkoutSessionEarly() },
                                                    onCloseWorkoutSummary = { viewModel.closeWorkoutSession() }
                                                )
                                                MainNavTab.EVOLUTION -> EvolutionHubScreen(
                                                    snapshot = data,
                                                    selectedSubTabKey = evolutionSubTab,
                                                    pomodoroState = pomodoroState,
                                                    extractYoutubeId = { viewModel.repository.extractYoutubeVideoId(it) },
                                                    onSelectSubTab = { evolutionSubTab = it },
                                                    onToggleChecklist = { key, checked, xp ->
                                                        viewModel.toggleChecklistItem(key, checked, xp)
                                                    },
                                                    onRecordSleep = { date, bed, wake, mins, q ->
                                                        viewModel.recordSleepLog(date, bed, wake, mins, q)
                                                    },
                                                    onDeleteSleep = { viewModel.deleteSleepLog(it) },
                                                    onUpdateBedtimeReminder = { en, h, m ->
                                                        viewModel.updateBedtimeReminder(en, h, m)
                                                    },
                                                    onUpdatePomodoroConfig = { f, b ->
                                                        viewModel.updatePomodoroConfig(f, b)
                                                    },
                                                    onTogglePomodoro = { viewModel.togglePomodoroRun() },
                                                    onResetPomodoro = { viewModel.resetPomodoro() }
                                                )
                                                MainNavTab.PROFILE -> ProfileSystemScreen(
                                                    snapshot = data,
                                                    onUpdateProfileMeta = { n, a, f ->
                                                        viewModel.updateProfileMeta(n, a, f)
                                                    },
                                                    onPurchaseOrEquipReward = { viewModel.purchaseOrEquipReward(it) },
                                                    onUpdateAudio = { m, mus, vol ->
                                                        viewModel.setAudioSettings(m, mus, vol)
                                                    },
                                                    onOpenVideoAdmin = { showVideoAdmin = true },
                                                    onExportJsonClick = {
                                                        exportLauncher.launch("real_life_leveling_backup.json")
                                                    },
                                                    onImportJsonClick = {
                                                        importLauncher.launch(arrayOf("application/json", "text/plain", "*/*"))
                                                    },
                                                    onResetAllData = { viewModel.resetAllData() }
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // Floating QUEST COMPLETED banner
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .statusBarsPadding()
                                .padding(top = 12.dp),
                            contentAlignment = Alignment.TopCenter
                        ) {
                            QuestCompletedFloatingBanner(
                                visible = questBannerData != null,
                                xp = questBannerData?.xp ?: 0,
                                coins = questBannerData?.coins ?: 0,
                                label = questBannerData?.label ?: ""
                            )
                        }

                        // Level Up Modal Overlay
                        LevelUpModalOverlay(
                            newLevel = levelUpDialogLevel,
                            onDismiss = { levelUpDialogLevel = null }
                        )
                    }
                }
            }
        }
    }
}
