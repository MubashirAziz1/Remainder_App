package com.remainder.app.ui.alarm

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.remainder.app.alarm.AlarmConfirmResult
import com.remainder.app.alarm.AlarmDraft
import com.remainder.app.alarm.AlarmFormValidator

@Composable
fun CreateAlarmScreen(
    onConfirm: (AlarmDraft) -> Unit,
    onCancel: () -> Unit,
    initialHour: Int = 7,
    initialMinute: Int = 0
) {
    var hour by rememberSaveable { mutableIntStateOf(initialHour.coerceIn(0, 23)) }
    var minute by rememberSaveable { mutableIntStateOf(initialMinute.coerceIn(0, 59)) }
    var title by rememberSaveable { mutableStateOf("") }
    var titleError by rememberSaveable { mutableStateOf<String?>(null) }

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "New alarm",
                style = MaterialTheme.typography.displaySmall
            )
            OutlinedTextField(
                value = title,
                onValueChange = { value ->
                    title = value
                    titleError = null
                },
                label = { Text("Alarm title") },
                isError = titleError != null,
                supportingText = {
                    titleError?.let { error -> Text(error) }
                },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("alarm_title")
            )
            TwentyFourHourPickers(
                hour = hour,
                minute = minute,
                onHourChange = { hour = it },
                onMinuteChange = { minute = it }
            )
            Button(
                onClick = {
                    when (val result = AlarmFormValidator.confirm(hour, minute, title)) {
                        is AlarmConfirmResult.Accepted -> onConfirm(result.draft)
                        is AlarmConfirmResult.Rejected -> titleError = result.message
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Confirm")
            }
            TextButton(onClick = onCancel) {
                Text("Cancel")
            }
        }
    }
}
