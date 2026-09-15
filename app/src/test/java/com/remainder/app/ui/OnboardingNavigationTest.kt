package com.remainder.app.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.remainder.app.ui.navigation.RemainderNavHost
import com.remainder.app.ui.theme.RemainderTheme
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class OnboardingNavigationTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun homeIsStartDestinationByDefault() {
        composeRule.setContent {
            RemainderTheme {
                RemainderNavHost()
            }
        }

        composeRule.onNodeWithText("Remainder").assertIsDisplayed()
    }

    @Test
    fun onboardingIsStartDestinationWhenRequested() {
        composeRule.setContent {
            RemainderTheme {
                RemainderNavHost(
                    startOnboarding = true,
                    onboardingPackageName = "com.remainder.app"
                )
            }
        }

        composeRule.onNodeWithText("Notifications").assertIsDisplayed()
    }

    @Test
    fun completingOnboardingLandsOnHomeAndReportsCompletion() {
        var completed = false
        composeRule.setContent {
            RemainderTheme {
                RemainderNavHost(
                    startOnboarding = true,
                    onboardingPackageName = "com.remainder.app",
                    onOnboardingCompleted = { completed = true }
                )
            }
        }

        composeRule.onNodeWithText("Next").performClick()
        composeRule.onNodeWithText("Next").performClick()
        composeRule.onNodeWithText("Done").performClick()

        assertTrue(completed)
        composeRule.onNodeWithText("Remainder").assertIsDisplayed()
    }

    @Test
    fun settingsCanReopenOnboarding() {
        composeRule.setContent {
            RemainderTheme {
                RemainderNavHost(onboardingPackageName = "com.remainder.app")
            }
        }

        composeRule.onNodeWithText("Open settings").performClick()
        composeRule.onNodeWithText("Notification setup").performClick()

        composeRule.onNodeWithText("Notifications").assertIsDisplayed()
    }
}
