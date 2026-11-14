package com.example.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.QuestEntity
import com.example.data.QuestStatus
import com.example.data.StatType
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
fun QuestsScreen(
    quests: List<QuestEntity>,
    onStartQuest: (String) -> Unit,
    onCompleteQuest: (String) -> Unit,
    onPostponeQuest: (String) -> Unit,
    onAddQuest: (title: String, desc: String, category: String, minutes: Int, xp: Int, stat: StatType) -> Unit,
    onDeleteQuest: (String) -> Unit,
    onResetDailyQuests: () -> Unit
) {
    val hud = LocalHudPalette.current
    var selectedFilter by remember { mutableStateOf<QuestStatus?>(null) }
    var showAddDialog by remember { mutableStateOf(false) }

    val filteredQuests = remember(quests, selectedFilter) {
        if (selectedFilter == null) quests else quests.filter { it.statusName == selectedFilter!!.name }
    }
    val completedCount = quests.count { it.statusName == QuestStatus.COMPLETED.name }
    val progress = if (quests.isEmpty()) 0f else completedCount.toFloat() / quests.size

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
                titleAr = "المهام اليومية",
                badgeEn = "DAILY QUESTS",
                trailing = {
                    Row {
                        IconButton(
                            onClick = onResetDailyQuests,
                            modifier = Modifier.size(48.dp).testTag("reset_quests_btn")
                        ) {
                            Icon(Icons.Filled.Refresh, contentDescription = "تجديد المهام اليومية", tint = TextSecondary)
                        }
                        Button(
                            onClick = { showAddDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = hud.accent),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                            modifier = Modifier.heightIn(min = 48.dp).testTag("open_add_quest_btn")
                        ) {
                            Icon(Icons.Filled.Add, contentDescription = null, tint = CharcoalVoid, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("مهمة", color = CharcoalVoid, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            )

            Spacer(modifier = Modifier.height(10.dp))
            HolographicGlassCard(contentPadding = 12.dp) {
                Column {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("إنجاز اليوم: $completedCount من ${quests.size}", style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                        Text("لا يتم خصم أي نقاط عند التأجيل أو الراحة", style = MaterialTheme.typography.bodyMedium, color = hud.success)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    GlowingProgressBar(progress = progress, fillColor = hud.success)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    FilterChip(
                        selected = selectedFilter == null,
                        onClick = { selectedFilter = null },
                        label = { Text("الكل (${quests.size})") }
                    )
                }
                items(QuestStatus.entries) { status ->
                    val count = quests.count { it.statusName == status.name }
                    FilterChip(
                        selected = selectedFilter == status,
                        onClick = { selectedFilter = status },
                        label = { Text("${status.titleAr} ($count)") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = hud.accent.copy(alpha = 0.2f),
                            selectedLabelColor = hud.accent
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(filteredQuests, key = { it.id }) { quest ->
                    QuestItemCard(
                        quest = quest,
                        onStart = { onStartQuest(quest.id) },
                        onComplete = { onCompleteQuest(quest.id) },
                        onPostpone = { onPostponeQuest(quest.id) },
                        onDelete = if (quest.isCustom) {
                            { onDeleteQuest(quest.id) }
                        } else null
                    )
                }
            }
        }
    }

    if (showAddDialog) {
        AddCustomQuestDialog(
            onDismiss = { showAddDialog = false },
            onSave = { title, desc, cat, mins, xp, stat ->
                onAddQuest(title, desc, cat, mins, xp, stat)
                showAddDialog = false
            }
        )
    }
}

@Composable
private fun QuestItemCard(
    quest: QuestEntity,
    onStart: () -> Unit,
    onComplete: () -> Unit,
    onPostpone: () -> Unit,
    onDelete: (() -> Unit)?
) {
    val hud = LocalHudPalette.current
    val status = QuestStatus.entries.find { it.name == quest.statusName } ?: QuestStatus.PENDING
    val stat = StatType.entries.find { it.name == quest.statTypeName } ?: StatType.DISCIPLINE
    val isCompleted = status == QuestStatus.COMPLETED
    val isInProgress = status == QuestStatus.IN_PROGRESS

    val statusColor = when (status) {
        QuestStatus.COMPLETED -> hud.success
        QuestStatus.IN_PROGRESS -> hud.accent
        QuestStatus.POSTPONED -> hud.gold
        QuestStatus.PENDING -> TextSecondary
    }

    HolographicGlassCard(
        glowAccent = isInProgress,
        modifier = Modifier.fillMaxWidth().testTag("quest_card_${quest.id}")
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                    HudTagChip(text = status.titleAr, color = statusColor)
                    HudTagChip(text = "+${quest.xpReward} XP", color = hud.accent)
                    HudTagChip(text = stat.codeEn, color = hud.secondaryAccent)
                }
                if (onDelete != null) {
                    IconButton(onClick = onDelete, modifier = Modifier.size(40.dp)) {
                        Icon(Icons.Filled.DeleteOutline, contentDescription = "حذف المهمة", tint = TextSecondary)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = quest.title,
                style = MaterialTheme.typography.titleLarge,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = quest.description,
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "التصنيف: ${quest.category}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
                Text(
                    text = "المدة: ${quest.estimatedMinutes} دقيقة • +${quest.coinsReward} عملة",
                    style = MaterialTheme.typography.bodyMedium,
                    color = hud.gold
                )
            }

            if (!isCompleted) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (!isInProgress) {
                        OutlinedButton(
                            onClick = onStart,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .heightIn(min = 48.dp)
                                .testTag("start_quest_${quest.id}")
                        ) {
                            Icon(Icons.Filled.PlayArrow, contentDescription = null, tint = hud.accent, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("بدء", color = hud.accent)
                        }
                    }
                    Button(
                        onClick = onComplete,
                        colors = ButtonDefaults.buttonColors(containerColor = hud.success),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(min = 48.dp)
                            .testTag("complete_quest_${quest.id}")
                    ) {
                        Icon(Icons.Filled.Check, contentDescription = null, tint = CharcoalVoid, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("إكمال", color = CharcoalVoid, fontWeight = FontWeight.Bold)
                    }
                    OutlinedButton(
                        onClick = onPostpone,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(min = 48.dp)
                            .testTag("postpone_quest_${quest.id}")
                    ) {
                        Icon(Icons.Filled.Schedule, contentDescription = null, tint = hud.gold, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("تأجيل", color = hud.gold)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun AddCustomQuestDialog(
    onDismiss: () -> Unit,
    onSave: (String, String, String, Int, Int, StatType) -> Unit
) {
    val hud = LocalHudPalette.current
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var minutes by remember { mutableIntStateOf(15) }
    var xp by remember { mutableIntStateOf(30) }
    var selectedStat by remember { mutableStateOf(StatType.DISCIPLINE) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("إضافة مهمة جديدة", color = TextPrimary) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("اسم المهمة") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("new_quest_title_input")
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("الوصف (اختياري)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Text("المدة التقديرية: $minutes دقيقة | المكافأة: $xp XP", color = hud.accent, style = MaterialTheme.typography.bodyMedium)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(10 to 20, 15 to 30, 30 to 45, 45 to 60).forEach { (m, x) ->
                        FilterChip(
                            selected = minutes == m,
                            onClick = { minutes = m; xp = x },
                            label = { Text("${m}د") }
                        )
                    }
                }
                Text("الإحصائية المرتبطة:", style = MaterialTheme.typography.labelLarge, color = TextPrimary)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    StatType.entries.forEach { st ->
                        FilterChip(
                            selected = selectedStat == st,
                            onClick = { selectedStat = st },
                            label = { Text(st.titleAr) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onSave(title, description, "مهمة شخصية", minutes, xp, selectedStat)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = hud.accent),
                modifier = Modifier.testTag("save_new_quest_btn")
            ) {
                Text("حفظ المهمة", color = CharcoalVoid, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("إلغاء", color = TextSecondary) }
        }
    )
}
