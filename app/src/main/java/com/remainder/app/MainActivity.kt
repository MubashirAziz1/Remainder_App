package com.remainder.app

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.remainder.app.notification.LockScreenNotificationController
import com.remainder.app.notification.LockScreenNotificationCoordinator
import com.remainder.app.notification.LockScreenNotificationPreferences
import com.remainder.app.notification.NotificationPermissionChecker
import com.remainder.app.notification.NotificationPermissionReader
import com.remainder.app.onboarding.ContextSystemSettingsLauncher
import com.remainder.app.onboarding.OnboardingPreferences
import com.remainder.app.ui.RemainderApp
import com.remainder.app.ui.theme.RemainderTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val appContext = applicationContext
        val coordinator = LockScreenNotificationCoordinator(
            preferences = LockScreenNotificationPreferences(appContext),
            permission = NotificationPermissionChecker {
                NotificationPermissionReader.isGranted(appContext)
            },
            notifications = LockScreenNotificationController(appContext)
        )
        coordinator.sync()
        setContent {
            RemainderTheme {
                RemainderApp(
                    coordinator = coordinator,
                    lifecycle = lifecycle,
                    onboardingStore = OnboardingPreferences(appContext),
                    settingsLauncher = ContextSystemSettingsLauncher(appContext),
                    packageName = appContext.packageName,
                    showNotificationRationale = {
                        shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)
                    }
                )
            }
        }
    }
}
