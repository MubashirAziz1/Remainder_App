package com.remainder.app.onboarding

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class DeniedPermissionGuidanceTest {

    @Test
    fun grantedPermissionNeedsNoGuidance() {
        assertEquals(
            NotificationGuidance.None,
            DeniedPermissionGuidance.resolve(
                permissionGranted = true,
                denialObserved = true,
                showRationale = false
            )
        )
    }

    @Test
    fun noGuidanceBeforeAnyDenial() {
        assertEquals(
            NotificationGuidance.None,
            DeniedPermissionGuidance.resolve(
                permissionGranted = false,
                denialObserved = false,
                showRationale = false
            )
        )
    }

    @Test
    fun denialWithRationaleSuggestsRetryingRequest() {
        assertEquals(
            NotificationGuidance.RetryRequest,
            DeniedPermissionGuidance.resolve(
                permissionGranted = false,
                denialObserved = true,
                showRationale = true
            )
        )
    }

    @Test
    fun denialWithoutRationaleSendsUserToAppSettings() {
        assertEquals(
            NotificationGuidance.OpenAppSettings,
            DeniedPermissionGuidance.resolve(
                permissionGranted = false,
                denialObserved = true,
                showRationale = false
            )
        )
    }

    @Test
    fun noneHasNoText() {
        assertNull(DeniedPermissionGuidance.text(NotificationGuidance.None))
    }

    @Test
    fun retryTextExplainsPermissionCanBeRequestedAgain() {
        val text = DeniedPermissionGuidance.text(NotificationGuidance.RetryRequest)

        assertTrue(text.isNullOrBlank().not())
        assertTrue(text!!.contains("again", ignoreCase = true))
    }

    @Test
    fun permanentDenialTextPointsToAndroidSettings() {
        val text = DeniedPermissionGuidance.text(NotificationGuidance.OpenAppSettings)

        assertTrue(text.isNullOrBlank().not())
        assertTrue(text!!.contains("settings", ignoreCase = true))
    }
}
