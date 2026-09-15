package com.remainder.app.notification

fun interface NotificationPermissionChecker {
    fun isGranted(): Boolean
}

/**
 * Executes lock-screen notification side-effects and reads the runtime notification
 * permission. It no longer owns the "enabled" preference — that durable state lives in
 * [com.remainder.app.data.RemainderPreferences] and is orchestrated by
 * [com.remainder.app.ui.RemainderViewModel], which tells the coordinator when to
 * show/hide via [sync].
 */
class LockScreenNotificationCoordinator(
    private val permission: NotificationPermissionChecker,
    private val notifications: LockScreenNotifier
) {
    fun hasPermission(): Boolean = permission.isGranted()

    fun ensureChannel() {
        notifications.ensureChannel()
    }

    fun show() {
        notifications.ensureChannel()
        notifications.show()
    }

    fun hide() {
        notifications.hide()
    }

    fun sync(desiredEnabled: Boolean) {
        notifications.ensureChannel()
        if (desiredEnabled && permission.isGranted()) {
            notifications.show()
        } else {
            notifications.hide()
        }
    }
}
