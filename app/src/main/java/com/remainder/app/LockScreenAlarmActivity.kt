package com.remainder.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.remainder.app.alarm.AlarmConfirmationFormatter
import com.remainder.app.alarm.AlarmDraft
import com.remainder.app.alarm.AlarmOccurrence
import com.remainder.app.alarm.AlarmScheduleError
import com.remainder.app.alarm.AlarmScheduleResult
import com.remainder.app.alarm.AlarmScheduler
import com.remainder.app.alarm.ClockAlarmScheduler
import com.remainder.app.alarm.ContextAlarmLauncher
import com.remainder.app.ui.alarm.AlarmConfirmationScreen
import com.remainder.app.ui.alarm.AlarmFailureScreen
import com.remainder.app.ui.alarm.CreateAlarmScreen
import com.remainder.app.ui.theme.RemainderTheme
import java.time.Clock
import java.time.LocalDate

class LockScreenAlarmActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setShowWhenLocked(true)
        setTurnScreenOn(true)
        enableEdgeToEdge()
        val alarmScheduler = ClockAlarmScheduler(ContextAlarmLauncher(this))
        setContent {
            RemainderTheme {
                LockScreenAlarmContent(
                    alarmScheduler = alarmScheduler,
                    onFinish = { finish() }
                )
            }
        }
    }
}

internal sealed interface AlarmUiState {
    data object Editing : AlarmUiState
    data class Failed(val draft: AlarmDraft, val error: AlarmScheduleError) : AlarmUiState
    data class Scheduled(val occurrence: AlarmOccurrence) : AlarmUiState
}

@Composable
internal fun LockScreenAlarmContent(
    alarmScheduler: AlarmScheduler,
    onFinish: () -> Unit,
    clock: Clock = Clock.systemDefaultZone()
) {
    var state by remember { mutableStateOf<AlarmUiState>(AlarmUiState.Editing) }

    fun attemptSchedule(draft: AlarmDraft) {
        state = when (val result = alarmScheduler.schedule(draft)) {
            is AlarmScheduleResult.Scheduled -> AlarmUiState.Scheduled(result.occurrence)
            is AlarmScheduleResult.Failed -> AlarmUiState.Failed(draft, result.error)
        }
    }

    when (val current = state) {
        is AlarmUiState.Editing -> CreateAlarmScreen(
            onConfirm = { draft -> attemptSchedule(draft) },
            onCancel = onFinish
        )
        is AlarmUiState.Failed -> AlarmFailureScreen(
            errorMessage = current.error.userMessage,
            onRetry = { attemptSchedule(current.draft) },
            onCancel = onFinish
        )
        is AlarmUiState.Scheduled -> AlarmConfirmationScreen(
            confirmation = AlarmConfirmationFormatter.format(
                occurrence = current.occurrence,
                today = LocalDate.now(clock)
            ),
            onDone = onFinish
        )
    }
}
