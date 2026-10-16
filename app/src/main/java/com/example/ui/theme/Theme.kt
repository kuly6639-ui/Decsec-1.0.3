package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DecSecColorScheme = darkColorScheme(
    primary = NeonGreen,
    onPrimary = PureBlackText,
    primaryContainer = NeonGreenMuted,
    onPrimaryContainer = NeonGreenBright,
    secondary = NeonGreenDim,
    onSecondary = PureBlackText,
    secondaryContainer = NeonGreenMuted,
    onSecondaryContainer = NeonGreenBright,
    tertiary = NeonGreenBright,
    onTertiary = PureBlackText,
    tertiaryContainer = NeonGreenMuted,
    onTertiaryContainer = NeonGreen,
    background = PureBlack,
    onBackground = NeonGreen,
    surface = PureBlack,
    onSurface = NeonGreen,
    surfaceVariant = TerminalSurfaceVariant,
    onSurfaceVariant = TextGreenDim,
    outline = TerminalBorder,
    error = AlertRed,
    onError = PureBlackText
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DecSecColorScheme,
        typography = CyberTypography,
        content = content
    )
}
