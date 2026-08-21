package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = PrimaryBlue,
    onPrimary = TextOnBlue,
    primaryContainer = PrimaryBlueSoft,
    onPrimaryContainer = PrimaryBlueDark,
    secondary = AccentPurple,
    onSecondary = TextOnBlue,
    secondaryContainer = AccentPurpleSoft,
    onSecondaryContainer = PrimaryBlueDark,
    tertiary = AccentPink,
    onTertiary = TextOnBlue,
    tertiaryContainer = AccentPinkSoft,
    onTertiaryContainer = AccentPink,
    background = BackgroundPaleBlue,
    onBackground = TextPrimary,
    surface = SurfaceWhite,
    onSurface = TextPrimary,
    surfaceVariant = BackgroundSecondary,
    onSurfaceVariant = TextSecondary,
    outline = IncomingBubbleBorder,
    outlineVariant = PrimaryBlueSoft
)

private val DarkColorScheme = darkColorScheme(
    primary = DarkPrimaryBlue,
    onPrimary = TextOnBlue,
    primaryContainer = DarkPrimaryBlueSoft,
    onPrimaryContainer = PrimaryBlueLight,
    secondary = AccentPurple,
    onSecondary = TextOnBlue,
    secondaryContainer = DarkSurfaceCard,
    onSecondaryContainer = PrimaryBlueLight,
    tertiary = AccentPink,
    onTertiary = TextOnBlue,
    tertiaryContainer = DarkSurfaceCard,
    onTertiaryContainer = AccentPink,
    background = DarkBackground,
    onBackground = DarkTextPrimary,
    surface = DarkSurface,
    onSurface = DarkTextPrimary,
    surfaceVariant = DarkSurfaceCard,
    onSurfaceVariant = DarkTextSecondary,
    outline = DarkSurfaceCard,
    outlineVariant = DarkPrimaryBlueSoft
)

@Composable
fun ChatTheme(
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

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    ChatTheme(darkTheme = darkTheme, content = content)
}
