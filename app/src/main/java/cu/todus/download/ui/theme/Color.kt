package cu.todus.download.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

enum class AppAccent(
    val id: String,
    val displayName: String,
    val previewColor: Color,
    val lightPrimary: Color,
    val darkPrimary: Color,
    val lightContainer: Color,
    val darkContainer: Color
) {
    CYAN(
        id = "CYAN",
        displayName = "Cian Eléctrico",
        previewColor = Color(0xFF00E5FF),
        lightPrimary = Color(0xFF00838F),
        darkPrimary = Color(0xFF00E5FF),
        lightContainer = Color(0xFFE0F7FA),
        darkContainer = Color(0xFF00434F)
    ),
    BLUE(
        id = "BLUE",
        displayName = "Azul Zafiro",
        previewColor = Color(0xFF0284C7),
        lightPrimary = Color(0xFF0284C7),
        darkPrimary = Color(0xFF38BDF8),
        lightContainer = Color(0xFFE0F2FE),
        darkContainer = Color(0xFF0C4A6E)
    ),
    VIOLET(
        id = "VIOLET",
        displayName = "Violeta Elegante",
        previewColor = Color(0xFF8B5CF6),
        lightPrimary = Color(0xFF7C3AED),
        darkPrimary = Color(0xFFA78BFA),
        lightContainer = Color(0xFFEDE9FE),
        darkContainer = Color(0xFF3B1F70)
    ),
    EMERALD(
        id = "EMERALD",
        displayName = "Verde Esmeralda",
        previewColor = Color(0xFF10B981),
        lightPrimary = Color(0xFF059669),
        darkPrimary = Color(0xFF34D399),
        lightContainer = Color(0xFFD1FAE5),
        darkContainer = Color(0xFF064E3B)
    ),
    ORANGE(
        id = "ORANGE",
        displayName = "Naranja Sunset",
        previewColor = Color(0xFFF97316),
        lightPrimary = Color(0xFFEA580C),
        darkPrimary = Color(0xFFFB923C),
        lightContainer = Color(0xFFFFEDD5),
        darkContainer = Color(0xFF7C2D12)
    ),
    ROSE(
        id = "ROSE",
        displayName = "Rosa Coral",
        previewColor = Color(0xFFF43F5E),
        lightPrimary = Color(0xFFE11D48),
        darkPrimary = Color(0xFFFB7185),
        lightContainer = Color(0xFFFFE4E6),
        darkContainer = Color(0xFF881337)
    );

    companion object {
        fun fromId(id: String): AppAccent =
            values().firstOrNull { it.id.equals(id, ignoreCase = true) } ?: CYAN
    }
}

// Light mode color palette
val LightBg = Color(0xFFF8FAFC)
val LightSurface = Color(0xFFFFFFFF)
val LightSurfaceCard = Color(0xFFF1F5F9)
val LightSurfaceCardAlt = Color(0xFFE2E8F0)
val LightSurfaceBorder = Color(0xFFCBD5E1)
val LightPrimary = Color(0xFF007E8A)
val LightOnPrimary = Color(0xFFFFFFFF)
val LightPrimaryContainer = Color(0xFFE0F7FA)
val LightOnPrimaryContainer = Color(0xFF00363A)
val LightSecondary = Color(0xFF5B4FFF)
val LightOnSecondary = Color(0xFFFFFFFF)
val LightSecondaryContainer = Color(0xFFEDE9FE)
val LightOnSecondaryContainer = Color(0xFF2E1065)
val LightTextPrimary = Color(0xFF0F172A)
val LightTextSecondary = Color(0xFF475569)
val LightTextMuted = Color(0xFF64748B)

// Dark mode color palette
val DarkBg = Color(0xFF0A0F1D)
val DarkSurface = Color(0xFF111827)
val DarkSurfaceCard = Color(0xFF1E293B)
val DarkSurfaceCardAlt = Color(0xFF243248)
val DarkSurfaceBorder = Color(0xFF334155)
val DarkPrimary = Color(0xFF00E5FF)
val DarkOnPrimary = Color(0xFF002930)
val DarkPrimaryContainer = Color(0xFF00434F)
val DarkOnPrimaryContainer = Color(0xFF80F2FF)
val DarkSecondary = Color(0xFF9D8CFF)
val DarkOnSecondary = Color(0xFF1E1459)
val DarkSecondaryContainer = Color(0xFF362B75)
val DarkOnSecondaryContainer = Color(0xFFDDD6FE)
val DarkTextPrimary = Color(0xFFF8FAFC)
val DarkTextSecondary = Color(0xFFCBD5E1)
val DarkTextMuted = Color(0xFF94A3B8)

// AMOLED mode color palette
val AmoledBg = Color(0xFF000000)
val AmoledSurface = Color(0xFF0B0E14)
val AmoledSurfaceCard = Color(0xFF121722)
val AmoledSurfaceCardAlt = Color(0xFF1A2130)
val AmoledSurfaceBorder = Color(0xFF263245)

// Status & Accents (Accessible in both modes)
val EmeraldSuccess = Color(0xFF10B981)
val EmeraldSuccessContainer = Color(0xFFD1FAE5)
val CrimsonError = Color(0xFFEF4444)
val CrimsonErrorContainer = Color(0xFFFEE2E2)
val AmberWarn = Color(0xFFF59E0B)
val AmberWarnContainer = Color(0xFFFEF3C7)
val IndigoInfo = Color(0xFF6366F1)
val AccentCyan = Color(0xFF00E5FF)
val AccentTurquoise = Color(0xFF00BFA5)
val AccentViolet = Color(0xFF8B5CF6)
val ElectricBlue = Color(0xFF0091EA)

// Legacy compatibility aliases
val BgDark = DarkBg
val BgAmoled = AmoledBg
val SurfaceDark = DarkSurface
val SurfaceCard = DarkSurfaceCard
val SurfaceCardAlt = DarkSurfaceCardAlt
val SurfaceBorder = DarkSurfaceBorder
val TextPrimary = DarkTextPrimary
val TextSecondary = DarkTextSecondary
val TextMuted = DarkTextMuted
val NeonCyanGlow = Color(0x3300E5FF)
val VioletGlow = Color(0x338B5CF6)

@Immutable
data class ExtendedColors(
    val surfaceCard: Color,
    val surfaceCardAlt: Color,
    val surfaceBorder: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textMuted: Color,
    val success: Color,
    val successContainer: Color,
    val warning: Color,
    val warningContainer: Color,
    val error: Color,
    val errorContainer: Color,
    val isDark: Boolean
)

val LocalExtendedColors = staticCompositionLocalOf {
    ExtendedColors(
        surfaceCard = DarkSurfaceCard,
        surfaceCardAlt = DarkSurfaceCardAlt,
        surfaceBorder = DarkSurfaceBorder,
        textPrimary = DarkTextPrimary,
        textSecondary = DarkTextSecondary,
        textMuted = DarkTextMuted,
        success = EmeraldSuccess,
        successContainer = EmeraldSuccessContainer,
        warning = AmberWarn,
        warningContainer = AmberWarnContainer,
        error = CrimsonError,
        errorContainer = CrimsonErrorContainer,
        isDark = true
    )
}
