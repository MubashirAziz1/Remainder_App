package com.remainder.app.notification

import android.app.Application
import android.app.NotificationManager
import android.content.Context
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class LockScreenNotificationCoordinatorTest {

    private lateinit var context: Application
    private lateinit var notificationManager: NotificationManager
    private lateinit var preferences: LockScreenNotificationPreferences
    private lateinit var controller: LockScreenNotificationController

    @Before
    fun setUp() {
        context = RuntimeEnvironment.getApplication()
        context.getSharedPreferences("remainder_notifications", Context.MODE_PRIVATE).edit().clear().commit()
        notificationManager = context.getSystemService(NotificationManager::class.java)
        preferences = LockScreenNotificationPreferences(context)
        controller = LockScreenNotificationController(context)
    }

    @Test
    fun enablingWithoutPermissionAsksAndDoesNotShow() {
        val coordinator = coordinator(granted = false)

        val result = coordinator.setEnabled(true)

        assertEquals(LockScreenEnableResult.NeedsPermission, result)
        assertFalse(preferences.isEnabled())
        assertFalse(isShowing())
    }

    @Test
    fun enablingWithPermissionShowsAndPersists() {
        val coordinator = coordinator(granted = true)

        val result = coordinator.setEnabled(true)

        assertEquals(LockScreenEnableResult.Shown, result)
        assertTrue(preferences.isEnabled())
        assertTrue(isShowing())
    }

    @Test
    fun disablingHidesAndPersists() {
        val coordinator = coordinator(granted = true)
        coordinator.setEnabled(true)

        val result = coordinator.setEnabled(false)

        assertEquals(LockScreenEnableResult.Hidden, result)
        assertFalse(preferences.isEnabled())
        assertFalse(isShowing())
    }

    @Test
    fun syncShowsWhenEnabledAndGranted() {
        preferences.setEnabled(true)
        coordinator(granted = true).sync()
        assertTrue(isShowing())
    }

    @Test
    fun syncHidesWhenEnabledButPermissionMissing() {
        preferences.setEnabled(true)
        coordinator(granted = false).sync()
        assertFalse(isShowing())
    }

    @Test
    fun syncHidesWhenDisabled() {
        preferences.setEnabled(false)
        coordinator(granted = true).sync()
        assertFalse(isShowing())
    }

    @Test
    fun permissionGrantedShowsAndPersists() {
        val coordinator = coordinator(granted = true)
        coordinator.onPermissionGranted()
        assertTrue(preferences.isEnabled())
        assertTrue(isShowing())
    }

    @Test
    fun permissionDeniedHidesAndDisables() {
        preferences.setEnabled(true)
        controller.show()
        val coordinator = coordinator(granted = false)
        coordinator.onPermissionDenied()
        assertFalse(preferences.isEnabled())
        assertFalse(isShowing())
    }

    private fun coordinator(granted: Boolean) = LockScreenNotificationCoordinator(
        preferences = preferences,
        permission = NotificationPermissionChecker { granted },
        notifications = controller
    )

    private fun isShowing(): Boolean =
        notificationManager.activeNotifications.any { it.id == LockScreenNotificationSpec.NOTIFICATION_ID }
}
