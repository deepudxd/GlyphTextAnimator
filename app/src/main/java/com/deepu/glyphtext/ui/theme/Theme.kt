package com.deepu.glyphtext.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = NothingRed,
    onPrimary = LedOn,
    primaryContainer = NothingRedDark,
    secondary = AccentGreen,
    tertiary = AccentAmber,
    background = NothingBlack,
    surface = NothingSurface,
    surfaceVariant = NothingSurfaceVariant,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    onSurfaceVariant = TextSecondary,
    outline = TextHint
)

@Composable
fun GlyphTextAnimatorTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
