package com.remainder.app.alarm

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

data class AlarmOccurrence(
    val date: LocalDate,
    val hour: Int,
    val minute: Int,
    val title: String
)

object AlarmOccurrenceResolver {
    fun resolve(draft: AlarmDraft, now: LocalDateTime): AlarmOccurrence {
        val selectedTime = LocalTime.of(draft.hour, draft.minute)
        val date = if (selectedTime.isAfter(now.toLocalTime())) {
            now.toLocalDate()
        } else {
            now.toLocalDate().plusDays(1)
        }
        return AlarmOccurrence(
            date = date,
            hour = draft.hour,
            minute = draft.minute,
            title = draft.title
        )
    }
}
