package com.bluetoothserialmonitor.presentation.theme

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

// Dark Color Scheme (Default)
private val DarkColorScheme = darkColorScheme(
    primary = NeonBlue,
    onPrimary = DeepNavy,
    primaryContainer = SurfaceElevated,
    onPrimaryContainer = NeonBlue,
    
    secondary = NeonPurple,
    onSecondary = DeepNavy,
    secondaryContainer = SurfaceElevated,
    onSecondaryContainer = NeonPurple,
    
    tertiary = NeonGreen,
    onTertiary = DeepNavy,
    tertiaryContainer = SurfaceElevated,
    onTertiaryContainer = NeonGreen,
    
    error = NeonRed,
    onError = DeepNavy,
    errorContainer = SurfaceElevated,
    onErrorContainer = NeonRed,
    
    background = DeepNavy,
    onBackground = TextPrimary,
    
    surface = Onyx,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceVariant,
    onSurfaceVariant = TextSecondary,
    
    outline = DividerColor,
    outlineVariant = CardBorder
)

// Light Color Scheme (Optional fallback)
private val LightColorScheme = darkColorScheme(
    primary = NeonBlue,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE0F4FF),
    onPrimaryContainer = Color(0xFF004A7C),
    
    secondary = NeonPurple,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFF5E9FF),
    onSecondaryContainer = Color(0xFF4B2168),
    
    tertiary = NeonGreen,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFD0FFE5),
    onTertiaryContainer = Color(0xFF004A2A),
    
    error = NeonRed,
    onError = Color.White,
    errorContainer = Color(0xFFFFDAD8),
    onErrorContainer = Color(0xFF680018),
    
    background = Color(0xFFFAFAFA),
    onBackground = Color(0xFF1A1C1E),
    
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF1A1C1E),
    surfaceVariant = Color(0xFFE8E8E8),
    onSurfaceVariant = Color(0xFF454748)
)

@Composable
fun BluetoothSerialMonitorTheme(
    darkTheme: Boolean = true, // Default to dark mode
    dynamicColor: Boolean = false, // Disable dynamic color for consistent branding
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = DeepNavy.toArgb()
            window.navigationBarColor = DeepNavy.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
