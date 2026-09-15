package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryCyan,
    onPrimary = Color(0xFF00354E),
    primaryContainer = Color(0xFF004D71),
    onPrimaryContainer = Color(0xFFC7E7FF),
    secondary = SecondaryTeal,
    onSecondary = Color(0xFF003831),
    secondaryContainer = Color(0xFF005147),
    onSecondaryContainer = Color(0xFF73F8D4),
    tertiary = AccentEmerald,
    onTertiary = Color(0xFF003920),
    background = DarkNavyBg,
    onBackground = TextPrimary,
    surface = DarkNavySurface,
    onSurface = TextPrimary,
    surfaceVariant = DarkNavyCard,
    onSurfaceVariant = TextSecondary,
    outline = DarkNavyBorder
)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryCyanVariant,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFC7E7FF),
    onPrimaryContainer = Color(0xFF001E2F),
    secondary = Color(0xFF0D9488),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFA7F3D0),
    onSecondaryContainer = Color(0xFF002018),
    tertiary = Color(0xFF059669),
    onTertiary = Color.White,
    background = LightBg,
    onBackground = LightTextPrimary,
    surface = LightSurface,
    onSurface = LightTextPrimary,
    surfaceVariant = LightCard,
    onSurfaceVariant = LightTextSecondary,
    outline = LightBorder
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Default to deep medical dark mode for optimal readability
    dynamicColor: Boolean = false, // Keep consistent medical theme
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

