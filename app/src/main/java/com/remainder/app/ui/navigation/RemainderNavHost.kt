package com.remainder.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.remainder.app.ui.home.HomeScreen
import com.remainder.app.ui.settings.SettingsScreen

@Composable
fun RemainderNavHost(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = RemainderDestinations.startDestination
    ) {
        composable(RemainderDestinations.Home.route) {
            HomeScreen(
                onOpenSettings = {
                    navController.navigate(RemainderDestinations.Settings.route)
                }
            )
        }
        composable(RemainderDestinations.Settings.route) {
            SettingsScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}
