package com.drew654.weather.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import com.drew654.weather.models.Place
import com.drew654.weather.models.WeatherViewModel

@Composable
fun SettingsScreen(
    weatherViewModel: WeatherViewModel
) {
    val places = weatherViewModel.places.collectAsState()
    val name = remember { mutableStateOf("") }
    val latitude = remember { mutableDoubleStateOf(0.0) }
    val longitude = remember { mutableDoubleStateOf(0.0) }
    val focusManager = LocalFocusManager.current
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable(
                interactionSource = interactionSource,
                indication = null,
            ) {
                focusManager.clearFocus()
            }
    ) {
        Column {
            Text(text = "Settings")
            OutlinedTextField(
                value = name.value,
                onValueChange = { name.value = it },
                label = { Text("Name") }
            )
            OutlinedTextField(
                value = latitude.doubleValue.toString(),
                onValueChange = { latitude.doubleValue = it.toDoubleOrNull() ?: 0.0 },
                label = { Text("Latitude") }
            )
            OutlinedTextField(
                value = longitude.doubleValue.toString(),
                onValueChange = { longitude.doubleValue = it.toDoubleOrNull() ?: 0.0 },
                label = { Text("Longitude") }
            )
            Button(
                onClick = {
                    weatherViewModel.addPlace(
                        Place(
                            name.value,
                            latitude.doubleValue,
                            longitude.doubleValue
                        )
                    )
                    name.value = ""
                    latitude.doubleValue = 0.0
                    longitude.doubleValue = 0.0
                },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(text = "Add Place")
            }
            Text(text = "Places")
            LazyColumn {
                items(places.value) { place ->
                    Text(text = place.name + ": " + place.latitude + ", " + place.longitude)
                }
            }
        }
    }
}
