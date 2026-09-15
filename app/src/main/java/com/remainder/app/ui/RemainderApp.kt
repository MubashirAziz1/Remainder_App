package com.remainder.app.ui

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.remainder.app.notification.LockScreenEnableResult
import com.remainder.app.notification.LockScreenNotificationCoordinator
import com.remainder.app.onboarding.DeniedPermissionGuidance
import com.remainder.app.onboarding.OnboardingPolicy
import com.remainder.app.onboarding.OnboardingStore
import com.remainder.app.onboarding.SystemSettingsLauncher
import com.remainder.app.ui.navigation.RemainderNavHost

@Composable
fun RemainderApp(
    coordinator: LockScreenNotificationCoordinator,
    lifecycle: Lifecycle,
    onboardingStore: OnboardingStore,
    settingsLauncher: SystemSettingsLauncher,
    packageName: String,
    showNotificationRationale: () -> Boolean
) {
    var enabled by remember { mutableStateOf(coordinator.isUserEnabled()) }
    var onboardingCompleted by remember { mutableStateOf(onboardingStore.isCompleted()) }
    var denialObserved by rememberSaveable { mutableStateOf(false) }
    var permissionGranted by remember { mutableStateOf(coordinator.hasPermission()) }
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            coordinator.onPermissionGranted()
        } else {
            coordinator.onPermissionDenied()
            denialObserved = true
        }
        permissionGranted = coordinator.hasPermission()
        enabled = coordinator.isUserEnabled()
    }
    DisposableEffect(lifecycle) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                coordinator.sync()
                permissionGranted = coordinator.hasPermission()
                enabled = coordinator.isUserEnabled()
            }
        }
        lifecycle.addObserver(observer)
        onDispose { lifecycle.removeObserver(observer) }
    }
    RemainderNavHost(
        startOnboarding = !onboardingCompleted,
        onboardingPackageName = packageName,
        needsRuntimeNotificationRequest = OnboardingPolicy.needsRuntimeNotificationRequest(
            Build.VERSION.SDK_INT,
            permissionGranted
        ),
        notificationGuidance = DeniedPermissionGuidance.resolve(
            permissionGranted = permissionGranted,
            denialObserved = denialObserved,
            showRationale = showNotificationRationale()
        ),
        onRequestNotificationPermission = {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        },
        onOpenSystemSettings = { spec -> settingsLauncher.launch(spec) },
        onOnboardingCompleted = {
            onboardingStore.markCompleted()
            onboardingCompleted = true
        },
        lockScreenReminderEnabled = enabled,
        onLockScreenReminderChange = { wantEnabled ->
            when (coordinator.setEnabled(wantEnabled)) {
                LockScreenEnableResult.NeedsPermission -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                }
                LockScreenEnableResult.Shown,
                LockScreenEnableResult.Hidden -> Unit
            }
            permissionGranted = coordinator.hasPermission()
            enabled = coordinator.isUserEnabled()
        }
    )
}
