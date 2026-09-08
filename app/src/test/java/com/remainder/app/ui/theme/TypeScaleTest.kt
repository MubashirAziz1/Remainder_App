package com.remainder.app.ui.theme

import androidx.compose.ui.unit.sp
import org.junit.Assert.assertEquals
import org.junit.Test

class TypeScaleTest {

    @Test
    fun displaySmallUsesHeadlineSize() {
        assertEquals(36.sp, RemainderTypography.displaySmall.fontSize)
    }

    @Test
    fun bodyLargeIsReadable() {
        assertEquals(16.sp, RemainderTypography.bodyLarge.fontSize)
    }
}
