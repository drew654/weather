package com.drew654.weather.ui.components

import android.text.format.DateFormat.is24HourFormat
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import com.drew654.weather.R
import com.drew654.weather.utils.formatTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeInputField(
    timePickerState: TimePickerState
) {
    val context = LocalContext.current
    val timePickerModalIsOpen = remember { mutableStateOf(false) }

    if (timePickerModalIsOpen.value) {
        TimePickerModal(
            timePickerState = timePickerState,
            onTimeSelected = {
                timePickerState.hour = it.first
                timePickerState.minute = it.second
                timePickerModalIsOpen.value = false
            },
            onDismissRequest = {
                timePickerModalIsOpen.value = false
            }
        )
    }
    OutlinedTextField(
        value = formatTime(
            timePickerState.hour,
            timePickerState.minute,
            is24HourFormat = is24HourFormat(context)
        ),
        onValueChange = { },
        readOnly = true,
        trailingIcon = {
            Icon(
                painter = painterResource(id = R.drawable.baseline_access_time_24),
                contentDescription = "Select time"
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .pointerInput(timePickerState) {
                awaitEachGesture {
                    awaitFirstDown(pass = PointerEventPass.Initial)
                    val upEvent = waitForUpOrCancellation(pass = PointerEventPass.Initial)
                    if (upEvent != null) {
                        timePickerModalIsOpen.value = true
                    }
                }
            }
    )
}
