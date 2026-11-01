package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = StudioPrimary,
    onPrimary = StudioTextPrimary,
    primaryContainer = StudioSurfaceVariant,
    onPrimaryContainer = StudioPrimaryBright,
    secondary = StudioSecondary,
    onSecondary = StudioBackground,
    secondaryContainer = StudioSurfaceVariant,
    onSecondaryContainer = StudioSecondaryBright,
    tertiary = StudioTertiary,
    background = StudioBackground,
    onBackground = StudioTextPrimary,
    surface = StudioSurface,
    onSurface = StudioTextPrimary,
    surfaceVariant = StudioSurfaceVariant,
    onSurfaceVariant = StudioTextSecondary,
    outline = StudioCardBorder
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Default to studio dark theme
    content: @Composable () -> Unit
) {
    val colorScheme = DarkColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = StudioBackground.toArgb()
                window.navigationBarColor = StudioBackground.toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
                WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
