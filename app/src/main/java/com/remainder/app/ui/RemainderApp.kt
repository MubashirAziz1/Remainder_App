package com.remainder.app.ui

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.remainder.app.onboarding.OnboardingPolicy
import com.remainder.app.onboarding.SystemSettingsLauncher
import com.remainder.app.onboarding.SystemSettingsIntentSpec
import com.remainder.app.ui.navigation.RemainderNavHost

@Composable
fun RemainderApp(
    viewModel: RemainderViewModel,
    lifecycle: Lifecycle,
    settingsLauncher: SystemSettingsLauncher,
    packageName: String,
    showNotificationRationale: () -> Boolean
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted -> viewModel.onPermissionResult(granted, showNotificationRationale()) }

    val requestNotificationPermission: () -> Unit = {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    DisposableEffect(lifecycle) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.refreshRationale(showNotificationRationale())
                viewModel.sync()
            }
        }
        lifecycle.addObserver(observer)
        onDispose { lifecycle.removeObserver(observer) }
    }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                RemainderEvent.RequestNotificationPermission -> requestNotificationPermission()
            }
        }
    }

    RemainderNavHost(
        startOnboarding = uiState.startOnboarding,
        onboardingPackageName = packageName,
        needsRuntimeNotificationRequest = OnboardingPolicy.needsRuntimeNotificationRequest(
            sdkInt = Build.VERSION.SDK_INT,
            permissionGranted = uiState.permissionGranted
        ),
        notificationGuidance = uiState.notificationGuidance,
        onRequestNotificationPermission = requestNotificationPermission,
        onOpenSystemSettings = { spec: SystemSettingsIntentSpec -> settingsLauncher.launch(spec) },
        onOnboardingCompleted = { viewModel.onOnboardingCompleted() },
        lockScreenReminderEnabled = uiState.lockScreenReminderEnabled,
        onLockScreenReminderChange = { wantEnabled -> viewModel.onLockScreenReminderChange(wantEnabled) }
    )
}
