package com.example.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.FullAppSnapshot
import com.example.data.ProgressionCatalog
import com.example.data.QuestStatus
import com.example.data.StatType
import com.example.ui.components.GlowingProgressBar
import com.example.ui.components.HolographicGlassCard
import com.example.ui.components.HudSectionHeader
import com.example.ui.components.HudTagChip
import com.example.ui.components.StatRadarChart
import com.example.ui.theme.CharcoalVoid
import com.example.ui.theme.LocalHudPalette
import com.example.ui.theme.OrbitronFontFamily
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun HomeHudScreen(
    snapshot: FullAppSnapshot,
    onToggleMute: () -> Unit,
    onToggleBossJoin: (String) -> Unit,
    onNavigateToQuests: () -> Unit,
    onNavigateToTraining: () -> Unit,
    onNavigateToEvolutionSection: (String) -> Unit
) {
    val hud = LocalHudPalette.current
    val profile = snapshot.profile
    val tier = ProgressionCatalog.tierForLevel(profile.level)
    val levelSpan = (tier.nextLevelXp - tier.minXp).coerceAtLeast(1)
    val xpInTier = (profile.currentXp - tier.minXp).coerceAtLeast(0)
    val xpProgress = (xpInTier.toFloat() / levelSpan.toFloat()).coerceIn(0f, 1f)
    val equippedTitle = ProgressionCatalog.rewardItems.find { it.id == profile.equippedTitleId }?.titleAr
        ?: "المستيقظ"

    val statsList = listOf(
        StatType.STRENGTH to profile.strength,
        StatType.STAMINA to profile.stamina,
        StatType.MOBILITY to profile.mobility,
        StatType.DISCIPLINE to profile.discipline,
        StatType.FOCUS to profile.focus,
        StatType.KNOWLEDGE to profile.knowledge,
        StatType.RECOVERY to profile.recovery
    )

    val completedToday = snapshot.quests.count { it.statusName == QuestStatus.COMPLETED.name }
    val totalQuests = snapshot.quests.size.coerceAtLeast(1)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(modifier = Modifier.widthIn(max = 640.dp)) {
            HudSectionHeader(
                titleAr = "لوحة حالة اللاعب",
                badgeEn = "STATUS HUD",
                trailing = {
                    IconButton(
                        onClick = onToggleMute,
                        modifier = Modifier
                            .size(48.dp)
                            .testTag("hud_mute_button")
                    ) {
                        Icon(
                            imageVector = if (snapshot.preferences.soundMuted) {
                                Icons.AutoMirrored.Filled.VolumeOff
                            } else {
                                Icons.AutoMirrored.Filled.VolumeUp
                            },
                            contentDescription = if (snapshot.preferences.soundMuted) "إلغاء الكتم" else "كتم الصوت",
                            tint = if (snapshot.preferences.soundMuted) TextSecondary else hud.accent
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Player Status Card
            HolographicGlassCard(
                glowAccent = profile.equippedFrameId != "frame_cyber_cyan",
                modifier = Modifier.fillMaxWidth().testTag("player_status_card")
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            HudTagChip(text = equippedTitle, color = hud.gold)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = profile.playerName,
                                style = MaterialTheme.typography.headlineLarge,
                                color = TextPrimary
                            )
                            Text(
                                text = "LEVEL ${tier.level} • ${tier.titleEn} (${tier.titleAr})",
                                style = MaterialTheme.typography.labelSmall,
                                color = hud.accent,
                                fontFamily = OrbitronFontFamily
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "${profile.coins} COINS",
                                style = MaterialTheme.typography.titleMedium,
                                color = hud.gold,
                                fontFamily = OrbitronFontFamily,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "XP ${profile.currentXp}",
                                style = MaterialTheme.typography.labelSmall,
                                color = hud.accent,
                                fontFamily = OrbitronFontFamily
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "التقدم نحو المستوى التالي",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary
                        )
                        Text(
                            text = "$xpInTier / $levelSpan XP",
                            style = MaterialTheme.typography.labelSmall,
                            color = hud.accent,
                            fontFamily = OrbitronFontFamily
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    GlowingProgressBar(progress = xpProgress, height = 12.dp)

                    Spacer(modifier = Modifier.height(14.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        MiniHudMetric(
                            icon = Icons.Filled.LocalFireDepartment,
                            label = "الأيام النشطة",
                            value = "${profile.activeDays} يوم",
                            tint = hud.gold
                        )
                        MiniHudMetric(
                            icon = Icons.Filled.Bolt,
                            label = "السلسلة",
                            value = "${profile.dailyStreak} أيام",
                            tint = hud.accent
                        )
                        MiniHudMetric(
                            icon = Icons.Filled.CheckCircle,
                            label = "مهام اليوم",
                            value = "$completedToday/$totalQuests",
                            tint = hud.success
                        )
                        MiniHudMetric(
                            icon = Icons.Filled.EmojiEvents,
                            label = "الإنجازات",
                            value = "${snapshot.unlockedAchievements.size}/${ProgressionCatalog.achievements.size}",
                            tint = hud.secondaryAccent
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Quick Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onNavigateToQuests,
                    colors = ButtonDefaults.buttonColors(containerColor = hud.accent),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = 48.dp)
                        .testTag("quick_quests_btn")
                ) {
                    Text("مهام اليوم", color = CharcoalVoid, fontWeight = FontWeight.Bold)
                }
                OutlinedButton(
                    onClick = onNavigateToTraining,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = 48.dp)
                        .testTag("quick_training_btn")
                ) {
                    Icon(Icons.Filled.FitnessCenter, contentDescription = null, tint = hud.accent, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("أكاديمية التدريب", color = hud.accent, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 7 Stats Radar & Bars
            HolographicGlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "إحصائيات التطور السبعة",
                            style = MaterialTheme.typography.titleLarge,
                            color = TextPrimary
                        )
                        HudTagChip(text = "PLAYER STATS", color = hud.accent)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    StatRadarChart(stats = statsList)
                    Spacer(modifier = Modifier.height(8.dp))
                    statsList.forEach { (stat, value) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${stat.titleAr} (${stat.codeEn})",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextPrimary,
                                modifier = Modifier.weight(1.2f)
                            )
                            GlowingProgressBar(
                                progress = (value / 50f).coerceIn(0.08f, 1f),
                                height = 7.dp,
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "$value",
                                style = MaterialTheme.typography.labelSmall,
                                color = hud.accent,
                                fontFamily = OrbitronFontFamily
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Weekly Boss Challenges
            HudSectionHeader(titleAr = "التحديات الأسبوعية", badgeEn = "BOSS CHALLENGES")
            Spacer(modifier = Modifier.height(10.dp))

            snapshot.bossChallenges.forEach { boss ->
                HolographicGlassCard(
                    glowAccent = boss.joined && !boss.completed,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp)
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = boss.title,
                                style = MaterialTheme.typography.titleMedium,
                                color = if (boss.completed) hud.success else hud.gold,
                                modifier = Modifier.weight(1f)
                            )
                            HudTagChip(
                                text = if (boss.completed) "CLEARED" else "+${boss.xpReward} XP",
                                color = if (boss.completed) hud.success else hud.accent
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = boss.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("التقدم: ${boss.currentCount} / ${boss.targetCount}", style = MaterialTheme.typography.labelLarge, color = TextPrimary)
                            Text("المكافأة: ${boss.coinsReward} COINS", style = MaterialTheme.typography.labelSmall, color = hud.gold)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        GlowingProgressBar(
                            progress = boss.currentCount.toFloat() / boss.targetCount.coerceAtLeast(1),
                            fillColor = if (boss.completed) hud.success else hud.gold
                        )
                        if (!boss.completed) {
                            Spacer(modifier = Modifier.height(10.dp))
                            OutlinedButton(
                                onClick = { onToggleBossJoin(boss.id) },
                                modifier = Modifier.heightIn(min = 48.dp)
                            ) {
                                Text(
                                    text = if (boss.joined) "مشارك حاليًا (إلغاء اختياري)" else "انضم للتحدي الاختياري",
                                    color = hud.accent
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            HudSectionHeader(titleAr = "بوابات التطوير السريعة", badgeEn = "EVOLUTION MODULES")
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                QuickModuleCard(
                    title = "المظهر",
                    subtitle = "Glow Up",
                    icon = Icons.Filled.Spa,
                    accent = hud.accent,
                    onClick = { onNavigateToEvolutionSection("glowup") },
                    modifier = Modifier.weight(1f)
                )
                QuickModuleCard(
                    title = "النوم",
                    subtitle = "Sleep",
                    icon = Icons.Filled.Bedtime,
                    accent = hud.secondaryAccent,
                    onClick = { onNavigateToEvolutionSection("sleep") },
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                QuickModuleCard(
                    title = "التغذية",
                    subtitle = "Nutrition",
                    icon = Icons.Filled.Restaurant,
                    accent = hud.success,
                    onClick = { onNavigateToEvolutionSection("nutrition") },
                    modifier = Modifier.weight(1f)
                )
                QuickModuleCard(
                    title = "العقل",
                    subtitle = "Pomodoro",
                    icon = Icons.Filled.Psychology,
                    accent = hud.gold,
                    onClick = { onNavigateToEvolutionSection("mind") },
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun MiniHudMetric(
    icon: ImageVector,
    label: String,
    value: String,
    tint: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, contentDescription = label, tint = tint, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.height(2.dp))
        Text(value, style = MaterialTheme.typography.labelLarge, color = TextPrimary)
        Text(label, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
    }
}

@Composable
private fun QuickModuleCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    accent: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    HolographicGlassCard(
        onClick = onClick,
        modifier = modifier.heightIn(min = 84.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = title, tint = accent, modifier = Modifier.size(28.dp))
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(title, style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                Text(subtitle, style = MaterialTheme.typography.labelSmall, color = accent, fontFamily = OrbitronFontFamily)
            }
        }
    }
}
