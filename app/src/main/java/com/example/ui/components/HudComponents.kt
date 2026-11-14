package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ProgressionCatalog
import com.example.data.StatType
import com.example.ui.theme.CharcoalVoid
import com.example.ui.theme.DeepViolet
import com.example.ui.theme.LocalHudPalette
import com.example.ui.theme.OrbitronFontFamily
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun HolographicBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val hud = LocalHudPalette.current
    val infinite = rememberInfiniteTransition(label = "bg_pulse")
    val pulse by infinite.animateFloat(
        initialValue = 0.25f,
        targetValue = 0.55f,
        animationSpec = infiniteRepeatable(
            animation = tween(4200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(CharcoalVoid)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            val topGlowColor = when (hud.backgroundId) {
                "bg_nebula_pulse" -> DeepViolet.copy(alpha = 0.28f * pulse)
                "bg_cyber_horizon" -> hud.accent.copy(alpha = 0.26f * pulse)
                else -> hud.accent.copy(alpha = 0.16f * pulse)
            }
            val bottomGlowColor = when (hud.backgroundId) {
                "bg_cyber_horizon" -> DeepViolet.copy(alpha = 0.24f)
                else -> hud.secondaryAccent.copy(alpha = 0.16f)
            }

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(topGlowColor, Color.Transparent),
                    center = Offset(w * 0.2f, h * 0.12f),
                    radius = w * 0.85f
                ),
                radius = w * 0.85f,
                center = Offset(w * 0.2f, h * 0.12f)
            )
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(bottomGlowColor, Color.Transparent),
                    center = Offset(w * 0.85f, h * 0.82f),
                    radius = w * 0.75f
                ),
                radius = w * 0.75f,
                center = Offset(w * 0.85f, h * 0.82f)
            )

            // Futuristic HUD grid lines
            val step = 64.dp.toPx()
            val gridColor = hud.border.copy(alpha = 0.16f)
            var x = 0f
            while (x < w) {
                drawLine(gridColor, Offset(x, 0f), Offset(x, h), strokeWidth = 1f)
                x += step
            }
            var y = 0f
            while (y < h) {
                drawLine(gridColor, Offset(0f, y), Offset(w, y), strokeWidth = 1f)
                y += step
            }
        }
        content()
    }
}

@Composable
fun HolographicGlassCard(
    modifier: Modifier = Modifier,
    glowAccent: Boolean = false,
    onClick: (() -> Unit)? = null,
    contentPadding: Dp = 16.dp,
    content: @Composable () -> Unit
) {
    val hud = LocalHudPalette.current
    val shape = RoundedCornerShape(18.dp)
    val borderBrush = if (glowAccent) {
        Brush.linearGradient(
            listOf(hud.accent.copy(alpha = 0.85f), hud.secondaryAccent.copy(alpha = 0.65f))
        )
    } else {
        Brush.linearGradient(
            listOf(hud.border.copy(alpha = 0.75f), hud.accent.copy(alpha = 0.22f))
        )
    }

    val clickableModifier = if (onClick != null) {
        Modifier.clickable(onClick = onClick)
    } else {
        Modifier
    }

    Box(
        modifier = modifier
            .clip(shape)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        hud.glassSurfaceElevated.copy(alpha = 0.86f),
                        hud.glassSurface.copy(alpha = 0.78f)
                    )
                )
            )
            .border(width = if (glowAccent) 1.4.dp else 1.dp, brush = borderBrush, shape = shape)
            .then(clickableModifier)
            .padding(contentPadding)
    ) {
        content()
    }
}

@Composable
fun HudSectionHeader(
    titleAr: String,
    badgeEn: String,
    modifier: Modifier = Modifier,
    trailing: @Composable (() -> Unit)? = null
) {
    val hud = LocalHudPalette.current
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = badgeEn,
                style = MaterialTheme.typography.labelSmall,
                color = hud.accent,
                fontFamily = OrbitronFontFamily
            )
            Text(
                text = titleAr,
                style = MaterialTheme.typography.headlineMedium,
                color = TextPrimary
            )
        }
        if (trailing != null) {
            Spacer(modifier = Modifier.width(8.dp))
            trailing()
        }
    }
}

