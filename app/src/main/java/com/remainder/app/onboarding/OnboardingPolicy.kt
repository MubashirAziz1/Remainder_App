package com.remainder.app.onboarding

import com.remainder.app.notification.NotificationPermissionPolicy

object OnboardingPolicy {
    fun needsRuntimeNotificationRequest(sdkInt: Int, permissionGranted: Boolean): Boolean {
        return NotificationPermissionPolicy.requiresRuntimePermission(sdkInt) && !permissionGranted
    }
}
