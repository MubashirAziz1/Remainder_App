package com.remainder.app.onboarding

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class OnboardingStepTest {

    @Test
    fun stepsAreOrderedNotificationsLockScreenBattery() {
        assertEquals(
            listOf(
                OnboardingStep.Notifications,
                OnboardingStep.LockScreen,
                OnboardingStep.BatteryOptimization
            ),
            OnboardingStep.ordered
        )
    }

    @Test
    fun notificationsIsTheFirstStep() {
        assertEquals(OnboardingStep.Notifications, OnboardingStep.first)
    }

    @Test
    fun everyStepHasNonBlankTitleAndBody() {
        OnboardingStep.ordered.forEach { step ->
            assertTrue("title must be non-blank", step.title.isNotBlank())
            assertTrue("body must be non-blank", step.body.isNotBlank())
        }
    }

    @Test
    fun nextAdvancesThroughStepsAndStopsAtEnd() {
        assertEquals(OnboardingStep.LockScreen, OnboardingStep.Notifications.next())
        assertEquals(OnboardingStep.BatteryOptimization, OnboardingStep.LockScreen.next())
        assertNull(OnboardingStep.BatteryOptimization.next())
    }

    @Test
    fun previousGoesBackAndStopsAtStart() {
        assertNull(OnboardingStep.Notifications.previous())
        assertEquals(OnboardingStep.Notifications, OnboardingStep.LockScreen.previous())
        assertEquals(OnboardingStep.LockScreen, OnboardingStep.BatteryOptimization.previous())
    }

    @Test
    fun onlyBatteryOptimizationIsLast() {
        assertFalse(OnboardingStep.Notifications.isLast)
        assertFalse(OnboardingStep.LockScreen.isLast)
        assertTrue(OnboardingStep.BatteryOptimization.isLast)
    }
}
