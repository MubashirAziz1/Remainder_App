package com.remainder.app.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager

object LockScreenNotificationChannel {
    fun ensureCreated(notificationManager: NotificationManager) {
        val channel = NotificationChannel(
            LockScreenNotificationSpec.CHANNEL_ID,
            LockScreenNotificationSpec.CHANNEL_NAME,
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Shows Remainder on the lock screen"
            lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            setShowBadge(false)
        }
        notificationManager.createNotificationChannel(channel)
    }
}
