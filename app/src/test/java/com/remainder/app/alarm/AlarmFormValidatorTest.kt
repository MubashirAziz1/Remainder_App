package com.remainder.app.alarm

import org.junit.Assert.assertEquals
import org.junit.Test

class AlarmFormValidatorTest {

    @Test
    fun emptyTitleIsRejected() {
        val result = AlarmFormValidator.confirm(hour = 7, minute = 0, title = "")
        assertEquals(AlarmConfirmResult.Rejected(AlarmFormValidator.TITLE_REQUIRED), result)
    }

    @Test
    fun whitespaceOnlyTitleIsRejected() {
        val result = AlarmFormValidator.confirm(hour = 7, minute = 0, title = "   \t\n")
        assertEquals(AlarmConfirmResult.Rejected(AlarmFormValidator.TITLE_REQUIRED), result)
    }

    @Test
    fun validTitleIsAcceptedAndTrimmed() {
        val result = AlarmFormValidator.confirm(hour = 14, minute = 5, title = "  Gym  ")
        assertEquals(AlarmConfirmResult.Accepted(AlarmDraft(hour = 14, minute = 5, title = "Gym")), result)
    }

    @Test
    fun unicodeTitleIsAccepted() {
        val result = AlarmFormValidator.confirm(hour = 0, minute = 0, title = "闹钟☕")
        assertEquals(AlarmConfirmResult.Accepted(AlarmDraft(hour = 0, minute = 0, title = "闹钟☕")), result)
    }

    @Test
    fun hourIsCoercedInto24HourRange() {
        val low = AlarmFormValidator.confirm(hour = -1, minute = 0, title = "A")
        val high = AlarmFormValidator.confirm(hour = 24, minute = 0, title = "A")
        assertEquals(AlarmConfirmResult.Accepted(AlarmDraft(hour = 0, minute = 0, title = "A")), low)
        assertEquals(AlarmConfirmResult.Accepted(AlarmDraft(hour = 23, minute = 0, title = "A")), high)
    }

    @Test
    fun minuteIsCoercedIntoHourRange() {
        val low = AlarmFormValidator.confirm(hour = 7, minute = -1, title = "A")
        val high = AlarmFormValidator.confirm(hour = 7, minute = 60, title = "A")
        assertEquals(AlarmConfirmResult.Accepted(AlarmDraft(hour = 7, minute = 0, title = "A")), low)
        assertEquals(AlarmConfirmResult.Accepted(AlarmDraft(hour = 7, minute = 59, title = "A")), high)
    }
}
