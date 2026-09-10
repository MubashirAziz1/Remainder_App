package com.remainder.app.alarm

import android.content.Intent
import android.provider.AlarmClock

object AlarmIntentFactory {
    fun create(draft: AlarmDraft): Intent {
        return Intent(AlarmClock.ACTION_SET_ALARM).apply {
            putExtra(AlarmClock.EXTRA_HOUR, draft.hour)
            putExtra(AlarmClock.EXTRA_MINUTES, draft.minute)
            putExtra(AlarmClock.EXTRA_MESSAGE, draft.title)
            putExtra(AlarmClock.EXTRA_SKIP_UI, true)
        }
    }
}
