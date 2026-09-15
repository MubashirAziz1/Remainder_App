package com.remainder.app.ui.navigation

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.remainder.app.LockScreenAlarmActivity
import com.remainder.app.onboarding.NotificationGuidance
import com.remainder.app.onboarding.SystemSettingsIntentSpec
import com.remainder.app.ui.home.HomeScreen
import com.remainder.app.ui.onboarding.OnboardingScreen
import com.remainder.app.ui.settings.SettingsScreen

@Composable
fun RemainderNavHost(
    navController: NavHostController = rememberNavController(),
    startOnboarding: Boolean = false,
    onboardingPackageName: String = "",
    needsRuntimeNotificationRequest: Boolean = false,
    notificationGuidance: NotificationGuidance = NotificationGuidance.None,
    onRequestNotificationPermission: () -> Unit = {},
    onOpenSystemSettings: (SystemSettingsIntentSpec) -> Unit = {},
    onOnboardingCompleted: () -> Unit = {},
    lockScreenReminderEnabled: Boolean = false,
    onLockScreenReminderChange: (Boolean) -> Unit = {}
) {
    val context = LocalContext.current
    NavHost(
        navController = navController,
        startDestination = if (startOnboarding) {
            RemainderDestinations.Onboarding.route
        } else {
            RemainderDestinations.startDestination
        }
    ) {
        composable(RemainderDestinations.Home.route) {
            HomeScreen(
                onOpenSettings = {
                    navController.navigate(RemainderDestinations.Settings.route)
                },
                onCreateAlarm = {
                    context.startActivity(Intent(context, LockScreenAlarmActivity::class.java))
                }
            )
        }
        composable(RemainderDestinations.Settings.route) {
            SettingsScreen(
                onBack = { navController.popBackStack() },
                onOpenOnboarding = {
                    navController.navigate(RemainderDestinations.Onboarding.route)
                },
                lockScreenReminderEnabled = lockScreenReminderEnabled,
                onLockScreenReminderChange = onLockScreenReminderChange
            )
        }
        composable(RemainderDestinations.Onboarding.route) {
            OnboardingScreen(
                packageName = onboardingPackageName,
                needsRuntimeNotificationRequest = needsRuntimeNotificationRequest,
                notificationGuidance = notificationGuidance,
                onRequestNotificationPermission = onRequestNotificationPermission,
                onOpenSystemSettings = onOpenSystemSettings,
                onCompleted = {
                    onOnboardingCompleted()
                    navController.navigate(RemainderDestinations.Home.route) {
                        popUpTo(RemainderDestinations.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }
    }
}
