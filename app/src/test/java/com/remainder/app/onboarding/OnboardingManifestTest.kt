package com.remainder.app.onboarding

import com.remainder.app.testutil.ProjectPaths
import org.junit.Assert.assertTrue
import org.junit.Test

class OnboardingManifestTest {

    private val manifest: String
        get() = ProjectPaths.appDir.resolve("src/main/AndroidManifest.xml").readText()

    @Test
    fun declaresRequestIgnoreBatteryOptimizationsPermission() {
        assertTrue(
            "AndroidManifest must declare REQUEST_IGNORE_BATTERY_OPTIMIZATIONS for the battery onboarding step",
            manifest.contains("android.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS")
        )
    }
}
