package com.remainder.app.alarm

import java.time.LocalDate
import java.time.LocalDateTime
import org.junit.Assert.assertEquals
import org.junit.Test

class AlarmOccurrenceResolverTest {

    private val today = LocalDate.of(2026, 9, 9)

    @Test
    fun selectedFutureTimeMeansToday() {
        val occurrence = AlarmOccurrenceResolver.resolve(
            draft = AlarmDraft(hour = 14, minute = 45, title = "Standup"),
            now = LocalDateTime.of(today, java.time.LocalTime.of(13, 15))
        )

        assertEquals(today, occurrence.date)
        assertEquals(14, occurrence.hour)
        assertEquals(45, occurrence.minute)
    }

    @Test
    fun selectedPastTimeMeansTomorrow() {
        val occurrence = AlarmOccurrenceResolver.resolve(
            draft = AlarmDraft(hour = 8, minute = 0, title = "Breakfast"),
            now = LocalDateTime.of(today, java.time.LocalTime.of(9, 30))
        )

        assertEquals(today.plusDays(1), occurrence.date)
    }

    @Test
    fun selectedCurrentMinuteMeansTomorrow() {
        val occurrence = AlarmOccurrenceResolver.resolve(
            draft = AlarmDraft(hour = 9, minute = 30, title = "Now"),
            now = LocalDateTime.of(today, java.time.LocalTime.of(9, 30))
        )

        assertEquals(today.plusDays(1), occurrence.date)
    }
}
