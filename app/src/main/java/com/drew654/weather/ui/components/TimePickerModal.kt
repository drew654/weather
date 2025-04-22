package com.drew654.weather.ui.components

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Dialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerModal(
    timePickerState: TimePickerState,
    onTimeSelected: (Pair<Int, Int>) -> Unit,
    onDismissRequest: () -> Unit
) {
    Dialog(
        onDismissRequest = {
            onDismissRequest()
            onTimeSelected(Pair(timePickerState.hour, timePickerState.minute))
        }
    ) {
        TimePicker(
            state = timePickerState
        )
    }
}
