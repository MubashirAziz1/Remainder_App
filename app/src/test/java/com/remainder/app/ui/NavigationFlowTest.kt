package com.remainder.app.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.remainder.app.ui.navigation.RemainderNavHost
import com.remainder.app.ui.theme.RemainderTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class NavigationFlowTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun tappingSettingsOpensSettingsScreen() {
        composeRule.setContent {
            RemainderTheme {
                RemainderNavHost()
            }
        }
        composeRule.onNodeWithText("Open settings").performClick()
        composeRule.onNodeWithText("Settings").assertIsDisplayed()
    }

    @Test
    fun backFromSettingsReturnsHome() {
        composeRule.setContent {
            RemainderTheme {
                RemainderNavHost()
            }
        }
        composeRule.onNodeWithText("Open settings").performClick()
        composeRule.onNodeWithText("Back").performClick()
        composeRule.onNodeWithText("Remainder").assertIsDisplayed()
    }
}
