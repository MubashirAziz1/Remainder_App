package com.remainder.app.onboarding

import android.provider.Settings

data class SystemSettingsIntentSpec(
    val action: String,
    val packageName: String? = null,
    val channelId: String? = null,
    val usePackageUri: Boolean = false
)

object SystemSettingsIntentFactory {
    fun appNotificationSettings(packageName: String): SystemSettingsIntentSpec {
        return SystemSettingsIntentSpec(
            action = Settings.ACTION_APP_NOTIFICATION_SETTINGS,
            packageName = packageName
        )
    }

    fun channelNotificationSettings(packageName: String, channelId: String): SystemSettingsIntentSpec {
        return SystemSettingsIntentSpec(
            action = Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS,
            packageName = packageName,
            channelId = channelId
        )
    }

    fun requestIgnoreBatteryOptimizations(packageName: String): SystemSettingsIntentSpec {
        return SystemSettingsIntentSpec(
            action = Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS,
            packageName = packageName,
            usePackageUri = true
        )
    }

    fun batteryOptimizationSettings(): SystemSettingsIntentSpec {
        return SystemSettingsIntentSpec(
            action = Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS
        )
    }

    fun appDetails(packageName: String): SystemSettingsIntentSpec {
        return SystemSettingsIntentSpec(
            action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            packageName = packageName,
            usePackageUri = true
        )
    }
}
