package com.remainder.app.alarm

import java.time.LocalDate

data class AlarmConfirmation(
    val timeText: String,
    val dayText: String,
    val title: String
)

object AlarmConfirmationFormatter {
    const val TODAY = "Today"
    const val TOMORROW = "Tomorrow"

    fun format(occurrence: AlarmOccurrence, today: LocalDate): AlarmConfirmation {
        return AlarmConfirmation(
            timeText = formatTime(occurrence.hour, occurrence.minute),
            dayText = if (occurrence.date == today) TODAY else TOMORROW,
            title = occurrence.title
        )
    }

    private fun formatTime(hour: Int, minute: Int): String {
        return "%02d:%02d".format(hour, minute)
    }
}
