package com.remainder.app.notification

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import androidx.core.app.NotificationCompat
import com.remainder.app.R

class LockScreenNotificationController(
    private val context: Context
) : LockScreenNotifier {
    private val notificationManager = context.getSystemService(NotificationManager::class.java)

    override fun show() {
        ensureChannel()
        val tapIntent = PendingIntent.getActivity(
            context,
            0,
            LockScreenTapIntent.create(context),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val notification = NotificationCompat.Builder(context, LockScreenNotificationSpec.CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_stat_lock_reminder)
            .setContentTitle(LockScreenNotificationSpec.CONTENT_TITLE)
            .setContentText(LockScreenNotificationSpec.CONTENT_TEXT)
            .setOngoing(true)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setContentIntent(tapIntent)
            .setOnlyAlertOnce(true)
            .setSilent(true)
            .setCategory(NotificationCompat.CATEGORY_STATUS)
            .build()
        notificationManager.notify(LockScreenNotificationSpec.NOTIFICATION_ID, notification)
    }

    override fun hide() {
        notificationManager.cancel(LockScreenNotificationSpec.NOTIFICATION_ID)
    }

    override fun ensureChannel() {
        LockScreenNotificationChannel.ensureCreated(notificationManager)
    }
}
