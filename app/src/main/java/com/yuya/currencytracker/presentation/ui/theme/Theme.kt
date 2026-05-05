package com.yuya.currencytracker.presentation.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = Violet,
    onPrimary = Paper,
    primaryContainer = VioletSoft,
    onPrimaryContainer = Paper,
    secondary = Charcoal,
    onSecondary = Paper,
    tertiary = Violet,
    onTertiary = Paper,
    background = Ink,
    onBackground = Paper,
    surface = Charcoal,
    onSurface = Paper,
    surfaceVariant = Graphite,
    onSurfaceVariant = Mist,
    outline = Slate,
    outlineVariant = Slate,
    inverseSurface = Paper,
    inverseOnSurface = Ink,
    scrim = Ink
)

@Composable
fun CurrencyTrackerTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColors,
        typography = Typography,
        content = content
    )
}
