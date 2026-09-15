package com.remainder.app.ui.navigation

import org.junit.Assert.assertEquals
import org.junit.Test

class RemainderDestinationsTest {

    @Test
    fun homeRouteIsHome() {
        assertEquals("home", RemainderDestinations.Home.route)
    }

    @Test
    fun settingsRouteIsSettings() {
        assertEquals("settings", RemainderDestinations.Settings.route)
    }

    @Test
    fun onboardingRouteIsOnboarding() {
        assertEquals("onboarding", RemainderDestinations.Onboarding.route)
    }

    @Test
    fun startDestinationIsHome() {
        assertEquals("home", RemainderDestinations.startDestination)
    }
}
