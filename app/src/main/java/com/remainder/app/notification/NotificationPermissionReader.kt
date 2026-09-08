package com.remainder.app.notification

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat

object NotificationPermissionReader {
    fun isGranted(context: Context): Boolean {
        val runtimeGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
        return NotificationPermissionPolicy.hasNotificationAccess(
            Build.VERSION.SDK_INT,
            runtimeGranted
        )
    }
}
