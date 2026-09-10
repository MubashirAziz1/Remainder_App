package com.remainder.app.ui.alarm

import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.SaverScope
import com.remainder.app.alarm.AlarmDraft
import com.remainder.app.alarm.AlarmOccurrence
import com.remainder.app.alarm.AlarmScheduleError
import java.time.LocalDate

sealed interface AlarmUiState {
    data object Editing : AlarmUiState
    data class Failed(val draft: AlarmDraft, val error: AlarmScheduleError) : AlarmUiState
    data class Scheduled(val occurrence: AlarmOccurrence) : AlarmUiState
}

object AlarmUiStateSaver : Saver<AlarmUiState, List<Any?>> {
    private const val TYPE_EDITING = "editing"
    private const val TYPE_FAILED = "failed"
    private const val TYPE_SCHEDULED = "scheduled"

    override fun SaverScope.save(value: AlarmUiState): List<Any?> {
        return when (value) {
            is AlarmUiState.Editing -> listOf(TYPE_EDITING)
            is AlarmUiState.Failed -> listOf(
                TYPE_FAILED,
                value.draft.hour,
                value.draft.minute,
                value.draft.title,
                value.error.name
            )
            is AlarmUiState.Scheduled -> listOf(
                TYPE_SCHEDULED,
                value.occurrence.date.toString(),
                value.occurrence.hour,
                value.occurrence.minute,
                value.occurrence.title
            )
        }
    }

    override fun restore(value: List<Any?>): AlarmUiState? {
        return when (value.firstOrNull()) {
            TYPE_EDITING -> AlarmUiState.Editing
            TYPE_FAILED -> restoreFailed(value)
            TYPE_SCHEDULED -> restoreScheduled(value)
            else -> null
        }
    }

    private fun restoreFailed(value: List<Any?>): AlarmUiState? {
        val hour = value.getOrNull(1) as? Int ?: return null
        val minute = value.getOrNull(2) as? Int ?: return null
        val title = value.getOrNull(3) as? String ?: return null
        val errorName = value.getOrNull(4) as? String ?: return null
        val error = AlarmScheduleError.entries.firstOrNull { it.name == errorName }
            ?: return null
        return AlarmUiState.Failed(AlarmDraft(hour, minute, title), error)
    }

    private fun restoreScheduled(value: List<Any?>): AlarmUiState? {
        val dateText = value.getOrNull(1) as? String ?: return null
        val hour = value.getOrNull(2) as? Int ?: return null
        val minute = value.getOrNull(3) as? Int ?: return null
        val title = value.getOrNull(4) as? String ?: return null
        val date = runCatching { LocalDate.parse(dateText) }.getOrNull() ?: return null
        return AlarmUiState.Scheduled(AlarmOccurrence(date, hour, minute, title))
    }
}
