package com.wsluoyj.sdaily.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// 低饱和护眼色板（与效果图一致）
private val Green = Color(0xFF6FA287)
private val GreenDark = Color(0xFF4E8168)
private val BgLight = Color(0xFFEEF3E8)
private val SurfaceLight = Color(0xFFF6F9F2)
private val OnSurfaceLight = Color(0xFF2D3A31)

private val LightColors = lightColorScheme(
    primary = Green,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFDCEBDF),
    onPrimaryContainer = Color(0xFF1F3A2A),
    secondary = Color(0xFF8BA896),
    background = BgLight,
    onBackground = OnSurfaceLight,
    surface = SurfaceLight,
    onSurface = OnSurfaceLight,
    surfaceVariant = Color(0xFFDEE8DC),
    onSurfaceVariant = Color(0xFF556459),
    outline = Color(0xFFB7C6B8)
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF9CC7AE),
    onPrimary = Color(0xFF14382A),
    primaryContainer = GreenDark,
    onPrimaryContainer = Color(0xFFDCEBDF),
    secondary = Color(0xFF9FC2AC),
    background = Color(0xFF121613),
    onBackground = Color(0xFFE2E8E1),
    surface = Color(0xFF1A201C),
    onSurface = Color(0xFFE2E8E1),
    surfaceVariant = Color(0xFF2A342E),
    onSurfaceVariant = Color(0xFFB7C6B8),
    outline = Color(0xFF556459)
)

@Composable
fun SdailyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        content = content
    )
}
