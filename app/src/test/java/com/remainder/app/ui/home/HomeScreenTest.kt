package com.remainder.app.ui.home

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.remainder.app.ui.theme.RemainderTheme
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class HomeScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun showsAppTitle() {
        composeRule.setContent {
            RemainderTheme {
                HomeScreen(onOpenSettings = {})
            }
        }
        composeRule.onNodeWithText("Remainder").assertIsDisplayed()
    }

    @Test
    fun settingsActionInvokesCallback() {
        var opened = false
        composeRule.setContent {
            RemainderTheme {
                HomeScreen(onOpenSettings = { opened = true })
            }
        }
        composeRule.onNodeWithText("Open settings").performClick()
        assertTrue(opened)
    }

    @Test
    fun createAlarmActionInvokesCallback() {
        var opened = false
        composeRule.setContent {
            RemainderTheme {
                HomeScreen(
                    onOpenSettings = {},
                    onCreateAlarm = { opened = true }
                )
            }
        }
        composeRule.onNodeWithText("New alarm").performClick()
        assertTrue(opened)
    }
}
