package com.remainder.app.notification

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat

object NotificationPermissionPolicy {
    fun requiresRuntimePermission(sdkInt: Int = Build.VERSION.SDK_INT): Boolean {
        return sdkInt >= Build.VERSION_CODES.TIRAMISU
    }

    fun isGranted(context: Context): Boolean {
        if (!requiresRuntimePermission()) {
            return true
        }
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    }
}
