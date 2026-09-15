package com.remainder.app.ui.navigation

sealed class RemainderDestinations(val route: String) {
    data object Home : RemainderDestinations("home")
    data object Settings : RemainderDestinations("settings")
    data object Onboarding : RemainderDestinations("onboarding")

    companion object {
        val startDestination: String get() = Home.route
    }
}
