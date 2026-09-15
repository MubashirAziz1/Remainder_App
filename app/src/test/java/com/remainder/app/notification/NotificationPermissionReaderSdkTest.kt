package com.remainder.app.notification

import android.app.Application
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

/**
 * Cross-Android-version behavior of the runtime notification-permission reader.
 *
 * Android 13 (API 33, Tiramisu) introduced the POST_NOTIFICATIONS runtime
 * permission. Below that, notifications are allowed by default; from API 33 up
 * the app must hold the granted permission. These tests pin the reader's
 * default behavior at the boundary and on each supported SDK so a
 * version-specific regression in the gating logic is caught.
 *
 * The granted-path logic (`hasNotificationAccess(sdk, true)`) is covered
 * exhaustively by [NotificationPermissionPolicyLogicTest]; the reader is a
 * thin gateway that delegates to that policy.
 */
@RunWith(RobolectricTestRunner::class)
class NotificationPermissionReaderSdkTest {

    @Test
    @Config(sdk = [27])
    fun preTiramisuReportsAccessWithoutRuntimePermission() {
        assertTrue(NotificationPermissionReader.isGranted(application()))
    }

    @Test
    @Config(sdk = [29])
    fun android10ReportsAccessWithoutRuntimePermission() {
        assertTrue(NotificationPermissionReader.isGranted(application()))
    }

    @Test
    @Config(sdk = [31])
    fun android12ReportsAccessWithoutRuntimePermission() {
        assertTrue(NotificationPermissionReader.isGranted(application()))
    }

    @Test
    @Config(sdk = [32])
    fun android12LReportsAccessWithoutRuntimePermission() {
        assertTrue(NotificationPermissionReader.isGranted(application()))
    }

    @Test
    @Config(sdk = [33])
    fun tiramisuDeniesAccessByDefault() {
        assertFalse(NotificationPermissionReader.isGranted(application()))
    }

    @Test
    @Config(sdk = [34])
    fun upsideDownCakeDeniesAccessByDefault() {
        assertFalse(NotificationPermissionReader.isGranted(application()))
    }

    @Test
    @Config(sdk = [35])
    fun vanillaIceCreamDeniesAccessByDefault() {
        assertFalse(NotificationPermissionReader.isGranted(application()))
    }

    private fun application(): Application = RuntimeEnvironment.getApplication()
}
