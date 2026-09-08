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

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class LockScreenNotificationControllerTest {

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
    fun showPostsOngoingPublicNotification() {
        controller.show()

        val posted = postedNotification()
        assertTrue(posted.flags and Notification.FLAG_ONGOING_EVENT != 0)
        assertEquals(Notification.VISIBILITY_PUBLIC, posted.visibility)
        assertEquals(LockScreenNotificationSpec.CONTENT_TITLE, posted.extras.getString(Notification.EXTRA_TITLE))
        assertEquals(LockScreenNotificationSpec.CONTENT_TEXT, posted.extras.getString(Notification.EXTRA_TEXT))
    }

    @Test
    fun showAttachesTapIntentToLockScreenAlarmActivity() {
        controller.show()

        val contentIntent = postedNotification().contentIntent
        assertNotNull(contentIntent)
        val launched = shadowOf(contentIntent).savedIntent
        assertEquals(LockScreenAlarmActivity::class.java.name, launched.component?.className)
    }

    @Test
    fun showCreatesChannelIfMissing() {
        controller.show()

        assertNotNull(
            notificationManager.getNotificationChannel(LockScreenNotificationSpec.CHANNEL_ID)
        )
    }

    @Test
    fun hideCancelsPostedNotification() {
        controller.show()
        controller.hide()

        assertTrue(notificationManager.activeNotifications.none { it.id == LockScreenNotificationSpec.NOTIFICATION_ID })
    }

    @Test
    fun hideWhenNothingPostedDoesNotThrow() {
        controller.hide()
    }

    private fun postedNotification(): Notification {
        val status = notificationManager.activeNotifications.single {
            it.id == LockScreenNotificationSpec.NOTIFICATION_ID
        }
        return status.notification
    }
}
