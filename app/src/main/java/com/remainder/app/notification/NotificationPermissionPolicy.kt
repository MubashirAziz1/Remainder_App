package com.remainder.app.notification

import android.os.Build

object NotificationPermissionPolicy {
    fun requiresRuntimePermission(sdkInt: Int): Boolean {
        return sdkInt >= Build.VERSION_CODES.TIRAMISU
    }

    fun hasNotificationAccess(sdkInt: Int, runtimePermissionGranted: Boolean): Boolean {
        return !requiresRuntimePermission(sdkInt) || runtimePermissionGranted
    }
}
