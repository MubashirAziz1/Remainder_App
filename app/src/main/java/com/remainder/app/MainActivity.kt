package com.remainder.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.remainder.app.data.RemainderPreferences
import com.remainder.app.notification.LockScreenNotificationController
import com.remainder.app.notification.LockScreenNotificationCoordinator
import com.remainder.app.notification.NotificationPermissionChecker
import com.remainder.app.notification.NotificationPermissionReader
import com.remainder.app.onboarding.ContextSystemSettingsLauncher
import com.remainder.app.ui.RemainderApp
import com.remainder.app.ui.RemainderViewModel
import com.remainder.app.ui.theme.RemainderTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val appContext = applicationContext
        val preferences = RemainderPreferences(appContext)
        val coordinator = LockScreenNotificationCoordinator(
            permission = NotificationPermissionChecker {
                NotificationPermissionReader.isGranted(appContext)
            },
            notifications = LockScreenNotificationController(appContext)
        )
        // Ensure the notification channel exists synchronously on cold start so the
        // channel is available before the async DataStore-driven reconcile runs.
        coordinator.ensureChannel()

        val viewModel = ViewModelProvider(this, RemainderViewModelFactory(
            preferences = preferences,
            coordinator = coordinator
        ))[RemainderViewModel::class.java]

        setContent {
            RemainderTheme {
                RemainderApp(
                    viewModel = viewModel,
                    lifecycle = lifecycle,
                    settingsLauncher = ContextSystemSettingsLauncher(appContext),
                    packageName = appContext.packageName,
                    showNotificationRationale = {
                        shouldShowRequestPermissionRationale(android.Manifest.permission.POST_NOTIFICATIONS)
                    }
                )
            }
        }
    }
}

private class RemainderViewModelFactory(
    private val preferences: RemainderPreferences,
    private val coordinator: LockScreenNotificationCoordinator
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = RemainderViewModel(
        preferences = preferences,
        coordinator = coordinator
    ) as T
}
