package com.Dadnoto2.Piwwof.core.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val SugarBloomScheme = lightColorScheme(
    primary = BloomPink,
    onPrimary = TextOnAccent,
    primaryContainer = SurfaceSunken,
    onPrimaryContainer = TextPrimary,
    secondary = GrapePurple,
    onSecondary = TextOnAccent,
    secondaryContainer = SurfaceAlt,
    onSecondaryContainer = TextPrimary,
    tertiary = CoinGold,
    onTertiary = TextPrimary,
    background = BlushBase,
    onBackground = TextPrimary,
    surface = SurfaceWhite,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceAlt,
    onSurfaceVariant = TextSecondary,
    outline = GrapePurple,
    error = DangerRose,
    onError = TextOnAccent
)

@Composable
fun AppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = SugarBloomScheme,
        typography = AppTypography,
        content = content
    )
}
