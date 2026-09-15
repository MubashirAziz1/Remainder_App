package com.remainder.app.onboarding

enum class NotificationGuidance {
    None,
    RetryRequest,
    OpenAppSettings
}

object DeniedPermissionGuidance {
    fun resolve(
        permissionGranted: Boolean,
        denialObserved: Boolean,
        showRationale: Boolean
    ): NotificationGuidance {
        if (permissionGranted || !denialObserved) {
            return NotificationGuidance.None
        }
        return if (showRationale) {
            NotificationGuidance.RetryRequest
        } else {
            NotificationGuidance.OpenAppSettings
        }
    }

    fun text(guidance: NotificationGuidance): String? {
        return when (guidance) {
            NotificationGuidance.None -> null
            NotificationGuidance.RetryRequest ->
                "Notifications are still off. Try the permission request again to receive reminders."
            NotificationGuidance.OpenAppSettings ->
                "Notifications are blocked for Remainder. Open Android app settings and enable notifications to receive reminders."
        }
    }
}
