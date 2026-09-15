package com.remainder.app.onboarding

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class OnboardingPolicyTest {

    @Test
    fun runtimeRequestNeededOnAndroid13PlusWhenNotGranted() {
        assertTrue(OnboardingPolicy.needsRuntimeNotificationRequest(33, permissionGranted = false))
        assertTrue(OnboardingPolicy.needsRuntimeNotificationRequest(36, permissionGranted = false))
    }

    @Test
    fun noRuntimeRequestWhenPermissionAlreadyGranted() {
        assertFalse(OnboardingPolicy.needsRuntimeNotificationRequest(34, permissionGranted = true))
    }

    @Test
    fun noRuntimeRequestBeforeAndroid13() {
        assertFalse(OnboardingPolicy.needsRuntimeNotificationRequest(27, permissionGranted = false))
        assertFalse(OnboardingPolicy.needsRuntimeNotificationRequest(32, permissionGranted = false))
    }
}
