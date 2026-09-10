package com.remainder.app.alarm

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import java.time.Clock
import java.time.LocalDateTime

interface AlarmScheduler {
    fun schedule(draft: AlarmDraft): AlarmScheduleResult
}

sealed class AlarmScheduleResult {
    data class Scheduled(val occurrence: AlarmOccurrence) : AlarmScheduleResult()
    data class Failed(val error: AlarmScheduleError) : AlarmScheduleResult()
}

enum class AlarmScheduleError(val userMessage: String) {
    NoClockHandler("No Clock app can create alarms"),
    ClockLaunchFailed("Could not open the Clock app")
}

interface AlarmLauncher {
    fun canHandle(intent: Intent): Boolean
    fun launch(intent: Intent)
}

class ContextAlarmLauncher(private val context: Context) : AlarmLauncher {
    override fun canHandle(intent: Intent): Boolean {
        return intent.resolveActivity(context.packageManager) != null
    }

    override fun launch(intent: Intent) {
        context.startActivity(intent)
    }
}

class ClockAlarmScheduler(
    private val launcher: AlarmLauncher,
    private val clock: Clock = Clock.systemDefaultZone()
) : AlarmScheduler {
    override fun schedule(draft: AlarmDraft): AlarmScheduleResult {
        val intent = AlarmIntentFactory.create(draft)
        if (!launcher.canHandle(intent)) {
            return AlarmScheduleResult.Failed(AlarmScheduleError.NoClockHandler)
        }

        return try {
            launcher.launch(intent)
            AlarmScheduleResult.Scheduled(
                AlarmOccurrenceResolver.resolve(draft, LocalDateTime.now(clock))
            )
        } catch (_: ActivityNotFoundException) {
            AlarmScheduleResult.Failed(AlarmScheduleError.ClockLaunchFailed)
        } catch (_: SecurityException) {
            AlarmScheduleResult.Failed(AlarmScheduleError.ClockLaunchFailed)
        } catch (_: IllegalStateException) {
            AlarmScheduleResult.Failed(AlarmScheduleError.ClockLaunchFailed)
        }
    }
}
