package com.fredy.gamevault.core.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Primary,
    onPrimary = OnDarkBackground,
    primaryContainer = PrimaryDark,
    onPrimaryContainer = PrimaryLight,
    secondary = Secondary,
    onSecondary = DarkBackground,
    secondaryContainer = SecondaryDark,
    background = DarkBackground,
    onBackground = OnDarkBackground,
    surface = DarkSurface,
    onSurface = OnDarkSurface,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = SubtleText,
    error = ErrorRed,
    onError = OnDarkBackground,
    errorContainer = Color(0xFF3D0000),
    tertiary = Accent,
    outline = Color(0xFF3A3A5C)
)

private val LightColorScheme = lightColorScheme(
    primary = Primary,
    onPrimary = LightSurface,
    primaryContainer = PrimaryLight,
    onPrimaryContainer = PrimaryDark,
    secondary = SecondaryDark,
    onSecondary = LightSurface,
    background = LightBackground,
    onBackground = Color(0xFF1A1A2E),
    surface = LightSurface,
    onSurface = Color(0xFF1A1A2E),
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = Color(0xFF666680),
    error = ErrorRed,
    tertiary = Accent,
    outline = Color(0xFFCCCCDD)
)

@Composable
fun GameVaultTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}