package com.remainder.app.alarm

import android.content.ActivityNotFoundException
import android.provider.AlarmClock
import java.time.Clock
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ClockAlarmSchedulerTest {

    private val clock = Clock.fixed(
        Instant.parse("2026-09-09T08:00:00Z"),
        ZoneId.of("UTC")
    )

    @Test
    fun launchesClockIntentAndReturnsResolvedOccurrence() {
        val launcher = RecordingAlarmLauncher(canHandle = true)
        val scheduler = ClockAlarmScheduler(launcher = launcher, clock = clock)

        val result = scheduler.schedule(AlarmDraft(hour = 9, minute = 15, title = "Medicine"))

        assertTrue(result is AlarmScheduleResult.Scheduled)
        val scheduled = result as AlarmScheduleResult.Scheduled
        assertEquals(LocalDate.of(2026, 9, 9), scheduled.occurrence.date)
        assertEquals(AlarmClock.ACTION_SET_ALARM, launcher.launchedSpec?.action)
        assertEquals("Medicine", launcher.launchedSpec?.message)
        assertEquals(9, launcher.launchedSpec?.hour)
        assertEquals(15, launcher.launchedSpec?.minute)
        assertTrue(launcher.launchedSpec?.skipUi == true)
    }

    @Test
    fun returnsFailureWhenNoClockHandlerExists() {
        val launcher = RecordingAlarmLauncher(canHandle = false)
        val scheduler = ClockAlarmScheduler(launcher = launcher, clock = clock)

        val result = scheduler.schedule(AlarmDraft(hour = 9, minute = 15, title = "Medicine"))

        assertEquals(
            AlarmScheduleResult.Failed(AlarmScheduleError.NoClockHandler),
            result
        )
        assertFalse(launcher.launchCalled)
    }

    @Test
    fun returnsFailureWhenClockLaunchThrows() {
        val launcher = RecordingAlarmLauncher(
            canHandle = true,
            launchFailure = ActivityNotFoundException("Clock vanished")
        )
        val scheduler = ClockAlarmScheduler(launcher = launcher, clock = clock)

        val result = scheduler.schedule(AlarmDraft(hour = 9, minute = 15, title = "Medicine"))

        assertEquals(
            AlarmScheduleResult.Failed(AlarmScheduleError.ClockLaunchFailed),
            result
        )
    }

    @Test
    fun returnsFailureWhenClockLaunchThrowsSecurityException() {
        val launcher = RecordingAlarmLauncher(
            canHandle = true,
            launchFailure = SecurityException("SET_ALARM revoked")
        )
        val scheduler = ClockAlarmScheduler(launcher = launcher, clock = clock)

        val result = scheduler.schedule(AlarmDraft(hour = 9, minute = 15, title = "Medicine"))

        assertEquals(
            AlarmScheduleResult.Failed(AlarmScheduleError.ClockLaunchFailed),
            result
        )
    }

    @Test
    fun returnsFailureWhenClockLaunchThrowsIllegalStateException() {
        val launcher = RecordingAlarmLauncher(
            canHandle = true,
            launchFailure = IllegalStateException("Not allowed in background")
        )
        val scheduler = ClockAlarmScheduler(launcher = launcher, clock = clock)

        val result = scheduler.schedule(AlarmDraft(hour = 9, minute = 15, title = "Medicine"))

        assertEquals(
            AlarmScheduleResult.Failed(AlarmScheduleError.ClockLaunchFailed),
            result
        )
    }
}

private class RecordingAlarmLauncher(
    private val canHandle: Boolean,
    private val launchFailure: RuntimeException? = null
) : AlarmLauncher {
    var launchedSpec: AlarmIntentSpec? = null
        private set
    var launchCalled: Boolean = false
        private set

    override fun canHandle(spec: AlarmIntentSpec): Boolean = canHandle

    override fun launch(spec: AlarmIntentSpec) {
        launchCalled = true
        if (launchFailure != null) {
            throw launchFailure
        }
        launchedSpec = spec
    }
}
