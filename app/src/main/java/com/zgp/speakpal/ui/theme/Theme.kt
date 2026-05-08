package com.zgp.speakpal.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = SkyBlue,
    onPrimary = Color.White,
    primaryContainer = SkyBlueContainer,
    onPrimaryContainer = Ink,
    secondary = Mint,
    onSecondary = Color.White,
    secondaryContainer = MintContainer,
    onSecondaryContainer = Ink,
    background = Sand,
    onBackground = Ink,
    surface = Color.White,
    onSurface = Ink,
)

private val DarkColors = darkColorScheme(
    primary = SkyBlueContainer,
    onPrimary = Ink,
    secondary = MintContainer,
    onSecondary = Ink,
    background = Color(0xFF10151C),
    onBackground = Color(0xFFF2F6FB),
    surface = Color(0xFF151B24),
    onSurface = Color(0xFFF2F6FB),
)

@Composable
fun SpeakPalTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colors = if (darkTheme) DarkColors else LightColors
    MaterialTheme(
        colorScheme = colors,
        typography = Typography,
        content = content,
    )
}
