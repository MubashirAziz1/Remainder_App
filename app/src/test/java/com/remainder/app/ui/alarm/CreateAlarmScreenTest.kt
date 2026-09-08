package com.remainder.app.ui.alarm

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.remainder.app.alarm.AlarmDraft
import com.remainder.app.ui.theme.RemainderTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class CreateAlarmScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun showsTitlePickersAndActions() {
        composeRule.setContent {
            RemainderTheme {
                CreateAlarmScreen(onConfirm = {}, onCancel = {})
            }
        }
        composeRule.onNodeWithText("New alarm").assertIsDisplayed()
        composeRule.onNodeWithTag("alarm_title").assertIsDisplayed()
        composeRule.onNodeWithTag("alarm_hour_picker").assertIsDisplayed()
        composeRule.onNodeWithTag("alarm_minute_picker").assertIsDisplayed()
        composeRule.onNodeWithText("Confirm").assertIsDisplayed()
        composeRule.onNodeWithText("Cancel").assertIsDisplayed()
        composeRule.onNodeWithText("AM").assertDoesNotExist()
        composeRule.onNodeWithText("PM").assertDoesNotExist()
        composeRule.onNodeWithText("Title is required").assertDoesNotExist()
    }

    @Test
    fun confirmWithoutTitleShowsErrorAndDoesNotSubmit() {
        var draft: AlarmDraft? = null
        composeRule.setContent {
            RemainderTheme {
                CreateAlarmScreen(onConfirm = { draft = it }, onCancel = {})
            }
        }
        composeRule.onNodeWithText("Confirm").performClick()
        composeRule.onNodeWithText("Title is required").assertIsDisplayed()
        assertNull(draft)
    }

    @Test
    fun whitespaceTitleIsRejected() {
        var draft: AlarmDraft? = null
        composeRule.setContent {
            RemainderTheme {
                CreateAlarmScreen(onConfirm = { draft = it }, onCancel = {})
            }
        }
        composeRule.onNodeWithTag("alarm_title").performTextInput("   ")
        composeRule.onNodeWithText("Confirm").performClick()
        composeRule.onNodeWithText("Title is required").assertIsDisplayed()
        assertNull(draft)
    }

    @Test
    fun confirmWithTitleSubmitsTrimmedDraft() {
        var draft: AlarmDraft? = null
        composeRule.setContent {
            RemainderTheme {
                CreateAlarmScreen(
                    initialHour = 7,
                    initialMinute = 0,
                    onConfirm = { draft = it },
                    onCancel = {}
                )
            }
        }
        composeRule.onNodeWithTag("alarm_title").performTextInput("  Gym  ")
        composeRule.onNodeWithText("Confirm").performClick()
        assertEquals(AlarmDraft(hour = 7, minute = 0, title = "Gym"), draft)
    }

    @Test
    fun hourAndMinutePickersUpdateConfirmedTime() {
        var draft: AlarmDraft? = null
        composeRule.setContent {
            RemainderTheme {
                CreateAlarmScreen(
                    initialHour = 7,
                    initialMinute = 0,
                    onConfirm = { draft = it },
                    onCancel = {}
                )
            }
        }
        composeRule.onNodeWithTag("alarm_hour_picker").performClick()
        composeRule.onNodeWithTag("alarm_hour_option_14").performClick()
        composeRule.onNodeWithTag("alarm_minute_picker").performClick()
        composeRule.onNodeWithTag("alarm_minute_option_45").performClick()
        composeRule.onNodeWithTag("alarm_title").performTextInput("Gym")
        composeRule.onNodeWithText("Confirm").performClick()
        assertEquals(AlarmDraft(hour = 14, minute = 45, title = "Gym"), draft)
    }

    @Test
    fun cancelDismissesWithoutConfirming() {
        var confirmed = false
        var cancelled = false
        composeRule.setContent {
            RemainderTheme {
                CreateAlarmScreen(
                    onConfirm = { confirmed = true },
                    onCancel = { cancelled = true }
                )
            }
        }
        composeRule.onNodeWithTag("alarm_title").performTextInput("Gym")
        composeRule.onNodeWithText("Cancel").performClick()
        assertTrue(cancelled)
        assertTrue(!confirmed)
    }
}
