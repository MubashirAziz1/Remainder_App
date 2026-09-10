package com.remainder.app.ui.alarm

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.remainder.app.ui.theme.RemainderTheme
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class AlarmFailureScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun showsErrorMessageWithRetryAndCancelActions() {
        composeRule.setContent {
            RemainderTheme {
                AlarmFailureScreen(
                    errorMessage = "No Clock app can create alarms",
                    onRetry = {},
                    onCancel = {}
                )
            }
        }

        composeRule.onNodeWithText("Alarm not created").assertIsDisplayed()
        composeRule.onNodeWithText("No Clock app can create alarms").assertIsDisplayed()
        composeRule.onNodeWithText("Retry").assertIsDisplayed()
        composeRule.onNodeWithText("Cancel").assertIsDisplayed()
    }

    @Test
    fun retryInvokesCallback() {
        var retried = false
        composeRule.setContent {
            RemainderTheme {
                AlarmFailureScreen(
                    errorMessage = "Could not open the Clock app",
                    onRetry = { retried = true },
                    onCancel = {}
                )
            }
        }

        composeRule.onNodeWithText("Retry").performClick()

        assertTrue(retried)
    }

    @Test
    fun cancelInvokesCallbackWithoutRetrying() {
        var retried = false
        var cancelled = false
        composeRule.setContent {
            RemainderTheme {
                AlarmFailureScreen(
                    errorMessage = "Could not open the Clock app",
                    onRetry = { retried = true },
                    onCancel = { cancelled = true }
                )
            }
        }

        composeRule.onNodeWithText("Cancel").performClick()

        assertTrue(cancelled)
        assertFalse(retried)
    }
}
