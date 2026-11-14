package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.example.data.AgeGroup
import com.example.data.FitnessLevel
import com.example.ui.components.HolographicBackground
import com.example.ui.components.HolographicGlassCard
import com.example.ui.components.HudTagChip
import com.example.ui.theme.CharcoalVoid
import com.example.ui.theme.LocalHudPalette
import com.example.ui.theme.OrbitronFontFamily
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.delay

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AwakeningScreen(
    onAcceptAwakening: (String, AgeGroup, FitnessLevel) -> Unit
) {
    val hud = LocalHudPalette.current
    var showContent by remember { mutableStateOf(false) }
    var playerName by remember { mutableStateOf("") }
    var selectedAge by remember { mutableStateOf(AgeGroup.TEEN_13_17) }
    var selectedFitness by remember { mutableStateOf(FitnessLevel.BEGINNER) }

    LaunchedEffect(Unit) {
        delay(220)
        showContent = true
    }

    HolographicBackground {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            AnimatedVisibility(
                visible = showContent,
                enter = fadeIn(tween(650)) + slideInVertically(tween(550)) { it / 6 }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 580.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    HudTagChip(text = "SYSTEM INITIALIZATION", color = hud.accent)
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "AWAKENING...",
                        style = MaterialTheme.typography.displayLarge,
                        color = hud.accent,
                        fontFamily = OrbitronFontFamily,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Image(
                        painter = painterResource(id = R.drawable.img_awakening_portal),
                        contentDescription = "بوابة الصحوة",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(170.dp)
                            .clip(RoundedCornerShape(18.dp))
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    HolographicGlassCard(glowAccent = true, modifier = Modifier.fillMaxWidth()) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "رحلتك تبدأ الآن.\nلا أحد سيغير حياتك بدلًا منك.\nابدأ من المستوى الأول.",
                                style = MaterialTheme.typography.titleLarge,
                                color = TextPrimary,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "النظام يدعم صحتك وتطورك بتوازن — بدون عقاب على الراحة أو أهداف قاسية.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSecondary,
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    HolographicGlassCard(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "هوية اللاعب",
                                style = MaterialTheme.typography.titleMedium,
                                color = hud.accent
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = playerName,
                                onValueChange = { playerName = it },
                                label = { Text("اسم اللاعب") },
                                placeholder = { Text("اكتب اسمك أو لقبك...") },
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = hud.accent,
                                    unfocusedBorderColor = hud.border,
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary,
                                    focusedLabelColor = hud.accent,
                                    unfocusedLabelColor = TextSecondary
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("awakening_name_input")
                            )

                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                text = "الفئة العمرية (لتخصيص هدف النوم الصحي)",
                                style = MaterialTheme.typography.labelLarge,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                AgeGroup.entries.forEach { group ->
                                    FilterChip(
                                        selected = selectedAge == group,
                                        onClick = { selectedAge = group },
                                        label = { Text(group.titleAr) },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = hud.accent.copy(alpha = 0.22f),
                                            selectedLabelColor = hud.accent
                                        ),
                                        modifier = Modifier.testTag("age_chip_${group.name}")
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "مستوى اللياقة الحالي (لتكييف المجموعات والتكرارات)",
                                style = MaterialTheme.typography.labelLarge,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                FitnessLevel.entries.forEach { level ->
                                    FilterChip(
                                        selected = selectedFitness == level,
                                        onClick = { selectedFitness = level },
                                        label = { Text(level.titleAr) },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = hud.secondaryAccent.copy(alpha = 0.28f),
                                            selectedLabelColor = TextPrimary
                                        ),
                                        modifier = Modifier.testTag("fitness_chip_${level.name}")
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = {
                            onAcceptAwakening(
                                playerName.trim().ifEmpty { "البطل" },
                                selectedAge,
                                selectedFitness
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = hud.accent),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 54.dp)
                            .testTag("accept_awakening_button")
                    ) {
                        Icon(Icons.Filled.Bolt, contentDescription = null, tint = CharcoalVoid)
                        Text(
                            text = "قبول الصحوة والبدء من LEVEL 1",
                            color = CharcoalVoid,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }
        }
    }
}
