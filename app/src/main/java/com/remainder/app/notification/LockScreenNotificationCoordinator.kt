package com.remainder.app.notification

fun interface NotificationPermissionChecker {
    fun isGranted(): Boolean
}

sealed interface LockScreenEnableResult {
    data object Shown : LockScreenEnableResult
    data object Hidden : LockScreenEnableResult
    data object NeedsPermission : LockScreenEnableResult
}

class LockScreenNotificationCoordinator(
    private val preferences: LockScreenReminderStore,
    private val permission: NotificationPermissionChecker,
    private val notifications: LockScreenNotifier
) {
    fun isUserEnabled(): Boolean = preferences.isEnabled() && permission.isGranted()

    fun hasPermission(): Boolean = permission.isGranted()

    fun setEnabled(enabled: Boolean): LockScreenEnableResult {
        if (!enabled) {
            preferences.setEnabled(false)
            notifications.hide()
            return LockScreenEnableResult.Hidden
        }
        if (!permission.isGranted()) {
            return LockScreenEnableResult.NeedsPermission
        }
        preferences.setEnabled(true)
        notifications.show()
        return LockScreenEnableResult.Shown
    }

    fun onPermissionGranted() {
        preferences.setEnabled(true)
        notifications.show()
    }

    fun onPermissionDenied() {
        preferences.setEnabled(false)
        notifications.hide()
    }

    fun sync() {
        notifications.ensureChannel()
        if (preferences.isEnabled() && permission.isGranted()) {
            notifications.show()
        } else {
            notifications.hide()
        }
    }
}
