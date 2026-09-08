package com.remainder.app.scaffold

import com.remainder.app.testutil.ProjectPaths
import org.junit.Assert.assertTrue
import org.junit.Test

class GradleSdkTest {

    private val gradleText: String
        get() = ProjectPaths.appDir.resolve("build.gradle.kts").readText()

    @Test
    fun minSdkIs27() {
        assertTrue(
            "app/build.gradle.kts must set minSdk = 27",
            gradleText.contains("minSdk = 27")
        )
    }

    @Test
    fun compileSdkIs37() {
        assertTrue(
            "app/build.gradle.kts must set compileSdk = 37",
            gradleText.contains("compileSdk = 37")
        )
    }

    @Test
    fun targetSdkIs36() {
        assertTrue(
            "app/build.gradle.kts must set targetSdk = 36",
            gradleText.contains("targetSdk = 36")
        )
    }

    @Test
    fun composeIsEnabled() {
        assertTrue(
            "Compose must be enabled",
            gradleText.contains("compose = true")
        )
    }

    @Test
    fun navigationComposeIsOnClasspath() {
        assertTrue(
            "navigation-compose must be a dependency",
            gradleText.contains("androidx.navigation.compose")
        )
    }
}
