package com.remainder.app.notification

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class NotificationPermissionPolicyLogicTest {

    @Test
    fun android13RequiresRuntimePermission() {
        assertTrue(NotificationPermissionPolicy.requiresRuntimePermission(33))
    }

    @Test
    fun preTiramisuDoesNotRequireRuntimePermission() {
        assertFalse(NotificationPermissionPolicy.requiresRuntimePermission(27))
        assertFalse(NotificationPermissionPolicy.requiresRuntimePermission(32))
    }

    @Test
    fun preTiramisuHasAccessEvenWhenRuntimeDenied() {
        assertTrue(NotificationPermissionPolicy.hasNotificationAccess(27, runtimePermissionGranted = false))
    }

    @Test
    fun android13HasAccessOnlyWhenRuntimeGranted() {
        assertFalse(NotificationPermissionPolicy.hasNotificationAccess(33, runtimePermissionGranted = false))
        assertTrue(NotificationPermissionPolicy.hasNotificationAccess(33, runtimePermissionGranted = true))
    }
}
