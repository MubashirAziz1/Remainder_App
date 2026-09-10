package com.remainder.app.alarm

import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Test

class AlarmConfirmationTest {

    private val today = LocalDate.of(2026, 9, 10)

    @Test
    fun formatsTimeAsZeroPadded24Hour() {
        val confirmation = AlarmConfirmationFormatter.format(
            occurrence = AlarmOccurrence(
                date = today,
                hour = 7,
                minute = 5,
                title = "Medicine"
            ),
            today = today
        )

        assertEquals("07:05", confirmation.timeText)
    }

    @Test
    fun occurrenceOnTodayIsLabeledToday() {
        val confirmation = AlarmConfirmationFormatter.format(
            occurrence = AlarmOccurrence(
                date = today,
                hour = 14,
                minute = 45,
                title = "Standup"
            ),
            today = today
        )

        assertEquals(AlarmConfirmationFormatter.TODAY, confirmation.dayText)
    }

    @Test
    fun occurrenceOnNextDayIsLabeledTomorrow() {
        val confirmation = AlarmConfirmationFormatter.format(
            occurrence = AlarmOccurrence(
                date = today.plusDays(1),
                hour = 8,
                minute = 0,
                title = "Breakfast"
            ),
            today = today
        )

        assertEquals(AlarmConfirmationFormatter.TOMORROW, confirmation.dayText)
    }

    @Test
    fun titleIsCarriedThrough() {
        val confirmation = AlarmConfirmationFormatter.format(
            occurrence = AlarmOccurrence(
                date = today,
                hour = 23,
                minute = 59,
                title = "Call Mom"
            ),
            today = today
        )

        assertEquals("Call Mom", confirmation.title)
    }
}
