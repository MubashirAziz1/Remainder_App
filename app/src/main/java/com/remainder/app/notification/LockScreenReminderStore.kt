package com.remainder.app.notification

interface LockScreenReminderStore {
    fun isEnabled(): Boolean
    fun setEnabled(enabled: Boolean)
}
