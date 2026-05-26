package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = MintSecondary, // D0BCFF (lighter purple for dark theme readability)
    secondary = EmeraldPrimary, // 6750A4
    tertiary = BlueTertiary, // Rose-accent
    background = DarkBg, // 141218
    surface = DarkSurface, // 211F26
    surfaceVariant = DarkSurfaceElevated, // 2B2930
    onPrimary = DarkBg,
    onSecondary = TextPrimaryDark,
    onTertiary = DarkBg,
    onBackground = TextPrimaryDark,
    onSurface = TextPrimaryDark,
    onSurfaceVariant = TextSecondaryDark
)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryLight, // 6750A4
    secondary = SecondaryLight, // EADDFF
    tertiary = TertiaryLight, // 21005D
    background = LightBg, // FEF7FF
    surface = LightSurface, // FFFFFF
    surfaceVariant = Color(0xFFF3EDF7), // Neutral M3 Light Card Background
    onPrimary = LightSurface,
    onSecondary = TertiaryLight,
    onTertiary = LightSurface,
    onBackground = TextPrimaryLight, // 1D1B20
    onSurface = TextPrimaryLight,
    onSurfaceVariant = TextSecondaryLight // 49454F
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // We intentionally use our custom Slate-Emerald theme for high-fidelity branding.
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
