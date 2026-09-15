package com.remainder.app.notification

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class LockScreenNotificationCoordinatorTest {

    @Test
    fun hasPermissionReflectsChecker() {
        assertTrue(coordinator(granted = true, FakeNotifier()).hasPermission())
        assertFalse(coordinator(granted = false, FakeNotifier()).hasPermission())
    }

    @Test
    fun showEnsuresChannelAndShows() {
        val notifier = FakeNotifier()
        coordinator(granted = true, notifier).show()

        assertTrue(notifier.channelEnsured)
        assertTrue(notifier.showing)
    }

    @Test
    fun hideHidesNotification() {
        val notifier = FakeNotifier()
        notifier.showing = true
        coordinator(granted = true, notifier).hide()

        assertFalse(notifier.showing)
    }

    @Test
    fun ensureChannelCreatesChannelWithoutShowing() {
        val notifier = FakeNotifier()
        coordinator(granted = true, notifier).ensureChannel()

        assertTrue(notifier.channelEnsured)
        assertFalse(notifier.showing)
    }

    @Test
    fun syncShowsWhenEnabledAndGranted() {
        val notifier = FakeNotifier()
        coordinator(granted = true, notifier).sync(desiredEnabled = true)

        assertTrue(notifier.channelEnsured)
        assertTrue(notifier.showing)
    }

    @Test
    fun syncHidesWhenEnabledButPermissionMissing() {
        val notifier = FakeNotifier()
        notifier.showing = true
        coordinator(granted = false, notifier).sync(desiredEnabled = true)

        assertTrue(notifier.channelEnsured)
        assertFalse(notifier.showing)
    }

    @Test
    fun syncHidesWhenDisabled() {
        val notifier = FakeNotifier()
        notifier.showing = true
        coordinator(granted = true, notifier).sync(desiredEnabled = false)

        assertFalse(notifier.showing)
    }

    private fun coordinator(granted: Boolean, notifications: LockScreenNotifier) =
        LockScreenNotificationCoordinator(
            permission = NotificationPermissionChecker { granted },
            notifications = notifications
        )

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
