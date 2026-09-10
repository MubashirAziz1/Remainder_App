package com.remainder.app.ui.alarm

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

private val PickerOptionWidth = 56.dp
private val PickerOptionSpacing = 4.dp

@Composable
internal fun TwentyFourHourPickers(
    hour: Int,
    minute: Int,
    onHourChange: (Int) -> Unit,
    onMinuteChange: (Int) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Hour %02d".format(hour),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .weight(1f)
                    .testTag("alarm_hour_picker")
            )
            Text(
                text = "Minute %02d".format(minute),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .weight(1f)
                    .testTag("alarm_minute_picker")
            )
        }
        DigitPicker(
            label = "Hour",
            value = hour,
            range = 0..23,
            onChange = onHourChange,
            optionTagPrefix = "alarm_hour_option_"
        )
        DigitPicker(
            label = "Minute",
            value = minute,
            range = 0..59,
            onChange = onMinuteChange,
            optionTagPrefix = "alarm_minute_option_"
        )
    }
}

@Composable
private fun DigitPicker(
    label: String,
    value: Int,
    range: IntRange,
    onChange: (Int) -> Unit,
    optionTagPrefix: String
) {
    val scrollState = rememberScrollState()
    val density = LocalDensity.current
    LaunchedEffect(Unit) {
        val index = (value - range.first).coerceIn(0, range.count() - 1)
        val itemWidthPx = with(density) { (PickerOptionWidth + PickerOptionSpacing).toPx() }
        scrollState.scrollTo((index * itemWidthPx).toInt())
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(scrollState),
        horizontalArrangement = Arrangement.spacedBy(PickerOptionSpacing)
    ) {
        range.forEach { option ->
            key(option) {
                Button(
                    onClick = { onChange(option) },
                    modifier = Modifier
                        .width(PickerOptionWidth)
                        .testTag("$optionTagPrefix$option")
                        .semantics {
                            contentDescription = "$label $option"
                            selected = option == value
                        }
                ) {
                    Text(
                        text = "%02d".format(option),
                        fontWeight = if (option == value) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }
    }
}
