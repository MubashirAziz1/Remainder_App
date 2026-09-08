package com.remainder.app.ui.theme

import androidx.compose.ui.graphics.Color
import org.junit.Assert.assertEquals
import org.junit.Test

class ThemeColorTest {

    @Test
    fun lightPrimaryIsRemainderTeal() {
        assertEquals(Color(0xFF0F6B5C), RemainderLightColorScheme.primary)
    }

    @Test
    fun darkPrimaryIsRemainderTeal() {
        assertEquals(Color(0xFF5FD0BC), RemainderDarkColorScheme.primary)
    }

    @Test
    fun lightSurfaceIsWarmPaper() {
        assertEquals(Color(0xFFF6F3EE), RemainderLightColorScheme.surface)
    }
}
