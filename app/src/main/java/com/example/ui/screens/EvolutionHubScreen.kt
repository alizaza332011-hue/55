package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Canvas
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.AgeGroup
import com.example.data.FullAppSnapshot
import com.example.data.GlowUpLesson
import com.example.data.GlowUpSection
import com.example.data.MotionPattern
import com.example.data.NutritionCategory
import com.example.data.WellnessCatalog
import com.example.ui.PomodoroState
import com.example.ui.components.EmbeddedMediaPlayer
import com.example.ui.components.HolographicGlassCard
import com.example.ui.components.HudSectionHeader
import com.example.ui.components.HudTagChip
import com.example.ui.theme.CharcoalVoid
import com.example.ui.theme.LocalHudPalette
import com.example.ui.theme.OrbitronFontFamily
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun EvolutionHubScreen(
    snapshot: FullAppSnapshot,
    selectedSubTabKey: String,
    pomodoroState: PomodoroState,
    extractYoutubeId: (String) -> String?,
    onSelectSubTab: (String) -> Unit,
    onToggleChecklist: (key: String, checked: Boolean, xp: Int) -> Unit,
    onRecordSleep: (dateLabel: String, bed: String, wake: String, mins: Int, quality: Int) -> Unit,
    onDeleteSleep: (Long) -> Unit,
    onUpdateBedtimeReminder: (enabled: Boolean, hour: Int, minute: Int) -> Unit,
    onUpdatePomodoroConfig: (focusMins: Int, breakMins: Int) -> Unit,
    onTogglePomodoro: () -> Unit,
    onResetPomodoro: () -> Unit
) {
    val hud = LocalHudPalette.current
    val tabs = listOf(
        "glowup" to "المظهر",
        "sleep" to "النوم",
        "nutrition" to "التغذية",
        "mind" to "العقل"
    )
    val selectedIndex = tabs.indexOfFirst { it.first == selectedSubTabKey }.coerceAtLeast(0)

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 640.dp)
                .fillMaxSize()
        ) {
            Spacer(modifier = Modifier.height(10.dp))
            Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                HudSectionHeader(titleAr = "مركز التطور الشامل", badgeEn = "EVOLUTION CENTER")
            }
            Spacer(modifier = Modifier.height(8.dp))

            TabRow(
                selectedTabIndex = selectedIndex,
                containerColor = Color.Transparent,
                contentColor = hud.accent,
                indicator = { tabPositions ->
                    if (selectedIndex < tabPositions.size) {
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedIndex]),
                            color = hud.accent
                        )
                    }
                }
            ) {
                tabs.forEachIndexed { idx, (key, title) ->
                    val icon = when (key) {
                        "glowup" -> Icons.Filled.Spa
                        "sleep" -> Icons.Filled.Bedtime
                        "nutrition" -> Icons.Filled.Restaurant
                        else -> Icons.Filled.Psychology
                    }
                    Tab(
                        selected = selectedIndex == idx,
                        onClick = { onSelectSubTab(key) },
                        text = { Text(title, style = MaterialTheme.typography.labelLarge) },
                        icon = { Icon(icon, contentDescription = title, modifier = Modifier.size(18.dp)) },
                        modifier = Modifier.testTag("evo_tab_$key")
                    )
                }
            }

            when (selectedSubTabKey) {
                "sleep" -> SleepSystemTab(
                    snapshot = snapshot,
                    onToggleChecklist = onToggleChecklist,
                    onRecordSleep = onRecordSleep,
                    onDeleteSleep = onDeleteSleep,
                    onUpdateBedtimeReminder = onUpdateBedtimeReminder
                )
                "nutrition" -> NutritionSystemTab()
                "mind" -> MindDisciplineTab(
                    snapshot = snapshot,
                    pomodoro = pomodoroState,
                    extractYoutubeId = extractYoutubeId,
                    onUpdateConfig = onUpdatePomodoroConfig,
                    onToggleRun = onTogglePomodoro,
                    onReset = onResetPomodoro
                )
                else -> GlowUpCenterTab(
                    snapshot = snapshot,
                    extractYoutubeId = extractYoutubeId,
                    onToggleChecklist = onToggleChecklist
                )
            }
        }
    }
}

