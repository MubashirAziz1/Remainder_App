package com.remainder.app.ui.navigation

sealed class RemainderDestinations(val route: String) {
    data object Home : RemainderDestinations("")
    data object Settings : RemainderDestinations("")

    companion object {
        val startDestination: String = ""
    }
}
