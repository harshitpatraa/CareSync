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
    onSecondaryContainer = Color(0xFF14532D),
    tertiary = CareSyncCyan,
    onTertiary = Color.White,
    tertiaryContainer = CareSyncCyanLight,
    onTertiaryContainer = Color(0xFF0E4D58),
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
    onErrorContainer = Color(0xFF7F1D1D)
)

private val DarkColorScheme = darkColorScheme(
    primary = CareSyncBlueOnDark,
    onPrimary = Color(0xFF08204A),
    primaryContainer = CareSyncBlueContainerDark,
    onPrimaryContainer = CareSyncBlueLight,
    secondary = CareSyncGreenOnDark,
    onSecondary = Color(0xFF0B2E13),
    secondaryContainer = CareSyncGreenContainerDark,
    onSecondaryContainer = CareSyncGreenLight,
    tertiary = CareSyncCyanOnDark,
    onTertiary = Color(0xFF062930),
    tertiaryContainer = CareSyncCyanContainerDark,
    onTertiaryContainer = CareSyncCyanLight,
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
    darkTheme: Boolean = isSystemInDarkTheme(),
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
