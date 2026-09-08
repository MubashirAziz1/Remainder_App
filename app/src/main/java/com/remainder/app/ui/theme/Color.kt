package com.remainder.app.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

val RemainderTeal = Color(0xFF0F6B5C)
val RemainderTealBright = Color(0xFF5FD0BC)
val RemainderPaper = Color(0xFFF6F3EE)
val RemainderInk = Color(0xFF1A1C1B)
val RemainderAmber = Color(0xFFE8A838)

val RemainderLightColorScheme = lightColorScheme(
    primary = RemainderTeal,
    onPrimary = Color.White,
    secondary = RemainderAmber,
    onSecondary = RemainderInk,
    surface = RemainderPaper,
    onSurface = RemainderInk,
    background = RemainderPaper,
    onBackground = RemainderInk
)

val RemainderDarkColorScheme = darkColorScheme(
    primary = RemainderTealBright,
    onPrimary = RemainderInk,
    secondary = RemainderAmber,
    onSecondary = RemainderInk,
    surface = Color(0xFF121212),
    onSurface = Color(0xFFF2F0EA),
    background = Color(0xFF121212),
    onBackground = Color(0xFFF2F0EA)
)
