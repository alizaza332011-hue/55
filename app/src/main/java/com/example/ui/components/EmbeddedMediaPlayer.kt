package com.example.ui.components

import android.annotation.SuppressLint
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.VideocamOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.data.MotionPattern
import com.example.data.VideoItemEntity
import com.example.data.VideoPlayerType
import com.example.ui.theme.CharcoalDeep
import com.example.ui.theme.LocalHudPalette
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun EmbeddedMediaPlayer(
    video: VideoItemEntity?,
    motionPattern: MotionPattern,
    extractYoutubeId: (String) -> String?,
    modifier: Modifier = Modifier
) {
    val hud = LocalHudPalette.current
    var webFailed by remember(video?.id, video?.sourceUrl) { mutableStateOf(false) }

    val canEmbedVideo = video != null &&
        video.embedAllowed &&
        !webFailed &&
        (video.playerTypeName == VideoPlayerType.YOUTUBE_EMBED.name ||
            video.playerTypeName == VideoPlayerType.DIRECT_MP4.name)

    Column(modifier = modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(215.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(CharcoalDeep)
                .border(1.dp, hud.accent.copy(alpha = 0.45f), RoundedCornerShape(16.dp))
                .testTag("embedded_media_container")
        ) {
            if (canEmbedVideo && video != null) {
                EmbeddedWebVideoView(
                    video = video,
                    extractYoutubeId = extractYoutubeId,
                    onError = { webFailed = true },
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                InteractiveMotionGuideCanvas(
                    pattern = motionPattern,
                    statusLabel = when {
                        webFailed -> "تعذر تحميل الفيديو — تم تفعيل الدليل التفاعلي"
                        video != null -> video.thumbnailLabel
                        else -> "دليل حركي تفاعلي مدمج"
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        if (video != null && !canEmbedVideo) {
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Filled.VideocamOff,
                    contentDescription = "بديل توضيحي",
                    tint = hud.gold,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = video.description.ifEmpty {
                        "المصدر المرجعي لا يسمح بالتضمين المباشر؛ تم تفعيل الرسم الحركي التوضيحي داخل التطبيق."
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            }
        }
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
private fun EmbeddedWebVideoView(
    video: VideoItemEntity,
    extractYoutubeId: (String) -> String?,
    onError: () -> Unit,
    modifier: Modifier = Modifier
) {
    val ytId = if (video.playerTypeName == VideoPlayerType.YOUTUBE_EMBED.name) {
        extractYoutubeId(video.sourceUrl)
    } else null

    AndroidView(
        modifier = modifier,
        factory = { context ->
            WebView(context).apply {
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                settings.mediaPlaybackRequiresUserGesture = false
                settings.mixedContentMode = WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE
                webChromeClient = WebChromeClient()
                webViewClient = object : WebViewClient() {
                    override fun onReceivedError(
                        view: WebView?,
                        request: WebResourceRequest?,
                        error: WebResourceError?
                    ) {
                        if (request?.isForMainFrame == true) {
                            onError()
                        }
                    }
                }
                if (ytId != null) {
                    val html = """
                        <!DOCTYPE html><html><body style="margin:0;padding:0;background:#07090F;">
                        <iframe width="100%" height="100%" style="position:fixed;inset:0;border:0;"
                        src="https://www.youtube-nocookie.com/embed/$ytId?playsinline=1&rel=0"
                        allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture"
                        allowfullscreen></iframe></body></html>
                    """.trimIndent()
                    loadDataWithBaseURL("https://www.youtube-nocookie.com", html, "text/html", "utf-8", null)
                } else {
                    val html = """
                        <!DOCTYPE html><html><body style="margin:0;padding:0;background:#07090F;display:flex;align-items:center;justify-content:center;height:100vh;">
                        <video controls playsinline style="width:100%;height:100%;object-fit:contain;" src="${video.sourceUrl}"></video>
                        </body></html>
                    """.trimIndent()
                    loadDataWithBaseURL(null, html, "text/html", "utf-8", null)
                }
            }
        }
    )
}

@Composable
fun InteractiveMotionGuideCanvas(
    pattern: MotionPattern,
    statusLabel: String,
    modifier: Modifier = Modifier
) {
    val hud = LocalHudPalette.current
    var isPlaying by remember { mutableStateOf(true) }
    val infinite = rememberInfiniteTransition(label = "exercise_motion")
    val rawPhase by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "motion_phase"
    )
    val t = if (isPlaying) rawPhase else 0.5f

    val cueText = when {
        t < 0.35f -> "وضعية البداية — شهيق هادئ"
        t < 0.7f -> "ذروة الحركة — تحكم عضلي كامل"
        else -> "العودة بسلاسة — زفير منتظم"
    }

    Box(modifier = modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val cx = w * 0.5f
            val floorY = h * 0.80f

            // Floor line & holographic scan ring
            drawLine(
                color = hud.accent.copy(alpha = 0.4f),
                start = Offset(w * 0.14f, floorY),
                end = Offset(w * 0.86f, floorY),
                strokeWidth = 3f
            )

            val figureColor = hud.accent
            val jointColor = hud.gold
            val stroke = 7f

            when (pattern) {
                MotionPattern.WALL_PUSHUP -> {
                    val wallX = w * 0.72f
                    drawLine(hud.secondaryAccent, Offset(wallX, h * 0.16f), Offset(wallX, floorY), strokeWidth = 8f)
                    val lean = 0.08f + t * 0.11f
                    val hip = Offset(cx - w * 0.08f, floorY - h * 0.28f)
                    val foot = Offset(cx - w * 0.12f, floorY)
                    val shoulder = Offset(hip.x + w * lean, hip.y - h * 0.22f)
                    val head = Offset(shoulder.x + 8f, shoulder.y - 26f)
                    val hand = Offset(wallX - 6f, shoulder.y + 8f)
                    drawLine(figureColor, foot, hip, strokeWidth = stroke, cap = StrokeCap.Round)
                    drawLine(figureColor, hip, shoulder, strokeWidth = stroke, cap = StrokeCap.Round)
                    drawLine(figureColor, shoulder, hand, strokeWidth = stroke, cap = StrokeCap.Round)
                    drawCircle(figureColor, 18f, head, style = Stroke(stroke))
                    drawCircle(jointColor, 6f, shoulder)
                }

                MotionPattern.INCLINE_PUSHUP, MotionPattern.PUSHUP -> {
                    val benchDrop = if (pattern == MotionPattern.INCLINE_PUSHUP) h * 0.12f else 0f
                    val chestDip = t * h * 0.12f
                    val foot = Offset(w * 0.26f, floorY)
                    val hand = Offset(w * 0.68f, floorY - benchDrop)
                    val shoulder = Offset(w * 0.64f, floorY - benchDrop - h * 0.20f + chestDip)
                    val hip = Offset((foot.x + shoulder.x) * 0.5f, (foot.y + shoulder.y) * 0.5f)
                    val head = Offset(shoulder.x + 24f, shoulder.y - 16f)
                    if (pattern == MotionPattern.INCLINE_PUSHUP) {
                        drawLine(hud.secondaryAccent, Offset(w * 0.63f, floorY - benchDrop), Offset(w * 0.76f, floorY - benchDrop), strokeWidth = 10f)
                    }
                    drawLine(figureColor, foot, shoulder, strokeWidth = stroke, cap = StrokeCap.Round)
                    drawLine(figureColor, shoulder, hand, strokeWidth = stroke, cap = StrokeCap.Round)
                    drawCircle(figureColor, 16f, head, style = Stroke(stroke))
                    drawCircle(jointColor, 6f, hip)
                }

                MotionPattern.SQUAT, MotionPattern.REVERSE_LUNGE -> {
                    val drop = t * h * 0.18f
                    val footL = Offset(cx - 22f, floorY)
                    val footR = if (pattern == MotionPattern.REVERSE_LUNGE) Offset(cx + 46f, floorY) else Offset(cx + 22f, floorY)
                    val knee = Offset(cx - 10f - t * 18f, floorY - h * 0.17f + drop * 0.35f)
                    val hip = Offset(cx + t * 24f, floorY - h * 0.34f + drop)
                    val shoulder = Offset(cx + 4f, hip.y - h * 0.22f)
                    val head = Offset(shoulder.x, shoulder.y - 24f)
                    drawLine(figureColor, footL, knee, strokeWidth = stroke, cap = StrokeCap.Round)
                    drawLine(figureColor, footR, hip, strokeWidth = stroke, cap = StrokeCap.Round)
                    drawLine(figureColor, knee, hip, strokeWidth = stroke, cap = StrokeCap.Round)
                    drawLine(figureColor, hip, shoulder, strokeWidth = stroke, cap = StrokeCap.Round)
                    drawLine(figureColor, shoulder, Offset(shoulder.x - 36f, shoulder.y + 12f), strokeWidth = stroke, cap = StrokeCap.Round)
                    drawCircle(figureColor, 17f, head, style = Stroke(stroke))
                    drawCircle(jointColor, 6f, knee)
                }

                MotionPattern.GLUTE_BRIDGE -> {
                    val lift = t * h * 0.16f
                    val shoulder = Offset(w * 0.32f, floorY - 10f)
                    val head = Offset(w * 0.24f, floorY - 16f)
                    val hip = Offset(w * 0.48f, floorY - 14f - lift)
                    val knee = Offset(w * 0.62f, floorY - h * 0.18f)
                    val foot = Offset(w * 0.68f, floorY)
                    drawLine(figureColor, shoulder, hip, strokeWidth = stroke, cap = StrokeCap.Round)
                    drawLine(figureColor, hip, knee, strokeWidth = stroke, cap = StrokeCap.Round)
                    drawLine(figureColor, knee, foot, strokeWidth = stroke, cap = StrokeCap.Round)
                    drawCircle(figureColor, 15f, head, style = Stroke(stroke))
                    drawCircle(jointColor, 6f, hip)
                }

                MotionPattern.PLANK -> {
                    val foot = Offset(w * 0.25f, floorY)
                    val elbow = Offset(w * 0.68f, floorY)
                    val shoulder = Offset(w * 0.66f, floorY - h * 0.18f)
                    val head = Offset(shoulder.x + 22f, shoulder.y - 12f)
                    val pulseY = sin(t * PI).toFloat() * 4f
                    drawLine(figureColor, foot, Offset(shoulder.x, shoulder.y + pulseY), strokeWidth = stroke, cap = StrokeCap.Round)
                    drawLine(figureColor, shoulder, elbow, strokeWidth = stroke, cap = StrokeCap.Round)
                    drawCircle(figureColor, 15f, head, style = Stroke(stroke))
                    drawCircle(jointColor, 6f, Offset((foot.x + shoulder.x) * 0.5f, (foot.y + shoulder.y) * 0.5f + pulseY))
                }

                MotionPattern.BIRD_DOG -> {
                    val knee = Offset(w * 0.42f, floorY)
                    val hand = Offset(w * 0.60f, floorY)
                    val hip = Offset(w * 0.42f, floorY - h * 0.20f)
                    val shoulder = Offset(w * 0.60f, floorY - h * 0.20f)
                    val extLeg = Offset(w * (0.42f - t * 0.20f), floorY - t * h * 0.20f)
                    val extArm = Offset(w * (0.60f + t * 0.18f), floorY - h * 0.20f - t * 6f)
                    drawLine(figureColor, knee, hip, strokeWidth = stroke, cap = StrokeCap.Round)
                    drawLine(figureColor, hand, shoulder, strokeWidth = stroke, cap = StrokeCap.Round)
                    drawLine(figureColor, hip, shoulder, strokeWidth = stroke, cap = StrokeCap.Round)
                    drawLine(jointColor, hip, extLeg, strokeWidth = stroke, cap = StrokeCap.Round)
                    drawLine(jointColor, shoulder, extArm, strokeWidth = stroke, cap = StrokeCap.Round)
                    drawCircle(figureColor, 15f, Offset(shoulder.x + 20f, shoulder.y - 12f), style = Stroke(stroke))
                }

                MotionPattern.JUMPING_JACKS -> {
                    val spread = t * 44f
                    val armAngle = (-0.3f + t * 2.1f).toDouble()
                    val hip = Offset(cx, floorY - h * 0.26f - t * 14f)
                    val shoulder = Offset(cx, hip.y - h * 0.22f)
                    val head = Offset(cx, shoulder.y - 24f)
                    drawLine(figureColor, hip, Offset(cx - 12f - spread, floorY - t * 12f), strokeWidth = stroke, cap = StrokeCap.Round)
                    drawLine(figureColor, hip, Offset(cx + 12f + spread, floorY - t * 12f), strokeWidth = stroke, cap = StrokeCap.Round)
                    drawLine(figureColor, hip, shoulder, strokeWidth = stroke, cap = StrokeCap.Round)
                    val armLen = 46f
                    drawLine(figureColor, shoulder, Offset(shoulder.x - (cos(armAngle) * armLen).toFloat(), shoulder.y - (sin(armAngle) * armLen).toFloat()), strokeWidth = stroke, cap = StrokeCap.Round)
                    drawLine(figureColor, shoulder, Offset(shoulder.x + (cos(armAngle) * armLen).toFloat(), shoulder.y - (sin(armAngle) * armLen).toFloat()), strokeWidth = stroke, cap = StrokeCap.Round)
                    drawCircle(figureColor, 17f, head, style = Stroke(stroke))
                }

                MotionPattern.CALF_RAISE, MotionPattern.WALKING, MotionPattern.SHOULDER_CIRCLES, MotionPattern.STRETCHING, MotionPattern.TRICEPS_DIP -> {
                    val lift = t * 22f
                    val hip = Offset(cx, floorY - h * 0.27f - lift * 0.5f)
                    val shoulder = Offset(cx, hip.y - h * 0.23f)
                    val head = Offset(cx, shoulder.y - 24f)
                    val legOffset = if (pattern == MotionPattern.WALKING) (t - 0.5f) * 34f else 12f
                    drawLine(figureColor, hip, Offset(cx - legOffset, floorY - lift * 0.4f), strokeWidth = stroke, cap = StrokeCap.Round)
                    drawLine(figureColor, hip, Offset(cx + legOffset, floorY - lift * 0.4f), strokeWidth = stroke, cap = StrokeCap.Round)
                    drawLine(figureColor, hip, shoulder, strokeWidth = stroke, cap = StrokeCap.Round)
                    val handY = if (pattern == MotionPattern.STRETCHING || pattern == MotionPattern.SHOULDER_CIRCLES) {
                        shoulder.y - 36f * t
                    } else {
                        shoulder.y + 34f
                    }
                    drawLine(figureColor, shoulder, Offset(cx - 34f, handY), strokeWidth = stroke, cap = StrokeCap.Round)
                    drawLine(figureColor, shoulder, Offset(cx + 34f, handY), strokeWidth = stroke, cap = StrokeCap.Round)
                    drawCircle(figureColor, 17f, head, style = Stroke(stroke))
                    drawCircle(jointColor, 6f, shoulder)
                }
            }
        }

        // Top badge & Play/Pause toggle
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            HudTagChip(text = statusLabel, color = hud.accent)
            IconButton(
                onClick = { isPlaying = !isPlaying },
                modifier = Modifier
                    .size(48.dp)
                    .background(Color(0x99101626), RoundedCornerShape(12.dp))
                    .testTag("toggle_motion_guide")
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                    contentDescription = if (isPlaying) "إيقاف الرسم المتحرك" else "تشغيل الرسم المتحرك",
                    tint = TextPrimary
                )
            }
        }

        // Bottom breathing & posture cue
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(Color(0xB307090F))
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Text(
                text = cueText,
                style = MaterialTheme.typography.bodyMedium,
                color = hud.gold,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}
