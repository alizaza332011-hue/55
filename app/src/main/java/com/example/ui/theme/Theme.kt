package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection

@Immutable
data class HudPalette(
    val accent: Color = ElectricCyan,
    val secondaryAccent: Color = DeepViolet,
    val glow: Color = Color(0x6600D4FF),
    val glassSurface: Color = GlassSurface,
    val glassSurfaceElevated: Color = GlassSurfaceElevated,
    val border: Color = GlassBorder,
    val gold: Color = QuestGold,
    val success: Color = EmeraldSuccess,
    val coral: Color = SoftCoral,
    val backgroundId: String = "bg_obsidian_grid"
)

val LocalHudPalette = staticCompositionLocalOf { HudPalette() }

@Composable
fun RealLifeLevelingTheme(
    accentTheme: HudAccentTheme = HudAccentTheme.ELECTRIC_BLUE,
    backgroundId: String = "bg_obsidian_grid",
    content: @Composable () -> Unit
) {
    val colorScheme = darkColorScheme(
        primary = accentTheme.primary,
        onPrimary = CharcoalVoid,
        secondary = accentTheme.secondary,
        onSecondary = TextPrimary,
        tertiary = QuestGold,
        onTertiary = CharcoalVoid,
        background = CharcoalVoid,
        onBackground = TextPrimary,
        surface = GlassSurfaceSolid,
        onSurface = TextPrimary,
        surfaceVariant = GlassSurfaceElevated,
        onSurfaceVariant = TextSecondary,
        outline = GlassBorder,
        error = SoftCoral,
        onError = CharcoalVoid
    )

    val hudPalette = HudPalette(
        accent = accentTheme.primary,
        secondaryAccent = accentTheme.secondary,
        glow = accentTheme.glow,
        backgroundId = backgroundId
    )

    CompositionLocalProvider(
        LocalHudPalette provides hudPalette,
        LocalLayoutDirection provides LayoutDirection.Rtl
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = AppTypography,
            content = content
        )
    }
}
