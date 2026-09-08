package com.remainder.app.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.remainder.app.MainActivity
import org.junit.Rule
import org.junit.Test

class AppLaunchTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun launchesOntoHomeScreen() {
        composeRule.onNodeWithText("Remainder").assertIsDisplayed()
        composeRule.onNodeWithText("Open settings").assertIsDisplayed()
    }

    @Test
    fun navigatesToSettingsAndBack() {
        composeRule.onNodeWithText("Open settings").performClick()
        composeRule.onNodeWithText("Settings").assertIsDisplayed()
        composeRule.onNodeWithText("Back").performClick()
        composeRule.onNodeWithText("Remainder").assertIsDisplayed()
    }
}
