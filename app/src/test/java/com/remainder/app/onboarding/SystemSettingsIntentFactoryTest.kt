package com.remainder.app.onboarding

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SystemSettingsIntentFactoryTest {

    @Test
    fun appNotificationSettingsTargetsAppPackage() {
        val spec = SystemSettingsIntentFactory.appNotificationSettings("com.remainder.app")

        assertEquals("android.settings.APP_NOTIFICATION_SETTINGS", spec.action)
        assertEquals("com.remainder.app", spec.packageName)
        assertFalse(spec.usePackageUri)
    }

    @Test
    fun channelNotificationSettingsTargetsChannelAndPackage() {
        val spec = SystemSettingsIntentFactory.channelNotificationSettings(
            "com.remainder.app",
            "lock_screen_reminder"
        )

        assertEquals("android.settings.CHANNEL_NOTIFICATION_SETTINGS", spec.action)
        assertEquals("com.remainder.app", spec.packageName)
        assertEquals("lock_screen_reminder", spec.channelId)
        assertFalse(spec.usePackageUri)
    }

    @Test
    fun requestIgnoreBatteryOptimizationsUsesPackageUri() {
        val spec = SystemSettingsIntentFactory.requestIgnoreBatteryOptimizations("com.remainder.app")

        assertEquals("android.settings.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS", spec.action)
        assertEquals("com.remainder.app", spec.packageName)
        assertTrue(spec.usePackageUri)
    }

    @Test
    fun batteryOptimizationSettingsOpensSystemList() {
        val spec = SystemSettingsIntentFactory.batteryOptimizationSettings()

        assertEquals("android.settings.IGNORE_BATTERY_OPTIMIZATION_SETTINGS", spec.action)
        assertNull(spec.packageName)
        assertFalse(spec.usePackageUri)
    }

    @Test
    fun appDetailsUsesPackageUri() {
        val spec = SystemSettingsIntentFactory.appDetails("com.remainder.app")

        assertEquals("android.settings.APPLICATION_DETAILS_SETTINGS", spec.action)
        assertEquals("com.remainder.app", spec.packageName)
        assertTrue(spec.usePackageUri)
    }
}
