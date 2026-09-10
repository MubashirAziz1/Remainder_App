package com.remainder.app.ui.alarm

import com.remainder.app.alarm.AlarmDraft
import com.remainder.app.alarm.AlarmOccurrence
import com.remainder.app.alarm.AlarmScheduleError
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class AlarmUiStateSaverTest {

    @Test
    fun editingRoundTrips() {
        val restored = AlarmUiStateSaver.restore(
            AlarmUiStateSaver.save(AlarmUiState.Editing)
        )

        assertEquals(AlarmUiState.Editing, restored)
    }

    @Test
    fun failedRoundTripsPreservingDraftAndError() {
        val state = AlarmUiState.Failed(
            draft = AlarmDraft(hour = 7, minute = 30, title = "Gym | Morning"),
            error = AlarmScheduleError.ClockLaunchFailed
        )

        val restored = AlarmUiStateSaver.restore(AlarmUiStateSaver.save(state))

        assertEquals(state, restored)
    }

    @Test
    fun scheduledRoundTripsPreservingOccurrence() {
        val state = AlarmUiState.Scheduled(
            occurrence = AlarmOccurrence(
                date = LocalDate.of(2026, 9, 11),
                hour = 8,
                minute = 5,
                title = "Breakfast"
            )
        )

        val restored = AlarmUiStateSaver.restore(AlarmUiStateSaver.save(state))

        assertEquals(state, restored)
    }

    @Test
    fun corruptSavedStateRestoresToNull() {
        assertNull(AlarmUiStateSaver.restore(emptyList()))
        assertNull(AlarmUiStateSaver.restore(listOf("unknown")))
        assertNull(AlarmUiStateSaver.restore(listOf("failed", "not-an-int")))
        assertNull(AlarmUiStateSaver.restore(listOf("failed", 7, 30, "Gym", "NOT_AN_ERROR")))
        assertNull(AlarmUiStateSaver.restore(listOf("scheduled", "not-a-date", 8, 5, "Breakfast")))
    }
}
