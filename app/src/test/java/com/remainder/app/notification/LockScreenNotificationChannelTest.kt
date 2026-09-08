package com.remainder.app.notification

import android.app.Notification
import android.app.NotificationManager
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class LockScreenNotificationChannelTest {

    private lateinit var notificationManager: NotificationManager

    @Before
    fun setUp() {
        notificationManager = RuntimeEnvironment.getApplication()
            .getSystemService(NotificationManager::class.java)
    }

    @Test
    fun createsPublicLockScreenChannel() {
        LockScreenNotificationChannel.ensureCreated(notificationManager)

        val channel = notificationManager.getNotificationChannel(LockScreenNotificationSpec.CHANNEL_ID)
        assertNotNull(channel)
        assertEquals(LockScreenNotificationSpec.CHANNEL_NAME, channel.name)
        assertEquals(NotificationManager.IMPORTANCE_DEFAULT, channel.importance)
        assertEquals(Notification.VISIBILITY_PUBLIC, channel.lockscreenVisibility)
    }

    @Test
    fun creatingTwiceDoesNotChangeId() {
        LockScreenNotificationChannel.ensureCreated(notificationManager)
        LockScreenNotificationChannel.ensureCreated(notificationManager)

        val channel = notificationManager.getNotificationChannel(LockScreenNotificationSpec.CHANNEL_ID)
        assertEquals(LockScreenNotificationSpec.CHANNEL_ID, channel.id)
    }
}
