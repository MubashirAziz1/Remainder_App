package com.remainder.app.ui.settings

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
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
class SettingsScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun showsLockScreenReminderToggle() {
        composeRule.setContent {
            RemainderTheme {
                SettingsScreen(onBack = {})
            }
        }
        composeRule.onNodeWithText("Show lock-screen reminder").assertIsDisplayed()
        composeRule.onNodeWithTag("lock_screen_reminder_toggle").assertIsOff()
    }

    @Test
    fun toggleReflectsEnabledState() {
        composeRule.setContent {
            RemainderTheme {
                SettingsScreen(
                    onBack = {},
                    lockScreenReminderEnabled = true
                )
            }
        }
        composeRule.onNodeWithTag("lock_screen_reminder_toggle").assertIsOn()
    }

    @Test
    fun togglingSwitchInvokesCallback() {
        var enabled: Boolean? = null
        composeRule.setContent {
            RemainderTheme {
                SettingsScreen(
                    onBack = {},
                    lockScreenReminderEnabled = false,
                    onLockScreenReminderChange = { enabled = it }
                )
            }
        }
        composeRule.onNodeWithTag("lock_screen_reminder_toggle").performClick()
        assertTrue(enabled == true)
    }
}
