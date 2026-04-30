package com.example.calmtask.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = AccentDefault,
    onPrimary = Color.White,
    primaryContainer = Color(0xFF1A3A8A),
    onPrimaryContainer = Color(0xFFD6E2FF),
    secondary = Color(0xFFA5D6FF),
    onSecondary = Color(0xFF003258),
    secondaryContainer = Color(0xFF00497D),
    onSecondaryContainer = Color(0xFFD1E3FF),
    tertiary = Color(0xFFE9BDFF),
    onTertiary = Color(0xFF4A0075),
    tertiaryContainer = Color(0xFF681B9B),
    onTertiaryContainer = Color(0xFFF7D8FF),
    background = BackgroundDark,
    onBackground = TextPrimary,
    surface = SurfaceDark,
    onSurface = TextPrimary,
    surfaceVariant = CardDark,
    onSurfaceVariant = TextSecondary,
    error = ErrorSoft,
    onError = Color.Black,
    outline = TextSecondary.copy(alpha = 0.5f)
)

@Composable
fun CalmTaskTheme(content: @Composable () -> Unit) {
    val colorScheme = DarkColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            window.navigationBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
