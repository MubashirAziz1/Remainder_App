package com.remainder.app.alarm

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.provider.AlarmClock
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
    fun canHandle(spec: AlarmIntentSpec): Boolean
    fun launch(spec: AlarmIntentSpec)
}

class ContextAlarmLauncher(private val context: Context) : AlarmLauncher {
    override fun canHandle(spec: AlarmIntentSpec): Boolean {
        return toIntent(spec).resolveActivity(context.packageManager) != null
    }

    override fun launch(spec: AlarmIntentSpec) {
        context.startActivity(toIntent(spec))
    }

    private fun toIntent(spec: AlarmIntentSpec): Intent {
        return Intent(spec.action).apply {
            putExtra(AlarmClock.EXTRA_HOUR, spec.hour)
            putExtra(AlarmClock.EXTRA_MINUTES, spec.minute)
            putExtra(AlarmClock.EXTRA_MESSAGE, spec.message)
            putExtra(AlarmClock.EXTRA_SKIP_UI, spec.skipUi)
        }
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
