package com.remainder.app.ui.alarm

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.remainder.app.alarm.AlarmConfirmation

@Composable
fun AlarmConfirmationScreen(
    confirmation: AlarmConfirmation,
    onDone: () -> Unit
) {
    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Alarm set",
                style = MaterialTheme.typography.displaySmall
            )
            Text(
                text = confirmation.timeText,
                style = MaterialTheme.typography.displayMedium,
                modifier = Modifier.testTag("confirmation_time")
            )
            Text(
                text = confirmation.dayText,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.testTag("confirmation_day")
            )
            Text(
                text = confirmation.title,
                style = MaterialTheme.typography.bodyLarge
            )
            Button(
                onClick = onDone,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Done")
            }
        }
    }
}
