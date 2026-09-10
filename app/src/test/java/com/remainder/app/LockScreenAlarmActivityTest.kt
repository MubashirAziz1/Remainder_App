package com.remainder.app

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.remainder.app.alarm.AlarmDraft
import com.remainder.app.alarm.AlarmOccurrence
import com.remainder.app.alarm.AlarmScheduleError
import com.remainder.app.alarm.AlarmScheduleResult
import com.remainder.app.alarm.AlarmScheduler
import com.remainder.app.ui.theme.RemainderTheme
import java.time.Clock
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class LockScreenAlarmActivityTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val clock = Clock.fixed(
        Instant.parse("2026-09-09T08:00:00Z"),
        ZoneId.of("UTC")
    )

    @Test
    fun launchesAlarmFormWithoutFinishing() {
        var finished = false
        setContent(scheduler = SuccessfulScheduler(), onFinish = { finished = true })

        composeRule.onNodeWithText("New alarm").assertIsDisplayed()
        composeRule.onNodeWithTag("alarm_title").assertIsDisplayed()
        composeRule.onNodeWithTag("alarm_hour_picker").assertIsDisplayed()
        composeRule.onNodeWithTag("alarm_minute_picker").assertIsDisplayed()
        composeRule.onNodeWithText("Confirm").assertIsDisplayed()
        composeRule.onNodeWithText("Cancel").assertIsDisplayed()
        assertFalse(finished)
    }

    @Test
    fun cancelFinishesActivity() {
        var finished = false
        setContent(scheduler = SuccessfulScheduler(), onFinish = { finished = true })

        composeRule.onNodeWithText("Cancel").performClick()
        assertTrue(finished)
    }

    @Test
    fun confirmWithoutTitleKeepsActivityOpen() {
        var finished = false
        setContent(scheduler = SuccessfulScheduler(), onFinish = { finished = true })

        composeRule.onNodeWithText("Confirm").performClick()
        composeRule.onNodeWithText("Title is required").assertIsDisplayed()
        assertFalse(finished)
    }

    @Test
    fun successfulConfirmShowsConfirmationWithTimeTitleAndTodayStatus() {
        var finished = false
        setContent(scheduler = SuccessfulScheduler(), onFinish = { finished = true })

        composeRule.onNodeWithTag("alarm_title").performTextInput("Gym")
        composeRule.onNodeWithText("Confirm").performClick()

        composeRule.onNodeWithText("Alarm set").assertIsDisplayed()
        composeRule.onNodeWithText("07:00").assertIsDisplayed()
        composeRule.onNodeWithText("Gym").assertIsDisplayed()
        composeRule.onNodeWithText("Today").assertIsDisplayed()
        assertFalse(finished)
    }

    @Test
    fun doneOnConfirmationFinishesActivity() {
        var finished = false
        setContent(scheduler = SuccessfulScheduler(), onFinish = { finished = true })

        composeRule.onNodeWithTag("alarm_title").performTextInput("Gym")
        composeRule.onNodeWithText("Confirm").performClick()
        composeRule.onNodeWithText("Done").performClick()

        assertTrue(finished)
    }

    @Test
    fun failedConfirmShowsErrorWithRetryAndCancel() {
        var finished = false
        setContent(
            scheduler = FailedScheduler(AlarmScheduleError.NoClockHandler),
            onFinish = { finished = true }
        )

        composeRule.onNodeWithTag("alarm_title").performTextInput("Gym")
        composeRule.onNodeWithText("Confirm").performClick()

        composeRule.onNodeWithText("Alarm not created").assertIsDisplayed()
        composeRule.onNodeWithText("No Clock app can create alarms").assertIsDisplayed()
        composeRule.onNodeWithText("Retry").assertIsDisplayed()
        composeRule.onNodeWithText("Cancel").assertIsDisplayed()
        assertFalse(finished)
    }

    @Test
    fun retryAfterFailureSchedulesAgainAndShowsConfirmation() {
        var finished = false
        val scheduler = FlakyScheduler(failuresBeforeSuccess = 1)
        setContent(scheduler = scheduler, onFinish = { finished = true })

        composeRule.onNodeWithTag("alarm_title").performTextInput("Gym")
        composeRule.onNodeWithText("Confirm").performClick()
        composeRule.onNodeWithText("Alarm not created").assertIsDisplayed()

        composeRule.onNodeWithText("Retry").performClick()

        assertEquals(2, scheduler.attempts)
        composeRule.onNodeWithText("Alarm set").assertIsDisplayed()
        composeRule.onNodeWithText("Gym").assertIsDisplayed()
        assertFalse(finished)
    }

    @Test
    fun cancelOnFailureFinishesActivity() {
        var finished = false
        setContent(
            scheduler = FailedScheduler(AlarmScheduleError.ClockLaunchFailed),
            onFinish = { finished = true }
        )

        composeRule.onNodeWithTag("alarm_title").performTextInput("Gym")
        composeRule.onNodeWithText("Confirm").performClick()
        composeRule.onNodeWithText("Alarm not created").assertIsDisplayed()

        composeRule.onNodeWithText("Cancel").performClick()

        assertTrue(finished)
    }

    private fun setContent(
        scheduler: AlarmScheduler,
        onFinish: () -> Unit
    ) {
        composeRule.setContent {
            RemainderTheme {
                LockScreenAlarmContent(
                    alarmScheduler = scheduler,
                    onFinish = onFinish,
                    clock = clock
                )
            }
        }
    }
}

private class SuccessfulScheduler : AlarmScheduler {
    override fun schedule(draft: AlarmDraft): AlarmScheduleResult {
        return AlarmScheduleResult.Scheduled(
            AlarmOccurrence(
                date = LocalDate.of(2026, 9, 9),
                hour = draft.hour,
                minute = draft.minute,
                title = draft.title
            )
        )
    }
}

private class FailedScheduler(
    private val error: AlarmScheduleError
) : AlarmScheduler {
    override fun schedule(draft: AlarmDraft): AlarmScheduleResult {
        return AlarmScheduleResult.Failed(error)
    }
}

private class FlakyScheduler(
    private val failuresBeforeSuccess: Int
) : AlarmScheduler {
    var attempts = 0
        private set

    override fun schedule(draft: AlarmDraft): AlarmScheduleResult {
        attempts += 1
        return if (attempts <= failuresBeforeSuccess) {
            AlarmScheduleResult.Failed(AlarmScheduleError.ClockLaunchFailed)
        } else {
            AlarmScheduleResult.Scheduled(
                AlarmOccurrence(
                    date = LocalDate.of(2026, 9, 9),
                    hour = draft.hour,
                    minute = draft.minute,
                    title = draft.title
                )
            )
        }
    }
}
