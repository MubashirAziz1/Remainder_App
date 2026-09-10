package com.remainder.app.ui.alarm

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.remainder.app.alarm.AlarmConfirmation
import com.remainder.app.ui.theme.RemainderTheme
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class AlarmConfirmationScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun showsTimeTitleAndDayStatus() {
        composeRule.setContent {
            RemainderTheme {
                AlarmConfirmationScreen(
                    confirmation = AlarmConfirmation(
                        timeText = "14:45",
                        dayText = "Today",
                        title = "Standup"
                    ),
                    onDone = {}
                )
            }
        }

        composeRule.onNodeWithText("Alarm set").assertIsDisplayed()
        composeRule.onNodeWithTag("confirmation_time").assertIsDisplayed()
        composeRule.onNodeWithText("14:45").assertIsDisplayed()
        composeRule.onNodeWithTag("confirmation_day").assertIsDisplayed()
        composeRule.onNodeWithText("Today").assertIsDisplayed()
        composeRule.onNodeWithText("Standup").assertIsDisplayed()
        composeRule.onNodeWithText("Done").assertIsDisplayed()
    }

    @Test
    fun tomorrowStatusIsShownForNextDayAlarm() {
        composeRule.setContent {
            RemainderTheme {
                AlarmConfirmationScreen(
                    confirmation = AlarmConfirmation(
                        timeText = "08:00",
                        dayText = "Tomorrow",
                        title = "Breakfast"
                    ),
                    onDone = {}
                )
            }
        }

        composeRule.onNodeWithText("Tomorrow").assertIsDisplayed()
        composeRule.onNodeWithText("08:00").assertIsDisplayed()
    }

    @Test
    fun doneInvokesCallback() {
        var done = false
        composeRule.setContent {
            RemainderTheme {
                AlarmConfirmationScreen(
                    confirmation = AlarmConfirmation(
                        timeText = "07:00",
                        dayText = "Today",
                        title = "Gym"
                    ),
                    onDone = { done = true }
                )
            }
        }

        composeRule.onNodeWithText("Done").performClick()

        assertTrue(done)
    }
}
