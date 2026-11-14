package com.caresync.app.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = CareSyncBlue,
    onPrimary = Color.White,
    primaryContainer = CareSyncBlueLight,
    onPrimaryContainer = CareSyncBlueDark,
    secondary = CareSyncGreen,
    onSecondary = Color.White,
    secondaryContainer = CareSyncGreenLight,
    onSecondaryContainer = Color(0xFF065F46),
    tertiary = CareSyncCyan,
    onTertiary = Color.White,
    tertiaryContainer = CareSyncCyanLight,
    onTertiaryContainer = Color(0xFF155E75),
    background = CareSyncBackgroundLight,
    onBackground = CareSyncTextPrimaryLight,
    surface = CareSyncSurfaceLight,
    onSurface = CareSyncTextPrimaryLight,
    surfaceVariant = CareSyncSurfaceVariantLight,
    onSurfaceVariant = CareSyncTextSecondaryLight,
    outline = CareSyncOutlineLight,
    error = CriticalRed,
    onError = Color.White,
    errorContainer = CriticalRedSoft,
    onErrorContainer = Color(0xFF991B1B)
)

private val DarkColorScheme = darkColorScheme(
    primary = CareSyncBlueOnDark,
    onPrimary = Color(0xFF0F172A),
    primaryContainer = CareSyncBlueContainerDark,
    onPrimaryContainer = Color(0xFFBFDBFE),
    secondary = CareSyncGreenOnDark,
    onSecondary = Color(0xFF022C22),
    secondaryContainer = CareSyncGreenContainerDark,
    onSecondaryContainer = Color(0xFFA7F3D0),
    tertiary = CareSyncCyanOnDark,
    onTertiary = Color(0xFF083344),
    tertiaryContainer = CareSyncCyanContainerDark,
    onTertiaryContainer = Color(0xFFA5F3FC),
    background = CareSyncBackgroundDark,
    onBackground = CareSyncTextPrimaryDark,
    surface = CareSyncSurfaceDark,
    onSurface = CareSyncTextPrimaryDark,
    surfaceVariant = CareSyncSurfaceVariantDark,
    onSurfaceVariant = CareSyncTextSecondaryDark,
    outline = CareSyncOutlineDark,
    error = Color(0xFFF87171),
    onError = Color(0xFF450A0A),
    errorContainer = Color(0xFF5C1616),
    onErrorContainer = Color(0xFFFECACA)
)

@Composable
fun CareSyncTheme(
    darkTheme: Boolean = true, // Default to true for the requested classy dark theme
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = CareSyncTypography,
        shapes = CareSyncShapes,
        content = content
    )
}
