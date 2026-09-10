package com.remainder.app.alarm

import android.provider.AlarmClock

data class AlarmIntentSpec(
    val action: String,
    val hour: Int,
    val minute: Int,
    val message: String,
    val skipUi: Boolean
)

object AlarmIntentFactory {
    fun create(draft: AlarmDraft): AlarmIntentSpec {
        return AlarmIntentSpec(
            action = AlarmClock.ACTION_SET_ALARM,
            hour = draft.hour,
            minute = draft.minute,
            message = draft.title,
            skipUi = true
        )
    }
}
