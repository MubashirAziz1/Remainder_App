package com.remainder.app.ui.navigation

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.remainder.app.LockScreenAlarmActivity
import com.remainder.app.ui.home.HomeScreen
import com.remainder.app.ui.settings.SettingsScreen

@Composable
fun RemainderNavHost(
    navController: NavHostController = rememberNavController(),
    lockScreenReminderEnabled: Boolean = false,
    onLockScreenReminderChange: (Boolean) -> Unit = {}
) {
    val context = LocalContext.current
    NavHost(
        navController = navController,
        startDestination = RemainderDestinations.startDestination
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
                lockScreenReminderEnabled = lockScreenReminderEnabled,
                onLockScreenReminderChange = onLockScreenReminderChange
            )
        }
    }
}
