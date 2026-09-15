package com.remainder.app.notification

import android.app.Application
import android.app.Notification
import android.app.NotificationManager
import com.remainder.app.LockScreenAlarmActivity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config

/**
 * Cross-Android-version compatibility for the persistent lock-screen notification.
 *
 * Robolectric runs every test once per SDK listed in [Config.sdk], so these
 * assertions verify the notification contract (channel, public lock-screen
 * visibility, ongoing flag, tap target, idempotency) is invariant from the
 * project minSdk (API 27) through API 35.
 *
 * API 36 (Android 16) is not exercised here because Robolectric 4.14.1 does
 * not ship an Android 16 runtime; the manifest and permission-logic tests
 * cover that floor independently of a running runtime.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [27, 29, 31, 32, 33, 34, 35])
class LockScreenNotificationControllerCompatibilityTest {

    private lateinit var context: Application
    private lateinit var notificationManager: NotificationManager
    private lateinit var controller: LockScreenNotificationController

    @Before
    fun setUp() {
        context = RuntimeEnvironment.getApplication()
        notificationManager = context.getSystemService(NotificationManager::class.java)
        controller = LockScreenNotificationController(context)
    }

    @Test
    fun showCreatesLockScreenReminderChannelOnEverySupportedSdk() {
        controller.show()

        val channel = notificationManager.getNotificationChannel(LockScreenNotificationSpec.CHANNEL_ID)
        assertNotNull("Channel must exist on every supported SDK", channel)
        assertEquals(LockScreenNotificationSpec.CHANNEL_NAME, channel.name.toString())
        assertEquals(
            "Channel must be public so the reminder is visible over the keyguard",
            Notification.VISIBILITY_PUBLIC,
            channel.lockscreenVisibility
        )
    }

    @Test
    fun showPostsOngoingNotificationOnEverySupportedSdk() {
        controller.show()

        assertTrue(
            "Notification must be ongoing so OEMs keep it pinned",
            postedNotification().flags and Notification.FLAG_ONGOING_EVENT != 0
        )
    }

    @Test
    fun showPostsPublicVisibilityOnEverySupportedSdk() {
        controller.show()

        assertEquals(
            "Public visibility is required for the reminder to show over the keyguard",
            Notification.VISIBILITY_PUBLIC,
            postedNotification().visibility
        )
    }

    @Test
    fun showAttachesTapIntentToLockScreenAlarmActivityOnEverySupportedSdk() {
        controller.show()

        val contentIntent = postedNotification().contentIntent
        assertNotNull(contentIntent)
        val launched = shadowOf(contentIntent).savedIntent
        assertEquals(LockScreenAlarmActivity::class.java.name, launched.component?.className)
    }

    @Test
    fun hideRemovesNotificationOnEverySupportedSdk() {
        controller.show()
        controller.hide()

        assertTrue(
            notificationManager.activeNotifications.none {
                it.id == LockScreenNotificationSpec.NOTIFICATION_ID
            }
        )
    }

    @Test
    fun showIsIdempotentAcrossEverySupportedSdk() {
        controller.show()
        controller.show()

        assertEquals(
            1,
            notificationManager.activeNotifications.count {
                it.id == LockScreenNotificationSpec.NOTIFICATION_ID
            }
        )
    }

    private fun postedNotification(): Notification {
        val status = notificationManager.activeNotifications.single {
            it.id == LockScreenNotificationSpec.NOTIFICATION_ID
        }
        return status.notification
    }
}
