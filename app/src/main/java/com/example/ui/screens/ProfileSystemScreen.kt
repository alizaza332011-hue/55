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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.AgeGroup
import com.example.data.FitnessLevel
import com.example.data.FullAppSnapshot
import com.example.data.ProgressionCatalog
import com.example.data.RewardCategory
import com.example.data.RewardItemDefinition
import com.example.ui.components.HolographicGlassCard
import com.example.ui.components.HudSectionHeader
import com.example.ui.components.HudTagChip
import com.example.ui.theme.CharcoalVoid
import com.example.ui.theme.LocalHudPalette
import com.example.ui.theme.OrbitronFontFamily
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProfileSystemScreen(
    snapshot: FullAppSnapshot,
    onUpdateProfileMeta: (String, AgeGroup, FitnessLevel) -> Unit,
    onPurchaseOrEquipReward: (RewardItemDefinition) -> Unit,
    onUpdateAudio: (muted: Boolean, musicEnabled: Boolean, volume: Float) -> Unit,
    onOpenVideoAdmin: () -> Unit,
    onExportJsonClick: () -> Unit,
    onImportJsonClick: () -> Unit,
    onResetAllData: () -> Unit
) {
    val hud = LocalHudPalette.current
    val profile = snapshot.profile
    var selectedRewardCategory by remember { mutableStateOf<RewardCategory?>(null) }
    var showEditProfileDialog by remember { mutableStateOf(false) }
    var showResetConfirm by remember { mutableStateOf(false) }

    val filteredRewards = remember(selectedRewardCategory) {
        if (selectedRewardCategory == null) ProgressionCatalog.rewardItems
        else ProgressionCatalog.rewardItems.filter { it.category == selectedRewardCategory }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LazyColumn(
            modifier = Modifier
                .widthIn(max = 640.dp)
                .fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                HudSectionHeader(
                    titleAr = "الملف الشخصي والنظام",
                    badgeEn = "PLAYER & SYSTEM",
                    trailing = {
                        HudTagChip(text = "${profile.coins} COINS", color = hud.gold)
                    }
                )
            }

            // Profile edit & summary
            item {
                HolographicGlassCard(glowAccent = true) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(profile.playerName, style = MaterialTheme.typography.headlineMedium, color = TextPrimary)
                                val age = AgeGroup.entries.find { it.name == profile.ageGroupName } ?: AgeGroup.YOUNG_ADULT_18_25
                                val fit = FitnessLevel.entries.find { it.name == profile.fitnessLevelName } ?: FitnessLevel.BEGINNER
                                Text("${age.titleAr} • مستوى اللياقة: ${fit.titleAr}", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                                Text("المهام المكتملة: ${profile.completedQuestsCount} • التمارين المسجلة: ${snapshot.workoutLogs.size}", style = MaterialTheme.typography.bodyMedium, color = hud.accent)
                            }
                            OutlinedButton(
                                onClick = { showEditProfileDialog = true },
                                modifier = Modifier.heightIn(min = 48.dp)
                            ) {
                                Text("تعديل", color = hud.accent)
                            }
                        }
                    }
                }
            }

            // Levels 1-7 Roadmap
            item {
                HolographicGlassCard {
                    Column {
                        Text("مسار المستويات السبعة (LEVELS 1–7)", style = MaterialTheme.typography.titleLarge, color = TextPrimary)
                        Spacer(modifier = Modifier.height(8.dp))
                        ProgressionCatalog.levelTiers.forEach { tier ->
                            val reached = profile.level >= tier.level
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "LEVEL ${tier.level} — ${tier.titleEn} (${tier.titleAr})",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = if (reached) hud.accent else TextSecondary,
                                    fontWeight = if (profile.level == tier.level) FontWeight.Bold else FontWeight.Normal
                                )
                                HudTagChip(
                                    text = if (reached) "مفتوح" else "${tier.minXp} XP",
                                    color = if (reached) hud.success else TextSecondary
                                )
                            }
                        }
                    }
                }
            }

            // Music & Sound Controls
            item {
                HolographicGlassCard {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.MusicNote, contentDescription = null, tint = hud.accent)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("الموسيقى والمؤثرات الصوتية (HUD AUDIO)", style = MaterialTheme.typography.titleLarge, color = TextPrimary)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("كتم جميع الأصوات", style = MaterialTheme.typography.bodyLarge, color = TextPrimary)
                            Switch(
                                checked = snapshot.preferences.soundMuted,
                                onCheckedChange = {
                                    onUpdateAudio(it, snapshot.preferences.musicEnabled, snapshot.preferences.volume)
                                },
                                modifier = Modifier.testTag("settings_mute_switch")
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("موسيقى Dark Cinematic خلفية", style = MaterialTheme.typography.bodyLarge, color = TextPrimary)
                                Text("موسيقى سينمائية أصلية مولدة بدون ملفات محمية", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                            }
                            Switch(
                                checked = snapshot.preferences.musicEnabled,
                                onCheckedChange = {
                                    onUpdateAudio(snapshot.preferences.soundMuted, it, snapshot.preferences.volume)
                                },
                                modifier = Modifier.testTag("settings_music_switch")
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("مستوى الصوت: ${(snapshot.preferences.volume * 100).toInt()}%", style = MaterialTheme.typography.bodyMedium, color = hud.accent)
                        Slider(
                            value = snapshot.preferences.volume,
                            onValueChange = {
                                onUpdateAudio(snapshot.preferences.soundMuted, snapshot.preferences.musicEnabled, it)
                            },
                            colors = SliderDefaults.colors(thumbColor = hud.accent, activeTrackColor = hud.accent)
                        )
                    }
                }
            }

            // Rewards Shop
            item {
                HolographicGlassCard {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.ShoppingBag, contentDescription = null, tint = hud.gold)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("متجر المكافآت (REWARDS SHOP)", style = MaterialTheme.typography.titleLarge, color = TextPrimary)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            FilterChip(
                                selected = selectedRewardCategory == null,
                                onClick = { selectedRewardCategory = null },
                                label = { Text("الكل") }
                            )
                            RewardCategory.entries.forEach { cat ->
                                FilterChip(
                                    selected = selectedRewardCategory == cat,
                                    onClick = { selectedRewardCategory = cat },
                                    label = { Text(cat.titleAr) }
                                )
                            }
                        }
                    }
                }
            }

            items(filteredRewards, key = { it.id }) { item ->
                val isUnlocked = item.id in snapshot.unlockedRewards
                val isEquipped = item.id in setOf(
                    profile.equippedThemeId,
                    profile.equippedBackgroundId,
                    profile.equippedFrameId,
                    profile.equippedTitleId,
                    profile.equippedEffectId
                )
                HolographicGlassCard(
                    glowAccent = isEquipped,
                    contentPadding = 12.dp
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                HudTagChip(text = item.category.titleAr, color = hud.secondaryAccent)
                                if (item.costCoins > 0 && !isUnlocked) {
                                    HudTagChip(text = "${item.costCoins} COINS", color = hud.gold)
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(item.titleAr, style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                            Text(item.descriptionAr, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = { onPurchaseOrEquipReward(item) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = when {
                                    isEquipped -> hud.success
                                    isUnlocked -> hud.accent
                                    else -> hud.gold
                                }
                            ),
                            modifier = Modifier.heightIn(min = 48.dp).testTag("reward_btn_${item.id}")
                        ) {
                            Text(
                                text = when {
                                    isEquipped -> "مُفعّل"
                                    isUnlocked -> "تفعيل"
                                    else -> "فتح"
                                },
                                color = CharcoalVoid,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Achievements
            item {
                HolographicGlassCard {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.EmojiEvents, contentDescription = null, tint = hud.gold)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("الإنجازات (ACHIEVEMENTS)", style = MaterialTheme.typography.titleLarge, color = TextPrimary)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        ProgressionCatalog.achievements.forEach { ach ->
                            val unlocked = ach.id in snapshot.unlockedAchievements
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (unlocked) Icons.Filled.EmojiEvents else Icons.Filled.Lock,
                                    contentDescription = ach.titleAr,
                                    tint = if (unlocked) hud.gold else TextSecondary,
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "${ach.titleAr} (${ach.titleEn})",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = if (unlocked) TextPrimary else TextSecondary
                                    )
                                    Text(ach.descriptionAr, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                                }
                                HudTagChip(text = "+${ach.xpBonus} XP", color = if (unlocked) hud.success else TextSecondary)
                            }
                        }
                    }
                }
            }

            // Video Library Admin & Data Backup / Sync Info
            item {
                HolographicGlassCard {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("إدارة النظام والمحتوى والبيانات", style = MaterialTheme.typography.titleLarge, color = TextPrimary)

                        Button(
                            onClick = onOpenVideoAdmin,
                            colors = ButtonDefaults.buttonColors(containerColor = hud.accent),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp).testTag("open_video_admin_btn")
                        ) {
                            Icon(Icons.Filled.VideoLibrary, contentDescription = null, tint = CharcoalVoid)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("إدارة مكتبة الفيديوهات (Admin)", color = CharcoalVoid, fontWeight = FontWeight.Bold)
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = onExportJsonClick,
                                modifier = Modifier.weight(1f).heightIn(min = 48.dp).testTag("export_json_btn")
                            ) {
                                Icon(Icons.Filled.Upload, contentDescription = null, tint = hud.accent, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("تصدير JSON", color = hud.accent)
                            }
                            OutlinedButton(
                                onClick = onImportJsonClick,
                                modifier = Modifier.weight(1f).heightIn(min = 48.dp).testTag("import_json_btn")
                            ) {
                                Icon(Icons.Filled.Download, contentDescription = null, tint = hud.success, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("استيراد JSON", color = hud.success)
                            }
                        }

                        HolographicGlassCard(contentPadding = 12.dp) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Filled.CloudOff, contentDescription = null, tint = hud.gold)
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text("الحفظ المحلي مفعل (Local-First)", style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                                    Text(
                                        "بياناتك محفوظة محليًا على الجهاز وتبقى بعد الإغلاق. لتفعيل المزامنة السحابية عبر Firebase مستقبلًا يلزم ربط مشروع Firebase وتسجيل الدخول بحساب Google.",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = TextSecondary
                                    )
                                }
                            }
                        }

                        OutlinedButton(
                            onClick = { showResetConfirm = true },
                            modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp).testTag("reset_all_data_btn")
                        ) {
                            Icon(Icons.Filled.DeleteForever, contentDescription = null, tint = hud.coral)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("حذف وتصفير جميع البيانات", color = hud.coral)
                        }
                    }
                }
            }
        }
    }

    if (showEditProfileDialog) {
        var editName by remember { mutableStateOf(profile.playerName) }
        var editAge by remember { mutableStateOf(AgeGroup.entries.find { it.name == profile.ageGroupName } ?: AgeGroup.YOUNG_ADULT_18_25) }
        var editFit by remember { mutableStateOf(FitnessLevel.entries.find { it.name == profile.fitnessLevelName } ?: FitnessLevel.BEGINNER) }

        AlertDialog(
            onDismissRequest = { showEditProfileDialog = false },
            title = { Text("تعديل بيانات اللاعب") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        label = { Text("اسم اللاعب") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text("الفئة العمرية:", style = MaterialTheme.typography.labelLarge)
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        AgeGroup.entries.forEach { ag ->
                            FilterChip(selected = editAge == ag, onClick = { editAge = ag }, label = { Text(ag.titleAr) })
                        }
                    }
                    Text("مستوى اللياقة:", style = MaterialTheme.typography.labelLarge)
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        FitnessLevel.entries.forEach { fl ->
                            FilterChip(selected = editFit == fl, onClick = { editFit = fl }, label = { Text(fl.titleAr) })
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    onUpdateProfileMeta(editName, editAge, editFit)
                    showEditProfileDialog = false
                }) { Text("حفظ") }
            },
            dismissButton = {
                TextButton(onClick = { showEditProfileDialog = false }) { Text("إلغاء") }
            }
        )
    }

    if (showResetConfirm) {
        AlertDialog(
            onDismissRequest = { showResetConfirm = false },
            title = { Text("تأكيد حذف البيانات") },
            text = { Text("هل أنت متأكد من تصفير المستوى وجميع المهام وسجلات النوم والتمارين؟ يُنصح بتصدير نسخة JSON أولًا.") },
            confirmButton = {
                Button(
                    onClick = {
                        onResetAllData()
                        showResetConfirm = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = hud.coral)
                ) { Text("نعم، تصفير الكل", color = CharcoalVoid, fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { showResetConfirm = false }) { Text("إلغاء") }
            }
        )
    }
}
