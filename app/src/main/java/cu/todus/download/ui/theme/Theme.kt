package cu.todus.download.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import cu.todus.download.data.ThemeMode

private fun createLightColorScheme(accent: AppAccent) = lightColorScheme(
    primary = accent.lightPrimary,
    onPrimary = LightOnPrimary,
    primaryContainer = accent.lightContainer,
    onPrimaryContainer = accent.lightPrimary,
    secondary = LightSecondary,
    onSecondary = LightOnSecondary,
    secondaryContainer = LightSecondaryContainer,
    onSecondaryContainer = LightOnSecondaryContainer,
    background = LightBg,
    onBackground = LightTextPrimary,
    surface = LightSurface,
    onSurface = LightTextPrimary,
    surfaceVariant = LightSurfaceCard,
    onSurfaceVariant = LightTextSecondary,
    outline = LightSurfaceBorder,
    outlineVariant = LightSurfaceCardAlt,
    error = CrimsonError,
    onError = LightOnPrimary
)

private fun createDarkColorScheme(accent: AppAccent) = darkColorScheme(
    primary = accent.darkPrimary,
    onPrimary = DarkOnPrimary,
    primaryContainer = accent.darkContainer,
    onPrimaryContainer = accent.darkPrimary,
    secondary = DarkSecondary,
    onSecondary = DarkOnSecondary,
    secondaryContainer = DarkSecondaryContainer,
    onSecondaryContainer = DarkOnSecondaryContainer,
    background = DarkBg,
    onBackground = DarkTextPrimary,
    surface = DarkSurface,
    onSurface = DarkTextPrimary,
    surfaceVariant = DarkSurfaceCard,
    onSurfaceVariant = DarkTextSecondary,
    outline = DarkSurfaceBorder,
    outlineVariant = DarkSurfaceCardAlt,
    error = CrimsonError,
    onError = DarkOnPrimary
)

object AppTheme {
    val colors: ExtendedColors
        @Composable
        get() = LocalExtendedColors.current
}

@Composable
fun TodusDownloadTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    dynamicColor: Boolean = false,
    accent: AppAccent = AppAccent.CYAN,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val systemInDark = isSystemInDarkTheme()
    val isDark = when (themeMode) {
        ThemeMode.SYSTEM -> systemInDark
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }

    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (isDark) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        isDark -> createDarkColorScheme(accent)
        else -> createLightColorScheme(accent)
    }

    val extendedColors = if (isDark) {
        ExtendedColors(
            surfaceCard = DarkSurfaceCard,
            surfaceCardAlt = DarkSurfaceCardAlt,
            surfaceBorder = DarkSurfaceBorder,
            textPrimary = DarkTextPrimary,
            textSecondary = DarkTextSecondary,
            textMuted = DarkTextMuted,
            success = EmeraldSuccess,
            successContainer = EmeraldSuccess.copy(alpha = 0.2f),
            warning = AmberWarn,
            warningContainer = AmberWarn.copy(alpha = 0.2f),
            error = CrimsonError,
            errorContainer = CrimsonError.copy(alpha = 0.2f),
            isDark = true
        )
    } else {
        ExtendedColors(
            surfaceCard = LightSurfaceCard,
            surfaceCardAlt = LightSurfaceCardAlt,
            surfaceBorder = LightSurfaceBorder,
            textPrimary = LightTextPrimary,
            textSecondary = LightTextSecondary,
            textMuted = LightTextMuted,
            success = EmeraldSuccess,
            successContainer = EmeraldSuccessContainer,
            warning = AmberWarn,
            warningContainer = AmberWarnContainer,
            error = CrimsonError,
            errorContainer = CrimsonErrorContainer,
            isDark = false
        )
    }

    CompositionLocalProvider(LocalExtendedColors provides extendedColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = TodusTypography,
            content = content
        )
    }
}

private val TodusTypography = androidx.compose.material3.Typography(
    displayLarge = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Black, fontSize = 34.sp, letterSpacing = (-1.0).sp),
    displayMedium = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.ExtraBold, fontSize = 28.sp, letterSpacing = (-0.8).sp),
    displaySmall = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Bold, fontSize = 24.sp, letterSpacing = (-0.5).sp),
    headlineMedium = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Bold, fontSize = 20.sp, letterSpacing = (-0.4).sp),
    titleLarge = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Bold, fontSize = 18.sp),
    titleMedium = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.SemiBold, fontSize = 16.sp),
    titleSmall = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.SemiBold, fontSize = 14.sp),
    bodyLarge = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Normal, fontSize = 16.sp),
    bodyMedium = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Normal, fontSize = 14.sp),
    bodySmall = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Normal, fontSize = 12.sp),
    labelLarge = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Bold, fontSize = 14.sp),
    labelMedium = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.SemiBold, fontSize = 12.sp),
    labelSmall = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Medium, fontSize = 11.sp)
)
