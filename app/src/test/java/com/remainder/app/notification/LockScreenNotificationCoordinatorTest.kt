package com.remainder.app.notification

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class LockScreenNotificationCoordinatorTest {

    @Test
    fun enablingWithoutPermissionAsksAndDoesNotShow() {
        val notifications = FakeNotifier()
        val preferences = FakeStore()
        val coordinator = coordinator(preferences, granted = false, notifications)

        val result = coordinator.setEnabled(true)

        assertEquals(LockScreenEnableResult.NeedsPermission, result)
        assertFalse(preferences.isEnabled())
        assertFalse(notifications.showing)
    }

    @Test
    fun enablingWithPermissionShowsAndPersists() {
        val notifications = FakeNotifier()
        val preferences = FakeStore()
        val coordinator = coordinator(preferences, granted = true, notifications)

        val result = coordinator.setEnabled(true)

        assertEquals(LockScreenEnableResult.Shown, result)
        assertTrue(preferences.isEnabled())
        assertTrue(notifications.showing)
    }

    @Test
    fun disablingHidesAndPersists() {
        val notifications = FakeNotifier()
        val preferences = FakeStore(enabled = true)
        notifications.showing = true
        val coordinator = coordinator(preferences, granted = true, notifications)

        val result = coordinator.setEnabled(false)

        assertEquals(LockScreenEnableResult.Hidden, result)
        assertFalse(preferences.isEnabled())
        assertFalse(notifications.showing)
    }

    @Test
    fun syncShowsWhenEnabledAndGranted() {
        val notifications = FakeNotifier()
        val preferences = FakeStore(enabled = true)
        coordinator(preferences, granted = true, notifications).sync()
        assertTrue(notifications.showing)
        assertTrue(notifications.channelEnsured)
    }

    @Test
    fun syncHidesWhenEnabledButPermissionMissing() {
        val notifications = FakeNotifier()
        notifications.showing = true
        val preferences = FakeStore(enabled = true)
        coordinator(preferences, granted = false, notifications).sync()
        assertFalse(notifications.showing)
        assertTrue(notifications.channelEnsured)
    }

    @Test
    fun syncHidesWhenDisabled() {
        val notifications = FakeNotifier()
        notifications.showing = true
        val preferences = FakeStore(enabled = false)
        coordinator(preferences, granted = true, notifications).sync()
        assertFalse(notifications.showing)
    }

    @Test
    fun permissionGrantedShowsAndPersists() {
        val notifications = FakeNotifier()
        val preferences = FakeStore()
        coordinator(preferences, granted = true, notifications).onPermissionGranted()
        assertTrue(preferences.isEnabled())
        assertTrue(notifications.showing)
    }

    @Test
    fun permissionDeniedHidesAndDisables() {
        val notifications = FakeNotifier()
        notifications.showing = true
        val preferences = FakeStore(enabled = true)
        coordinator(preferences, granted = false, notifications).onPermissionDenied()
        assertFalse(preferences.isEnabled())
        assertFalse(notifications.showing)
    }

    @Test
    fun isUserEnabledReadsStore() {
        val preferences = FakeStore(enabled = true)
        assertTrue(coordinator(preferences, granted = true, FakeNotifier()).isUserEnabled())
    }

    @Test
    fun isUserEnabledIsFalseWhenPermissionMissing() {
        val preferences = FakeStore(enabled = true)
        assertFalse(coordinator(preferences, granted = false, FakeNotifier()).isUserEnabled())
    }

    private fun coordinator(
        preferences: LockScreenReminderStore,
        granted: Boolean,
        notifications: LockScreenNotifier
    ) = LockScreenNotificationCoordinator(
        preferences = preferences,
        permission = NotificationPermissionChecker { granted },
        notifications = notifications
    )

    private class FakeStore(private var enabled: Boolean = false) : LockScreenReminderStore {
        override fun isEnabled(): Boolean = enabled
        override fun setEnabled(enabled: Boolean) {
            this.enabled = enabled
        }
    }

    private class FakeNotifier : LockScreenNotifier {
        var showing: Boolean = false
        var channelEnsured: Boolean = false

        override fun show() {
            showing = true
        }

        override fun hide() {
            showing = false
        }

        override fun ensureChannel() {
            channelEnsured = true
        }
    }
}
