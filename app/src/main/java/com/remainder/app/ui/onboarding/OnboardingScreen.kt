package com.remainder.app.ui.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.remainder.app.notification.LockScreenNotificationSpec
import com.remainder.app.onboarding.DeniedPermissionGuidance
import com.remainder.app.onboarding.NotificationGuidance
import com.remainder.app.onboarding.OnboardingStep
import com.remainder.app.onboarding.SystemSettingsIntentFactory
import com.remainder.app.onboarding.SystemSettingsIntentSpec

@Composable
fun OnboardingScreen(
    packageName: String,
    needsRuntimeNotificationRequest: Boolean,
    notificationGuidance: NotificationGuidance,
    onRequestNotificationPermission: () -> Unit,
    onOpenSystemSettings: (SystemSettingsIntentSpec) -> Unit,
    onCompleted: () -> Unit,
    channelId: String = LockScreenNotificationSpec.CHANNEL_ID
) {
    var step by rememberSaveable { mutableStateOf(OnboardingStep.first) }

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = step.title,
                style = MaterialTheme.typography.displaySmall
            )
            Text(
                text = step.body,
                style = MaterialTheme.typography.bodyLarge
            )
            when (step) {
                OnboardingStep.Notifications -> NotificationStepActions(
                    packageName = packageName,
                    needsRuntimeNotificationRequest = needsRuntimeNotificationRequest,
                    notificationGuidance = notificationGuidance,
                    onRequestNotificationPermission = onRequestNotificationPermission,
                    onOpenSystemSettings = onOpenSystemSettings
                )
                OnboardingStep.LockScreen -> OutlinedButton(
                    onClick = {
                        onOpenSystemSettings(
                            SystemSettingsIntentFactory.channelNotificationSettings(packageName, channelId)
                        )
                    }
                ) {
                    Text("Open lock-screen settings")
                }
                OnboardingStep.BatteryOptimization -> BatteryStepActions(
                    packageName = packageName,
                    onOpenSystemSettings = onOpenSystemSettings
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                step.previous()?.let { previous ->
                    OutlinedButton(onClick = { step = previous }) {
                        Text("Back")
                    }
                }
                val next = step.next()
                if (next != null) {
                    Button(onClick = { step = next }) {
                        Text("Next")
                    }
                } else {
                    Button(onClick = onCompleted) {
                        Text("Done")
                    }
                }
            }
        }
    }
}

@Composable
private fun NotificationStepActions(
    packageName: String,
    needsRuntimeNotificationRequest: Boolean,
    notificationGuidance: NotificationGuidance,
    onRequestNotificationPermission: () -> Unit,
    onOpenSystemSettings: (SystemSettingsIntentSpec) -> Unit
) {
    if (needsRuntimeNotificationRequest && notificationGuidance != NotificationGuidance.OpenAppSettings) {
        Button(onClick = onRequestNotificationPermission) {
            Text("Allow notifications")
        }
    }
    DeniedPermissionGuidance.text(notificationGuidance)?.let { guidanceText ->
        Text(
            text = guidanceText,
            style = MaterialTheme.typography.bodyMedium
        )
    }
    when (notificationGuidance) {
        NotificationGuidance.RetryRequest -> Button(onClick = onRequestNotificationPermission) {
            Text("Try again")
        }
        NotificationGuidance.OpenAppSettings -> Button(
            onClick = {
                onOpenSystemSettings(SystemSettingsIntentFactory.appDetails(packageName))
            }
        ) {
            Text("Open app settings")
        }
        NotificationGuidance.None -> Unit
    }
    OutlinedButton(
        onClick = {
            onOpenSystemSettings(SystemSettingsIntentFactory.appNotificationSettings(packageName))
        }
    ) {
        Text("Open notification settings")
    }
}

@Composable
private fun BatteryStepActions(
    packageName: String,
    onOpenSystemSettings: (SystemSettingsIntentSpec) -> Unit
) {
    Button(
        onClick = {
            onOpenSystemSettings(
                SystemSettingsIntentFactory.requestIgnoreBatteryOptimizations(packageName)
            )
        }
    ) {
        Text("Exclude from battery optimization")
    }
    OutlinedButton(
        onClick = {
            onOpenSystemSettings(SystemSettingsIntentFactory.batteryOptimizationSettings())
        }
    ) {
        Text("Open battery settings")
    }
}
