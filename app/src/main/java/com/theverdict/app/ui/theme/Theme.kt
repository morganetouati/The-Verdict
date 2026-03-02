package com.theverdict.app.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val VerdictColorScheme = darkColorScheme(
    primary = Gold,
    onPrimary = TextOnGold,
    primaryContainer = GoldDark,
    onPrimaryContainer = GoldLight,

    secondary = GreenTruth,
    onSecondary = NoirDeep,
    secondaryContainer = GreenTruthDark,

    tertiary = RedLie,
    onTertiary = TextPrimary,
    tertiaryContainer = RedLieDark,

    background = NoirDeep,
    onBackground = TextPrimary,

    surface = Surface,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceLight,
    onSurfaceVariant = TextSecondary,

    outline = TextSecondary,
    outlineVariant = SurfaceLight,

    error = RedLie,
    onError = TextPrimary
)

@Composable
fun VerdictTheme(
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            // Edge-to-edge: only control appearance flags, not colors (deprecated on SDK 35)
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = false
                isAppearanceLightNavigationBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = VerdictColorScheme,
        typography = VerdictTypography,
        content = content
    )
}
