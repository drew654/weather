package com.drew654.weather.ui.components

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableLongState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import com.drew654.weather.utils.convertMillisToDate

@Composable
fun DateInputField(
    dateInMillis: MutableLongState
) {
    val datePickerModalIsOpen = remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }

    if (datePickerModalIsOpen.value) {
        DatePickerModal(
            onDateSelected = {
                dateInMillis.longValue = it
                datePickerModalIsOpen.value = false
            },
            onDismissRequest = { datePickerModalIsOpen.value = false }
        )
    }
    OutlinedTextField(
        value = convertMillisToDate(dateInMillis.longValue),
        onValueChange = { },
        placeholder = { Text("MM/DD/YYYY") },
        trailingIcon = {
            Icon(Icons.Default.DateRange, contentDescription = "Select date")
        },
        readOnly = true,
        interactionSource = interactionSource,
        modifier = Modifier
            .fillMaxWidth()
            .pointerInput(dateInMillis) {
                awaitEachGesture {
                    awaitFirstDown(pass = PointerEventPass.Initial)
                    val upEvent = waitForUpOrCancellation(pass = PointerEventPass.Initial)
                    if (upEvent != null) {
                        datePickerModalIsOpen.value = true
                    }
                }
            }
    )
}
