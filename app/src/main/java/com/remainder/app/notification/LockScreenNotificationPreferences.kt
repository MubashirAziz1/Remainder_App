package com.remainder.app.notification

import android.content.Context

class LockScreenNotificationPreferences(context: Context) {
    private val prefs = context.applicationContext.getSharedPreferences(
        PREFS_NAME,
        Context.MODE_PRIVATE
    )

    fun isEnabled(): Boolean = prefs.getBoolean(KEY, false)

    fun setEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY, enabled).commit()
    }

    private companion object {
        const val PREFS_NAME = "remainder_notifications"
        const val KEY = "lock_screen_reminder_enabled"
    }
}
