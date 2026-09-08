package com.remainder.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun RemainderTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = RemainderLightColorScheme,
        typography = RemainderTypography,
        content = content
    )
}
