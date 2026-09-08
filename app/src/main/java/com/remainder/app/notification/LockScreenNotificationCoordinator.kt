package com.remainder.app.notification

import android.content.Context

fun interface NotificationPermissionChecker {
    fun isGranted(): Boolean
}

sealed interface LockScreenEnableResult {
    data object Shown : LockScreenEnableResult
    data object Hidden : LockScreenEnableResult
    data object NeedsPermission : LockScreenEnableResult
}

class LockScreenNotificationCoordinator(
    private val preferences: LockScreenNotificationPreferences,
    private val permission: NotificationPermissionChecker,
    private val notifications: LockScreenNotificationController
) {
    fun isUserEnabled(): Boolean = preferences.isEnabled()

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

    companion object {
        fun create(context: Context): LockScreenNotificationCoordinator {
            val appContext = context.applicationContext
            return LockScreenNotificationCoordinator(
                preferences = LockScreenNotificationPreferences(appContext),
                permission = NotificationPermissionChecker {
                    NotificationPermissionPolicy.isGranted(appContext)
                },
                notifications = LockScreenNotificationController(appContext)
            )
        }
    }
}
