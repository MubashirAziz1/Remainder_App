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
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.remainder.app.notification.LockScreenEnableResult
import com.remainder.app.notification.LockScreenNotificationCoordinator
import com.remainder.app.notification.NotificationPermissionPolicy
import com.remainder.app.ui.navigation.RemainderNavHost

@Composable
fun RemainderApp(coordinator: LockScreenNotificationCoordinator) {
    var enabled by remember { mutableStateOf(coordinator.isUserEnabled()) }
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            coordinator.onPermissionGranted()
        } else {
            coordinator.onPermissionDenied()
        }
        enabled = coordinator.isUserEnabled()
    }
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                coordinator.sync()
                enabled = coordinator.isUserEnabled()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }
    RemainderNavHost(
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
            enabled = coordinator.isUserEnabled()
        }
    )
}
