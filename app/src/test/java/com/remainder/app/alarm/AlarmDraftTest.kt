package com.remainder.app.alarm

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class AlarmDraftTest {

    @Test
    fun copyLeavesOriginalUnchanged() {
        val original = AlarmDraft(hour = 7, minute = 0, title = "Gym")
        val updated = original.copy(minute = 15)
        assertEquals(AlarmDraft(hour = 7, minute = 0, title = "Gym"), original)
        assertEquals(AlarmDraft(hour = 7, minute = 15, title = "Gym"), updated)
        assertNotEquals(original, updated)
    }
}
