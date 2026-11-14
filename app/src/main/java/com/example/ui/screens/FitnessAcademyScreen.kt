package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.ExerciseCategory
import com.example.data.ExerciseInfo
import com.example.data.FitnessCatalog
import com.example.data.FitnessLevel
import com.example.data.VideoItemEntity
import com.example.ui.WorkoutSessionState
import com.example.ui.components.EmbeddedMediaPlayer
import com.example.ui.components.GlowingProgressBar
import com.example.ui.components.HolographicGlassCard
import com.example.ui.components.HudSectionHeader
import com.example.ui.components.HudTagChip
import com.example.ui.theme.CharcoalVoid
import com.example.ui.theme.LocalHudPalette
import com.example.ui.theme.OrbitronFontFamily
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun FitnessAcademyScreen(
    videos: List<VideoItemEntity>,
    fitnessLevel: FitnessLevel,
    workoutSession: WorkoutSessionState?,
    extractYoutubeId: (String) -> String?,
    onStartExerciseWorkout: (ExerciseInfo) -> Unit,
    onStartCategoryWorkout: (List<ExerciseInfo>) -> Unit,
    onToggleWorkoutTimer: () -> Unit,
    onCompleteSet: () -> Unit,
    onSkipRest: () -> Unit,
    onNextExercise: () -> Unit,
    onPrevExercise: () -> Unit,
    onFinishWorkoutEarly: () -> Unit,
    onCloseWorkoutSummary: () -> Unit
) {
    var selectedCategory by remember { mutableStateOf<ExerciseCategory?>(null) }
    var selectedExercise by remember { mutableStateOf<ExerciseInfo?>(null) }

    if (workoutSession != null) {
        InteractiveWorkoutModeScreen(
            session = workoutSession,
            videos = videos,
            extractYoutubeId = extractYoutubeId,
            onToggleTimer = onToggleWorkoutTimer,
            onCompleteSet = onCompleteSet,
            onSkipRest = onSkipRest,
            onNext = onNextExercise,
            onPrevious = onPrevExercise,
            onFinishEarly = onFinishWorkoutEarly,
            onCloseSummary = onCloseWorkoutSummary
        )
        return
    }

    if (selectedExercise != null) {
        val exercise = selectedExercise!!
        val matchedVideo = videos.find { it.linkedContentId == exercise.id }
        ExerciseDetailScreen(
            exercise = exercise,
            video = matchedVideo,
            fitnessLevel = fitnessLevel,
            extractYoutubeId = extractYoutubeId,
            onBack = { selectedExercise = null },
            onStartWorkout = { onStartExerciseWorkout(exercise) }
        )
        return
    }

    val hud = LocalHudPalette.current
    val displayedExercises = remember(selectedCategory) {
        FitnessCatalog.exercisesForCategory(selectedCategory)
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 640.dp)
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(14.dp))
            HudSectionHeader(
                titleAr = "أكاديمية التدريب",
                badgeEn = "FITNESS ACADEMY",
                trailing = {
                    HudTagChip(text = "المستوى: ${fitnessLevel.titleAr}", color = hud.gold)
                }
            )
            Spacer(modifier = Modifier.height(10.dp))

            Image(
                painter = painterResource(id = R.drawable.img_fitness_academy),
                contentDescription = "أكاديمية التدريب",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(115.dp)
                    .clip(RoundedCornerShape(16.dp))
            )

            Spacer(modifier = Modifier.height(10.dp))

            // All 10 categories filter bar
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    FilterChip(
                        selected = selectedCategory == null,
                        onClick = { selectedCategory = null },
                        label = { Text("الكل (${FitnessCatalog.exercises.size})") }
                    )
                }
                items(ExerciseCategory.entries) { cat ->
                    FilterChip(
                        selected = selectedCategory == cat,
                        onClick = { selectedCategory = cat },
                        label = { Text(cat.titleAr) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = hud.accent.copy(alpha = 0.22f),
                            selectedLabelColor = hud.accent
                        ),
                        modifier = Modifier.testTag("cat_chip_${cat.name}")
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            if (displayedExercises.isNotEmpty()) {
                Button(
                    onClick = { onStartCategoryWorkout(displayedExercises.take(4)) },
                    colors = ButtonDefaults.buttonColors(containerColor = hud.accent),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 48.dp)
                        .testTag("start_full_workout_btn")
                ) {
                    Icon(Icons.Filled.PlayArrow, contentDescription = null, tint = CharcoalVoid)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "ابدأ جلسة تدريب متكاملة (${displayedExercises.take(4).size} تمارين)",
                        color = CharcoalVoid,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(displayedExercises, key = { it.id }) { ex ->
                    HolographicGlassCard(
                        onClick = { selectedExercise = ex },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("exercise_card_${ex.id}")
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    HudTagChip(text = ex.category.titleAr, color = hud.accent)
                                    HudTagChip(text = ex.difficultyAr, color = hud.gold)
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(ex.titleAr, style = MaterialTheme.typography.titleLarge, color = TextPrimary)
                                Text(
                                    ex.titleEn,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextSecondary,
                                    fontFamily = OrbitronFontFamily
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    "العضلات: ${ex.targetMusclesAr}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TextSecondary
                                )
                            }
                            Icon(
                                imageVector = Icons.Filled.FitnessCenter,
                                contentDescription = "عرض تفاصيل التمرين",
                                tint = hud.accent,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ExerciseDetailScreen(
    exercise: ExerciseInfo,
    video: VideoItemEntity?,
    fitnessLevel: FitnessLevel,
    extractYoutubeId: (String) -> String?,
    onBack: () -> Unit,
    onStartWorkout: () -> Unit
) {
    BackHandler(onBack = onBack)
    val hud = LocalHudPalette.current
    val adaptedSets = when (fitnessLevel) {
        FitnessLevel.BEGINNER -> exercise.defaultSets.coerceAtMost(2)
        FitnessLevel.INTERMEDIATE -> exercise.defaultSets
        FitnessLevel.ADVANCED -> (exercise.defaultSets + 1).coerceAtMost(4)
    }
    val adaptedReps = (exercise.defaultRepsOrSeconds * fitnessLevel.repScale).toInt().coerceAtLeast(5)
    val unitLabel = if (exercise.isTimedSeconds) "ثانية" else "تكرار"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(modifier = Modifier.widthIn(max = 640.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack, modifier = Modifier.size(48.dp).testTag("exercise_back_btn")) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "رجوع", tint = hud.accent)
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(exercise.titleAr, style = MaterialTheme.typography.headlineMedium, color = TextPrimary)
                    Text(exercise.titleEn, style = MaterialTheme.typography.labelSmall, color = hud.accent, fontFamily = OrbitronFontFamily)
                }
                HudTagChip(text = "+${exercise.xpReward} XP", color = hud.gold)
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Embedded video player OR interactive animated diagram fallback
            EmbeddedMediaPlayer(
                video = video,
                motionPattern = exercise.motionPattern,
                extractYoutubeId = extractYoutubeId
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onStartWorkout,
                colors = ButtonDefaults.buttonColors(containerColor = hud.accent),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 52.dp)
                    .testTag("start_exercise_session_btn")
            ) {
                Icon(Icons.Filled.PlayArrow, contentDescription = null, tint = CharcoalVoid)
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "بدء جلسة التدريب ($adaptedSets مجموعات × $adaptedReps $unitLabel)",
                    color = CharcoalVoid,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            DetailBlock("العضلات المستهدفة", exercise.targetMusclesAr)
            DetailBlock("مستوى الصعوبة والأدوات", "الصعوبة: ${exercise.difficultyAr}\nالأدوات المطلوبة: ${exercise.equipmentAr}")
            DetailBlock("وضعية البداية", exercise.startingPositionAr)

            HolographicGlassCard(modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp)) {
                Column {
                    Text("خطوات تنفيذ الحركة بالتسلسل", style = MaterialTheme.typography.titleMedium, color = hud.accent)
                    Spacer(modifier = Modifier.height(6.dp))
                    exercise.stepsAr.forEachIndexed { idx, step ->
                        Text("${idx + 1}. $step", style = MaterialTheme.typography.bodyLarge, color = TextPrimary)
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                }
            }

            DetailBlock("طريقة التنفس", exercise.breathingAr)

            HolographicGlassCard(modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp)) {
                Column {
                    Text("الأخطاء الشائعة وطريقة تصحيحها", style = MaterialTheme.typography.titleMedium, color = hud.gold)
                    Spacer(modifier = Modifier.height(6.dp))
                    exercise.commonMistakesAr.forEachIndexed { i, mistake ->
                        Text("• الخطأ: $mistake", style = MaterialTheme.typography.bodyMedium, color = hud.coral)
                        val fix = exercise.mistakeCorrectionsAr.getOrNull(i)
                        if (fix != null) {
                            Text("  التصحيح: $fix", style = MaterialTheme.typography.bodyMedium, color = hud.success)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                }
            }

            DetailBlock("بديل أسهل للمبتدئين", exercise.beginnerAlternativeAr)
            DetailBlock("تعليمات السلامة", exercise.safetyNoteAr, isWarning = true)
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun DetailBlock(title: String, body: String, isWarning: Boolean = false) {
    val hud = LocalHudPalette.current
    HolographicGlassCard(modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp)) {
        Column {
            Text(title, style = MaterialTheme.typography.titleMedium, color = if (isWarning) hud.coral else hud.accent)
            Spacer(modifier = Modifier.height(4.dp))
            Text(body, style = MaterialTheme.typography.bodyLarge, color = TextPrimary)
        }
    }
}

@Composable
private fun InteractiveWorkoutModeScreen(
    session: WorkoutSessionState,
    videos: List<VideoItemEntity>,
    extractYoutubeId: (String) -> String?,
    onToggleTimer: () -> Unit,
    onCompleteSet: () -> Unit,
    onSkipRest: () -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onFinishEarly: () -> Unit,
    onCloseSummary: () -> Unit
) {
    BackHandler(onBack = onCloseSummary)
    val hud = LocalHudPalette.current

    if (session.sessionFinished) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            HolographicGlassCard(
                glowAccent = true,
                modifier = Modifier.widthIn(max = 520.dp).fillMaxWidth().testTag("workout_summary_card")
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = hud.success, modifier = Modifier.size(56.dp))
                    Spacer(modifier = Modifier.height(10.dp))
                    Text("WORKOUT COMPLETED", style = MaterialTheme.typography.labelSmall, color = hud.accent, fontFamily = OrbitronFontFamily)
                    Text("أحسنت! اكتملت جلسة التدريب", style = MaterialTheme.typography.headlineMedium, color = TextPrimary, textAlign = TextAlign.Center)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("التمارين المنجزة: ${session.completedExercisesCount}", style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                    Text("إجمالي XP المكتسبة: +${session.totalXpEarned} XP", style = MaterialTheme.typography.titleLarge, color = hud.gold, fontFamily = OrbitronFontFamily)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("تذكر شرب الماء وأخذ قسط كافٍ من الراحة والتعافي.", style = MaterialTheme.typography.bodyMedium, color = TextSecondary, textAlign = TextAlign.Center)
                    Spacer(modifier = Modifier.height(18.dp))
                    Button(
                        onClick = onCloseSummary,
                        colors = ButtonDefaults.buttonColors(containerColor = hud.accent),
                        modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp).testTag("close_workout_summary_btn")
                    ) {
                        Text("العودة لأكاديمية التدريب", color = CharcoalVoid, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
        return
    }

    val exercise = session.queue.getOrNull(session.currentIndex) ?: return
    val matchedVideo = videos.find { it.linkedContentId == exercise.id }
    val unitLabel = if (session.isTimedExercise) "ثانية" else "تكرار"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(modifier = Modifier.widthIn(max = 600.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                HudTagChip(text = "تمرين ${session.currentIndex + 1} / ${session.queue.size}", color = hud.accent)
                OutlinedButton(
                    onClick = onFinishEarly,
                    modifier = Modifier.heightIn(min = 48.dp).testTag("finish_workout_btn")
                ) {
                    Icon(Icons.Filled.Close, contentDescription = null, tint = hud.coral, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("إنهاء الجلسة", color = hud.coral)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            GlowingProgressBar(progress = (session.currentIndex + 1).toFloat() / session.queue.size.coerceAtLeast(1))
            Spacer(modifier = Modifier.height(10.dp))

            Text(exercise.titleAr, style = MaterialTheme.typography.headlineMedium, color = TextPrimary)
            Text(exercise.titleEn, style = MaterialTheme.typography.labelSmall, color = hud.accent, fontFamily = OrbitronFontFamily)
            Spacer(modifier = Modifier.height(10.dp))

            EmbeddedMediaPlayer(
                video = matchedVideo,
                motionPattern = exercise.motionPattern,
                extractYoutubeId = extractYoutubeId
            )

            Spacer(modifier = Modifier.height(12.dp))

            HolographicGlassCard(glowAccent = true, modifier = Modifier.fillMaxWidth()) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    if (session.isResting) {
                        HudTagChip(text = "REST TIMER • وقت الراحة", color = hud.gold)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "${session.remainingSeconds}s",
                            style = MaterialTheme.typography.displayLarge,
                            color = hud.gold,
                            fontFamily = OrbitronFontFamily
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            OutlinedButton(onClick = onToggleTimer, modifier = Modifier.heightIn(min = 48.dp)) {
                                Icon(if (session.timerRunning) Icons.Filled.Pause else Icons.Filled.PlayArrow, contentDescription = null, tint = hud.accent)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(if (session.timerRunning) "إيقاف" else "استئناف", color = hud.accent)
                            }
                            Button(
                                onClick = onSkipRest,
                                colors = ButtonDefaults.buttonColors(containerColor = hud.accent),
                                modifier = Modifier.heightIn(min = 48.dp).testTag("skip_rest_btn")
                            ) {
                                Icon(Icons.Filled.SkipNext, contentDescription = null, tint = CharcoalVoid)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("تخطي الراحة", color = CharcoalVoid, fontWeight = FontWeight.Bold)
                            }
                        }
                    } else {
                        Text(
                            text = "المجموعة ${session.currentSet} من ${session.totalSets}",
                            style = MaterialTheme.typography.titleLarge,
                            color = hud.accent
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = if (session.isTimedExercise) "${session.remainingSeconds} ثانية" else "${session.targetRepsOrSeconds} $unitLabel",
                            style = MaterialTheme.typography.displayLarge,
                            color = TextPrimary,
                            fontFamily = OrbitronFontFamily
                        )
                        Text(
                            text = "تنفذ الحركة بدون ألم — البديل: ${exercise.beginnerAlternativeAr}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            if (session.isTimedExercise) {
                                OutlinedButton(
                                    onClick = onToggleTimer,
                                    modifier = Modifier.weight(1f).heightIn(min = 48.dp)
                                ) {
                                    Icon(Icons.Filled.Timer, contentDescription = null, tint = hud.accent)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(if (session.timerRunning) "إيقاف المؤقت" else "بدء المؤقت", color = hud.accent)
                                }
                            }
                            Button(
                                onClick = onCompleteSet,
                                colors = ButtonDefaults.buttonColors(containerColor = hud.success),
                                modifier = Modifier
                                    .weight(1f)
                                    .heightIn(min = 48.dp)
                                    .testTag("complete_set_btn")
                            ) {
                                Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = CharcoalVoid)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("إتمام المجموعة", color = CharcoalVoid, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onPrevious,
                    enabled = session.currentIndex > 0,
                    modifier = Modifier.weight(1f).heightIn(min = 48.dp).testTag("workout_prev_btn")
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("السابق")
                }
                OutlinedButton(
                    onClick = onNext,
                    modifier = Modifier.weight(1f).heightIn(min = 48.dp).testTag("workout_next_btn")
                ) {
                    Text(if (session.currentIndex + 1 < session.queue.size) "التالي" else "إنهاء")
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
