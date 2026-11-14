package com.example.ui.screens

import androidx.activity.compose.BackHandler
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import com.example.data.FitnessCatalog
import com.example.data.VideoItemEntity
import com.example.data.VideoPlayerType
import com.example.data.VideoSectionCategory
import com.example.data.VideoVerificationStatus
import com.example.ui.components.HolographicGlassCard
import com.example.ui.components.HudTagChip
import com.example.ui.theme.CharcoalVoid
import com.example.ui.theme.LocalHudPalette
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun VideoAdminScreen(
    videos: List<VideoItemEntity>,
    onSaveVideo: (id: String?, title: String, category: VideoSectionCategory, linkedContentId: String, sourceUrl: String, description: String) -> Unit,
    onDeleteVideo: (String) -> Unit,
    onBack: () -> Unit
) {
    BackHandler(onBack = onBack)
    val hud = LocalHudPalette.current
    var editingVideo by remember { mutableStateOf<VideoItemEntity?>(null) }
    var showCreateDialog by remember { mutableStateOf(false) }

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
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBack, modifier = Modifier.size(48.dp)) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "رجوع", tint = hud.accent)
                    }
                    Text("إدارة مكتبة الفيديوهات", style = MaterialTheme.typography.headlineMedium, color = TextPrimary)
                }
                Button(
                    onClick = { showCreateDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = hud.accent),
                    modifier = Modifier.heightIn(min = 48.dp).testTag("add_video_btn")
                ) {
                    Icon(Icons.Filled.Add, contentDescription = null, tint = CharcoalVoid)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("إضافة", color = CharcoalVoid, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            HolographicGlassCard(contentPadding = 12.dp) {
                Text(
                    text = "يتم التحقق تلقائيًا من صلاحية رابط YouTube Embed أو MP4 مباشر قبل تفعيله. أي مصدر لا يدعم التضمين يظهر بحالة مقيد مع تفعيل الدليل الحركي التفاعلي تلقائيًا.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            }
            Spacer(modifier = Modifier.height(10.dp))

            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(videos, key = { it.id }) { vid ->
                    val status = VideoVerificationStatus.entries.find { it.name == vid.verificationStatusName }
                        ?: VideoVerificationStatus.NEEDS_CHECK
                    val player = VideoPlayerType.entries.find { it.name == vid.playerTypeName }
                        ?: VideoPlayerType.INTERACTIVE_GUIDE_ONLY
                    val cat = VideoSectionCategory.entries.find { it.name == vid.categoryName }
                        ?: VideoSectionCategory.FITNESS

                    HolographicGlassCard(modifier = Modifier.fillMaxWidth()) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    HudTagChip(text = cat.titleAr, color = hud.accent)
                                    HudTagChip(
                                        text = status.titleAr,
                                        color = if (vid.embedAllowed) hud.success else hud.gold
                                    )
                                }
                                Row {
                                    IconButton(
                                        onClick = { editingVideo = vid },
                                        modifier = Modifier.size(44.dp)
                                    ) {
                                        Icon(Icons.Filled.Edit, contentDescription = "تعديل", tint = hud.accent)
                                    }
                                    IconButton(
                                        onClick = { onDeleteVideo(vid.id) },
                                        modifier = Modifier.size(44.dp)
                                    ) {
                                        Icon(Icons.Filled.DeleteOutline, contentDescription = "حذف", tint = hud.coral)
                                    }
                                }
                            }
                            Text(vid.title, style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                            Text("المشغل: ${player.titleAr} • التضمين: ${if (vid.embedAllowed) "مسموح" else "بديل تفاعلي"}", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                            if (vid.linkedContentId.isNotBlank()) {
                                Text("مرتبط بـ: ${vid.linkedContentId}", style = MaterialTheme.typography.bodyMedium, color = hud.accent)
                            }
                            if (vid.sourceUrl.isNotBlank()) {
                                Text("المصدر: ${vid.sourceUrl}", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                            }
                            if (vid.description.isNotBlank()) {
                                Text(vid.description, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                            }
                        }
                    }
                }
            }
        }
    }

    if (showCreateDialog || editingVideo != null) {
        VideoEditDialog(
            initial = editingVideo,
            onDismiss = {
                showCreateDialog = false
                editingVideo = null
            },
            onSave = { title, cat, linked, url, desc ->
                onSaveVideo(editingVideo?.id, title, cat, linked, url, desc)
                showCreateDialog = false
                editingVideo = null
            }
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun VideoEditDialog(
    initial: VideoItemEntity?,
    onDismiss: () -> Unit,
    onSave: (String, VideoSectionCategory, String, String, String) -> Unit
) {
    val hud = LocalHudPalette.current
    var title by remember { mutableStateOf(initial?.title ?: "") }
    var category by remember {
        mutableStateOf(
            VideoSectionCategory.entries.find { it.name == initial?.categoryName }
                ?: VideoSectionCategory.FITNESS
        )
    }
    var linkedContentId by remember { mutableStateOf(initial?.linkedContentId ?: "wall_pushup") }
    var sourceUrl by remember { mutableStateOf(initial?.sourceUrl ?: "") }
    var description by remember { mutableStateOf(initial?.description ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (initial == null) "إضافة فيديو تعليمي" else "تعديل بيانات الفيديو") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("عنوان الفيديو") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("video_title_input")
                )
                Text("التصنيف:", style = MaterialTheme.typography.labelLarge)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    VideoSectionCategory.entries.forEach { cat ->
                        FilterChip(
                            selected = category == cat,
                            onClick = { category = cat },
                            label = { Text(cat.titleAr) }
                        )
                    }
                }
                OutlinedTextField(
                    value = linkedContentId,
                    onValueChange = { linkedContentId = it },
                    label = { Text("معرف التمرين/الدرس المرتبط (مثل wall_pushup)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                if (category == VideoSectionCategory.FITNESS) {
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        FitnessCatalog.exercises.take(5).forEach { ex ->
                            FilterChip(
                                selected = linkedContentId == ex.id,
                                onClick = { linkedContentId = ex.id },
                                label = { Text(ex.titleEn) }
                            )
                        }
                    }
                }
                OutlinedTextField(
                    value = sourceUrl,
                    onValueChange = { sourceUrl = it },
                    label = { Text("رابط YouTube Embed أو MP4 مباشر") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("video_url_input")
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("وصف مختصر") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onSave(title, category, linkedContentId, sourceUrl, description)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = hud.accent),
                modifier = Modifier.testTag("save_video_btn")
            ) {
                Text("حفظ والتحقق", color = CharcoalVoid, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("إلغاء") }
        }
    )
}