@Composable
private fun GlowUpCenterTab(
    snapshot: FullAppSnapshot,
    extractYoutubeId: (String) -> String?,
    onToggleChecklist: (String, Boolean, Int) -> Unit
) {
    val hud = LocalHudPalette.current
    var selectedSection by remember { mutableStateOf(GlowUpSection.SKINCARE) }
    var activeLesson by remember { mutableStateOf<GlowUpLesson?>(null) }

    if (activeLesson != null) {
        val lesson = activeLesson!!
        BackHandler { activeLesson = null }
        val matchedVideo = snapshot.videos.find { it.linkedContentId == lesson.id }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { activeLesson = null }, modifier = Modifier.size(48.dp)) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "رجوع", tint = hud.accent)
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(lesson.section.codeEn, style = MaterialTheme.typography.labelSmall, color = hud.accent, fontFamily = OrbitronFontFamily)
                    Text(lesson.titleAr, style = MaterialTheme.typography.headlineMedium, color = TextPrimary)
                }
            }
            Spacer(modifier = Modifier.height(10.dp))

            if (matchedVideo != null) {
                EmbeddedMediaPlayer(
                    video = matchedVideo,
                    motionPattern = MotionPattern.SHOULDER_CIRCLES,
                    extractYoutubeId = extractYoutubeId
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            HolographicGlassCard(modifier = Modifier.fillMaxWidth()) {
                Column {
                    Text(lesson.summaryAr, style = MaterialTheme.typography.bodyLarge, color = TextPrimary)
                    Spacer(modifier = Modifier.height(10.dp))
                    Text("خطوات التنفيذ:", style = MaterialTheme.typography.titleMedium, color = hud.accent)
                    Spacer(modifier = Modifier.height(6.dp))
                    lesson.stepsAr.forEachIndexed { idx, s ->
                        Text("${idx + 1}. $s", style = MaterialTheme.typography.bodyLarge, color = TextPrimary)
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("ملاحظة سلامة ووعي: ${lesson.safetyNoteAr}", style = MaterialTheme.typography.bodyMedium, color = hud.gold)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HolographicGlassCard(glowAccent = true, modifier = Modifier.fillMaxWidth()) {
                Column {
                    Text("قائمة مهام الدرس (+${lesson.xpPerTask} XP لكل خطوة)", style = MaterialTheme.typography.titleMedium, color = hud.accent)
                    Spacer(modifier = Modifier.height(6.dp))
                    lesson.checklistItems.forEachIndexed { i, item ->
                        val key = "glow_${lesson.id}_$i"
                        val checked = snapshot.checklistMap[key] == true
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)
                        ) {
                            Checkbox(
                                checked = checked,
                                onCheckedChange = { onToggleChecklist(key, it, lesson.xpPerTask) },
                                colors = CheckboxDefaults.colors(checkedColor = hud.success)
                            )
                            Text(item, style = MaterialTheme.typography.bodyLarge, color = TextPrimary)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
        return
    }

    val lessons = remember(selectedSection) {
        WellnessCatalog.glowUpLessons.filter { it.section == selectedSection }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Image(
                painter = painterResource(id = R.drawable.img_glowup_banner),
                contentDescription = "مركز تطوير المظهر",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(115.dp)
                    .clip(RoundedCornerShape(16.dp))
            )
            Spacer(modifier = Modifier.height(10.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(GlowUpSection.entries) { section ->
                    FilterChip(
                        selected = selectedSection == section,
                        onClick = { selectedSection = section },
                        label = { Text("${section.titleAr} (${section.codeEn})") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = hud.accent.copy(alpha = 0.22f),
                            selectedLabelColor = hud.accent
                        )
                    )
                }
            }
        }
        items(lessons, key = { it.id }) { lesson ->
            val completedTasks = lesson.checklistItems.indices.count { i ->
                snapshot.checklistMap["glow_${lesson.id}_$i"] == true
            }
            HolographicGlassCard(
                onClick = { activeLesson = lesson },
                modifier = Modifier.fillMaxWidth().testTag("glow_lesson_${lesson.id}")
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        HudTagChip(text = lesson.section.codeEn, color = hud.accent)
                        HudTagChip(
                            text = "مهام: $completedTasks/${lesson.checklistItems.size}",
                            color = if (completedTasks == lesson.checklistItems.size) hud.success else hud.gold
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(lesson.titleAr, style = MaterialTheme.typography.titleLarge, color = TextPrimary)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(lesson.summaryAr, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                }
            }
        }
    }
}

@Composable
private fun SleepSystemTab(
    snapshot: FullAppSnapshot,
    onToggleChecklist: (String, Boolean, Int) -> Unit,
    onRecordSleep: (String, String, String, Int, Int) -> Unit,
    onDeleteSleep: (Long) -> Unit,
    onUpdateBedtimeReminder: (Boolean, Int, Int) -> Unit
) {
    val hud = LocalHudPalette.current
    val ageGroup = AgeGroup.entries.find { it.name == snapshot.profile.ageGroupName } ?: AgeGroup.YOUNG_ADULT_18_25

    var bedHour by remember { mutableIntStateOf(22) }
    var bedMin by remember { mutableIntStateOf(30) }
    var wakeHour by remember { mutableIntStateOf(6) }
    var wakeMin by remember { mutableIntStateOf(45) }
    var quality by remember { mutableIntStateOf(4) }

    val bedTotal = bedHour * 60 + bedMin
    val wakeTotal = wakeHour * 60 + wakeMin
    val durationMinutes = if (wakeTotal >= bedTotal) wakeTotal - bedTotal else (24 * 60 - bedTotal) + wakeTotal
    val hours = durationMinutes / 60
    val mins = durationMinutes % 60
    val durationHoursFloat = durationMinutes / 60f

    val dateLabel = remember {
        SimpleDateFormat("EEE d MMM", Locale("ar")).format(Date())
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            HolographicGlassCard(glowAccent = true) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("حاسبة وتسجيل النوم", style = MaterialTheme.typography.titleLarge, color = TextPrimary)
                        HudTagChip(text = "الهدف الإرشادي: ${ageGroup.sleepRangeLabel}", color = hud.accent)
                    }
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        TimeStepperColumn(
                            label = "وقت النوم",
                            hour = bedHour,
                            minute = bedMin,
                            onChange = { h, m -> bedHour = h; bedMin = m },
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        TimeStepperColumn(
                            label = "وقت الاستيقاظ",
                            hour = wakeHour,
                            minute = wakeMin,
                            onChange = { h, m -> wakeHour = h; wakeMin = m },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "مدة النوم المحسوبة: $hours ساعة و $mins دقيقة",
                        style = MaterialTheme.typography.titleMedium,
                        color = if (durationHoursFloat in ageGroup.minSleepHours..ageGroup.maxSleepHours) hud.success else hud.gold
                    )
                    Text(
                        text = "الراحة والنوم الكافي يعززان إحصائية التعافي (RECOVERY) — لا عقاب على السهر الاضطراري أو المرض.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text("جودة الراحة:", style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
                        (1..5).forEach { q ->
                            FilterChip(
                                selected = quality == q,
                                onClick = { quality = q },
                                label = { Text("$q") }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = {
                            val bedStr = "%02d:%02d".format(bedHour, bedMin)
                            val wakeStr = "%02d:%02d".format(wakeHour, wakeMin)
                            onRecordSleep(dateLabel, bedStr, wakeStr, durationMinutes, quality)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = hud.accent),
                        modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp).testTag("save_sleep_log_btn")
                    ) {
                        Text("حفظ سجل النوم (+RECOVERY XP)", color = CharcoalVoid, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Weekly chart + Bedtime reminder
        item {
            val recentLogs = snapshot.sleepLogs.take(7).reversed()
            HolographicGlassCard {
                Column {
                    Text("الجدول الأسبوعي للنوم", style = MaterialTheme.typography.titleMedium, color = hud.accent)
                    Spacer(modifier = Modifier.height(8.dp))
                    if (recentLogs.isEmpty()) {
                        Text("سجل أول ليلة نوم ليظهر الرسم الأسبوعي هنا.", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                    } else {
                        Canvas(modifier = Modifier.fillMaxWidth().height(120.dp)) {
                            val count = recentLogs.size.coerceAtLeast(1)
                            val barW = (size.width / (count * 2f)).coerceAtMost(42f)
                            val maxMins = 660f // 11 hours
                            recentLogs.forEachIndexed { idx, log ->
                                val ratio = (log.durationMinutes / maxMins).coerceIn(0.1f, 1f)
                                val barH = size.height * 0.85f * ratio
                                val x = (idx + 0.5f) * (size.width / count) - barW / 2f
                                val barColor = if (log.durationMinutes >= (ageGroup.minSleepHours * 60)) hud.success else hud.accent
                                drawRoundRect(
                                    color = barColor,
                                    topLeft = Offset(x, size.height - barH),
                                    size = Size(barW, barH),
                                    cornerRadius = CornerRadius(10f, 10f)
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("تذكير الاستعداد للنوم", style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                            Text(
                                "الموعد المفضل: %02d:%02d".format(snapshot.preferences.bedtimeHour, snapshot.preferences.bedtimeMinute),
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSecondary
                            )
                        }
                        Switch(
                            checked = snapshot.preferences.bedtimeReminderEnabled,
                            onCheckedChange = {
                                onUpdateBedtimeReminder(it, snapshot.preferences.bedtimeHour, snapshot.preferences.bedtimeMinute)
                            }
                        )
                    }
                }
            }
        }

        // Pre-sleep routine & tips
        item {
            HolographicGlassCard {
                Column {
                    Text("روتين ما قبل النوم (+15 XP)", style = MaterialTheme.typography.titleMedium, color = hud.accent)
                    Spacer(modifier = Modifier.height(6.dp))
                    WellnessCatalog.sleepPreRoutineItems.forEachIndexed { idx, step ->
                        val key = "sleep_routine_$idx"
                        val checked = snapshot.checklistMap[key] == true
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)) {
                            Checkbox(
                                checked = checked,
                                onCheckedChange = { onToggleChecklist(key, it, 15) },
                                colors = CheckboxDefaults.colors(checkedColor = hud.success)
                            )
                            Text(step, style = MaterialTheme.typography.bodyLarge, color = TextPrimary)
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text("نصائح تحسين جودة النوم", style = MaterialTheme.typography.titleMedium, color = hud.gold)
                    Spacer(modifier = Modifier.height(4.dp))
                    WellnessCatalog.sleepQualityTips.forEach { tip ->
                        Text("• $tip", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                        Spacer(modifier = Modifier.height(3.dp))
                    }
                }
            }
        }

        if (snapshot.sleepLogs.isNotEmpty()) {
            items(snapshot.sleepLogs, key = { it.id }) { log ->
                HolographicGlassCard(contentPadding = 12.dp) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("${log.dateLabel} — ${log.durationMinutes / 60}س ${log.durationMinutes % 60}د", style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                            Text("النوم: ${log.bedTimeText} • الاستيقاظ: ${log.wakeTimeText} • الجودة: ${log.qualityRating}/5", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                        }
                        IconButton(onClick = { onDeleteSleep(log.id) }, modifier = Modifier.size(48.dp)) {
                            Icon(Icons.Filled.DeleteOutline, contentDescription = "حذف السجل", tint = TextSecondary)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TimeStepperColumn(
    label: String,
    hour: Int,
    minute: Int,
    onChange: (Int, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val hud = LocalHudPalette.current
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
        Spacer(modifier = Modifier.height(4.dp))
        Text("%02d:%02d".format(hour, minute), style = MaterialTheme.typography.headlineMedium, color = hud.accent, fontFamily = OrbitronFontFamily)
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            IconButton(
                onClick = {
                    val total = ((hour * 60 + minute - 15) + 1440) % 1440
                    onChange(total / 60, total % 60)
                },
                modifier = Modifier.size(48.dp)
            ) { Icon(Icons.Filled.Remove, contentDescription = "تقديم 15 دقيقة", tint = TextPrimary) }
            IconButton(
                onClick = {
                    val total = (hour * 60 + minute + 15) % 1440
                    onChange(total / 60, total % 60)
                },
                modifier = Modifier.size(48.dp)
            ) { Icon(Icons.Filled.Add, contentDescription = "تأخير 15 دقيقة", tint = TextPrimary) }
        }
    }
}

@Composable
private fun NutritionSystemTab() {
    val hud = LocalHudPalette.current
    var selectedCategory by remember { mutableStateOf<NutritionCategory?>(null) }
    val meals = remember(selectedCategory) {
        if (selectedCategory == null) WellnessCatalog.nutritionMeals
        else WellnessCatalog.nutritionMeals.filter { it.category == selectedCategory }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Image(
                painter = painterResource(id = R.drawable.img_nutrition_bowl),
                contentDescription = "وجبة متوازنة صحية",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .clip(RoundedCornerShape(16.dp))
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "التغذية المتوازنة تدعم نموك ونشاطك الذهني والبدني — لا توجد هنا حميات تجويع أو تقييد قاسٍ للسعرات.",
                style = MaterialTheme.typography.bodyMedium,
                color = hud.success
            )
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    FilterChip(
                        selected = selectedCategory == null,
                        onClick = { selectedCategory = null },
                        label = { Text("الكل") }
                    )
                }
                items(NutritionCategory.entries) { cat ->
                    FilterChip(
                        selected = selectedCategory == cat,
                        onClick = { selectedCategory = cat },
                        label = { Text(cat.titleAr) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = hud.success.copy(alpha = 0.22f),
                            selectedLabelColor = hud.success
                        )
                    )
                }
            }
        }

        items(meals, key = { it.id }) { meal ->
            HolographicGlassCard(modifier = Modifier.fillMaxWidth()) {
                Column {
                    HudTagChip(text = meal.category.titleAr, color = hud.success)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(meal.titleAr, style = MaterialTheme.typography.titleLarge, color = TextPrimary)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("المكونات:", style = MaterialTheme.typography.labelLarge, color = hud.accent)
                    meal.ingredientsAr.forEach { ing ->
                        Text("• $ing", style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("طريقة التحضير:", style = MaterialTheme.typography.labelLarge, color = hud.accent)
                    Text(meal.preparationAr, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("الفوائد الغذائية:", style = MaterialTheme.typography.labelLarge, color = hud.gold)
                    Text(meal.benefitsAr, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("بدائل متاحة:", style = MaterialTheme.typography.labelLarge, color = hud.success)
                    Text(meal.alternativesAr, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                }
            }
        }
    }
}

@Composable
private fun MindDisciplineTab(
    snapshot: FullAppSnapshot,
    pomodoro: PomodoroState,
    extractYoutubeId: (String) -> String?,
    onUpdateConfig: (Int, Int) -> Unit,
    onToggleRun: () -> Unit,
    onReset: () -> Unit
) {
    val hud = LocalHudPalette.current
    val totalSecs = (if (pomodoro.isBreakPhase) pomodoro.breakMinutes else pomodoro.focusMinutes) * 60
    val progress = 1f - (pomodoro.remainingSeconds.toFloat() / totalSecs.coerceAtLeast(1))
    val mins = pomodoro.remainingSeconds / 60
    val secs = pomodoro.remainingSeconds % 60
    val mindVideo = snapshot.videos.find { it.linkedContentId == "mind_focus" }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            HolographicGlassCard(glowAccent = true, modifier = Modifier.fillMaxWidth()) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    HudTagChip(
                        text = if (pomodoro.isBreakPhase) "BREAK PHASE • استراحة" else "POMODORO FOCUS • وضع التركيز",
                        color = if (pomodoro.isBreakPhase) hud.success else hud.accent
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(160.dp)) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            drawArc(
                                color = Color(0xFF1C2742),
                                startAngle = -90f,
                                sweepAngle = 360f,
                                useCenter = false,
                                style = Stroke(width = 14f)
                            )
                            drawArc(
                                color = if (pomodoro.isBreakPhase) hud.success else hud.accent,
                                startAngle = -90f,
                                sweepAngle = 360f * progress.coerceIn(0f, 1f),
                                useCenter = false,
                                style = Stroke(width = 14f, cap = StrokeCap.Round)
                            )
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "%02d:%02d".format(mins, secs),
                                style = MaterialTheme.typography.displayLarge,
                                color = TextPrimary,
                                fontFamily = OrbitronFontFamily
                            )
                            Text(
                                text = "جلسات مكتملة: ${pomodoro.completedSessions}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = hud.gold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text("ضبط المدة (دقائق التركيز / الراحة):", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(15 to 3, 25 to 5, 40 to 8, 50 to 10).forEach { (f, b) ->
                            FilterChip(
                                selected = pomodoro.focusMinutes == f,
                                onClick = { onUpdateConfig(f, b) },
                                label = { Text("${f}/${b}د") }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                        Button(
                            onClick = onToggleRun,
                            colors = ButtonDefaults.buttonColors(containerColor = hud.accent),
                            modifier = Modifier.weight(1f).heightIn(min = 48.dp).testTag("pomodoro_toggle_btn")
                        ) {
                            Icon(if (pomodoro.isRunning) Icons.Filled.Pause else Icons.Filled.PlayArrow, contentDescription = null, tint = CharcoalVoid)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(if (pomodoro.isRunning) "إيقاف مؤقت" else "بدء التركيز", color = CharcoalVoid, fontWeight = FontWeight.Bold)
                        }
                        OutlinedButton(
                            onClick = onReset,
                            modifier = Modifier.heightIn(min = 48.dp).testTag("pomodoro_reset_btn")
                        ) {
                            Icon(Icons.Filled.Refresh, contentDescription = "إعادة ضبط", tint = TextPrimary)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("إعادة", color = TextPrimary)
                        }
                    }
                }
            }
        }

        if (mindVideo != null) {
            item {
                EmbeddedMediaPlayer(
                    video = mindVideo,
                    motionPattern = MotionPattern.STRETCHING,
                    extractYoutubeId = extractYoutubeId
                )
            }
        }

        items(WellnessCatalog.mindGuides, key = { it.id }) { guide ->
            HolographicGlassCard {
                Column {
                    Text(guide.titleAr, style = MaterialTheme.typography.titleLarge, color = hud.accent)
                    Text(guide.subtitleAr, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                    Spacer(modifier = Modifier.height(6.dp))
                    guide.keyPointsAr.forEach { pt ->
                        Text("• $pt", style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("خطوة عملية: ${guide.actionTipAr}", style = MaterialTheme.typography.labelLarge, color = hud.gold)
                }
            }
        }
    }
}
