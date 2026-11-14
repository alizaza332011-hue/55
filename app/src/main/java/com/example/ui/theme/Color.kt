package com.example.ui.theme

import androidx.compose.ui.graphics.Color

val CharcoalVoid = Color(0xFF07090F)
val CharcoalDeep = Color(0xFF0B101C)
val GlassSurface = Color(0xCC101626)
val GlassSurfaceSolid = Color(0xFF101626)
val GlassSurfaceElevated = Color(0xE6162038)
val GlassBorder = Color(0xFF263B6A)

val ElectricCyan = Color(0xFF00D4FF)
val DeepViolet = Color(0xFF7B42F6)
val NeonViolet = Color(0xFFA87BFF)
val QuestGold = Color(0xFFF5B700)
val EmeraldSuccess = Color(0xFF00E5A3)
val SoftCoral = Color(0xFFFF5A79)

val TextPrimary = Color(0xFFEEF4FF)
val TextSecondary = Color(0xFF94A3C8)
val TextMuted = Color(0xFF5D6E94)

enum class HudAccentTheme(
    val id: String,
    val titleAr: String,
    val primary: Color,
    val secondary: Color,
    val glow: Color
) {
    ELECTRIC_BLUE(
        id = "theme_electric_blue",
        titleAr = "الأزرق الكهربائي",
        primary = Color(0xFF00D4FF),
        secondary = Color(0xFF7B42F6),
        glow = Color(0x6600D4FF)
    ),
    SHADOW_VIOLET(
        id = "theme_shadow_violet",
        titleAr = "البنفسجي الملكي",
        primary = Color(0xFFA87BFF),
        secondary = Color(0xFF00D4FF),
        glow = Color(0x66A87BFF)
    ),
    SOLAR_GOLD(
        id = "theme_solar_gold",
        titleAr = "الوهج الذهبي",
        primary = Color(0xFFF5B700),
        secondary = Color(0xFF00D4FF),
        glow = Color(0x66F5B700)
    ),
    EMERALD_MATRIX(
        id = "theme_emerald_matrix",
        titleAr = "الزمرد الرقمي",
        primary = Color(0xFF00E5A3),
        secondary = Color(0xFF00D4FF),
        glow = Color(0x6600E5A3)
    );

    companion object {
        fun fromId(id: String): HudAccentTheme = entries.find { it.id == id } ?: ELECTRIC_BLUE
    }
}
