package com.remainder.app.alarm

sealed class AlarmConfirmResult {
    data class Accepted(val draft: AlarmDraft) : AlarmConfirmResult()
    data class Rejected(val message: String) : AlarmConfirmResult()
}

object AlarmFormValidator {
    const val TITLE_REQUIRED = "Title is required"

    fun confirm(hour: Int, minute: Int, title: String): AlarmConfirmResult {
        val trimmed = title.trim()
        if (trimmed.isEmpty()) {
            return AlarmConfirmResult.Rejected(TITLE_REQUIRED)
        }
        return AlarmConfirmResult.Accepted(
            AlarmDraft(
                hour = hour.coerceIn(0, 23),
                minute = minute.coerceIn(0, 59),
                title = trimmed
            )
        )
    }
}