@Composable
fun GlowingProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    height: Dp = 10.dp,
    fillColor: Color = LocalHudPalette.current.accent,
    secondaryColor: Color = LocalHudPalette.current.secondaryAccent
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = spring(stiffness = 220f, dampingRatio = 0.78f),
        label = "xp_progress"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(50))
            .background(Color(0xFF1A233A))
            .border(0.75.dp, fillColor.copy(alpha = 0.3f), RoundedCornerShape(50))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(animatedProgress.coerceAtLeast(0.02f))
                .height(height)
                .clip(RoundedCornerShape(50))
                .background(
                    Brush.horizontalGradient(listOf(secondaryColor, fillColor))
                )
        )
    }
}

@Composable
fun HudTagChip(
    text: String,
    color: Color = LocalHudPalette.current.accent,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        color = color.copy(alpha = 0.14f),
        border = BorderStroke(1.dp, color.copy(alpha = 0.45f))
    ) {
        Text(
            text = text,
            color = color,
            style = MaterialTheme.typography.labelSmall,
            fontSize = 11.sp,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun StatRadarChart(
    stats: List<Pair<StatType, Int>>,
    modifier: Modifier = Modifier
) {
    val hud = LocalHudPalette.current
    val maxStat = (stats.maxOfOrNull { it.second } ?: 20).coerceAtLeast(25).toFloat()

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(210.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(175.dp)) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val radius = size.minDimension * 0.44f
            val count = stats.size.coerceAtLeast(3)

            // Rings
            listOf(0.33f, 0.66f, 1f).forEach { scale ->
                val ringPath = Path()
                for (i in 0 until count) {
                    val angle = (2.0 * PI * i / count) - PI / 2.0
                    val p = Offset(
                        center.x + (radius * scale * cos(angle)).toFloat(),
                        center.y + (radius * scale * sin(angle)).toFloat()
                    )
                    if (i == 0) ringPath.moveTo(p.x, p.y) else ringPath.lineTo(p.x, p.y)
                }
                ringPath.close()
                drawPath(ringPath, hud.border.copy(alpha = 0.45f), style = Stroke(width = 1.5f))
            }

            // Spokes
            for (i in 0 until count) {
                val angle = (2.0 * PI * i / count) - PI / 2.0
                val end = Offset(
                    center.x + (radius * cos(angle)).toFloat(),
                    center.y + (radius * sin(angle)).toFloat()
                )
                drawLine(hud.border.copy(alpha = 0.38f), center, end, strokeWidth = 1.2f)
            }

            // Filled stat polygon
            val statPath = Path()
            val dots = mutableListOf<Offset>()
            stats.forEachIndexed { i, (_, value) ->
                val ratio = (value / maxStat).coerceIn(0.18f, 1f)
                val angle = (2.0 * PI * i / count) - PI / 2.0
                val p = Offset(
                    center.x + (radius * ratio * cos(angle)).toFloat(),
                    center.y + (radius * ratio * sin(angle)).toFloat()
                )
                dots.add(p)
                if (i == 0) statPath.moveTo(p.x, p.y) else statPath.lineTo(p.x, p.y)
            }
            statPath.close()
            drawPath(
                path = statPath,
                brush = Brush.radialGradient(
                    colors = listOf(hud.accent.copy(alpha = 0.42f), hud.secondaryAccent.copy(alpha = 0.22f)),
                    center = center,
                    radius = radius
                )
            )
            drawPath(
                path = statPath,
                color = hud.accent,
                style = Stroke(width = 3f, cap = StrokeCap.Round)
            )
            dots.forEach { dot ->
                drawCircle(hud.accent, radius = 5f, center = dot)
            }
        }

        // Labels positioned around radar
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                stats.getOrNull(0)?.let { (st, v) ->
                    Text("${st.titleAr} ($v)", style = MaterialTheme.typography.labelSmall, color = hud.accent)
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                stats.getOrNull(6)?.let { (st, v) ->
                    Text("${st.titleAr}\n$v", style = MaterialTheme.typography.labelSmall, color = TextSecondary, textAlign = TextAlign.Center)
                }
                stats.getOrNull(1)?.let { (st, v) ->
                    Text("${st.titleAr}\n$v", style = MaterialTheme.typography.labelSmall, color = TextSecondary, textAlign = TextAlign.Center)
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                stats.getOrNull(5)?.let { (st, v) ->
                    Text("${st.titleAr}\n$v", style = MaterialTheme.typography.labelSmall, color = TextSecondary, textAlign = TextAlign.Center)
                }
                stats.getOrNull(2)?.let { (st, v) ->
                    Text("${st.titleAr}\n$v", style = MaterialTheme.typography.labelSmall, color = TextSecondary, textAlign = TextAlign.Center)
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                stats.getOrNull(4)?.let { (st, v) ->
                    Text("${st.titleAr} ($v)", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                }
                stats.getOrNull(3)?.let { (st, v) ->
                    Text("${st.titleAr} ($v)", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                }
            }
        }
    }
}

@Composable
fun QuestCompletedFloatingBanner(
    visible: Boolean,
    xp: Int,
    coins: Int,
    label: String,
    modifier: Modifier = Modifier
) {
    val hud = LocalHudPalette.current
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(220)) + scaleIn(initialScale = 0.85f),
        exit = fadeOut(tween(260)) + scaleOut(targetScale = 0.9f),
        modifier = modifier
    ) {
        HolographicGlassCard(
            glowAccent = true,
            contentPadding = 14.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .testTag("quest_completed_banner")
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Icon(
                        imageVector = Icons.Filled.CheckCircle,
                        contentDescription = "إتمام المهمة",
                        tint = hud.success,
                        modifier = Modifier.size(30.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "QUEST COMPLETED",
                            style = MaterialTheme.typography.labelSmall,
                            color = hud.accent,
                            fontFamily = OrbitronFontFamily
                        )
                        Text(
                            text = label,
                            style = MaterialTheme.typography.titleMedium,
                            color = TextPrimary
                        )
                    }
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "+$xp XP",
                        style = MaterialTheme.typography.titleMedium,
                        color = hud.accent,
                        fontFamily = OrbitronFontFamily,
                        fontWeight = FontWeight.Bold
                    )
                    if (coins > 0) {
                        Text(
                            text = "+$coins COINS",
                            style = MaterialTheme.typography.labelSmall,
                            color = hud.gold,
                            fontFamily = OrbitronFontFamily
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LevelUpModalOverlay(
    newLevel: Int?,
    onDismiss: () -> Unit
) {
    val hud = LocalHudPalette.current
    if (newLevel == null) return
    val tier = ProgressionCatalog.tierForLevel(newLevel)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xD905070E))
            .clickable(onClick = onDismiss)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        HolographicGlassCard(
            glowAccent = true,
            contentPadding = 24.dp,
            modifier = Modifier.fillMaxWidth().testTag("level_up_overlay")
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Filled.AutoAwesome,
                    contentDescription = "ترقية المستوى",
                    tint = hud.gold,
                    modifier = Modifier.size(56.dp)
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "LEVEL UP!",
                    style = MaterialTheme.typography.displayLarge,
                    color = hud.accent,
                    fontFamily = OrbitronFontFamily
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "LEVEL ${tier.level} — ${tier.titleEn}",
                    style = MaterialTheme.typography.titleLarge,
                    color = hud.gold,
                    fontFamily = OrbitronFontFamily
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "تهانينا! لقد ارتقيت إلى رتبة (${tier.titleAr}) بفضل استمرارك وانضباطك الواقعي.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextPrimary,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(20.dp))
                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = hud.accent),
                    modifier = Modifier.fillMaxWidth().testTag("dismiss_level_up_button")
                ) {
                    Icon(Icons.Filled.EmojiEvents, contentDescription = null, tint = CharcoalVoid)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("متابعة الرحلة", color = CharcoalVoid, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
