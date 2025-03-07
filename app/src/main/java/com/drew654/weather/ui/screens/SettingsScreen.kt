package com.drew654.weather.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import com.drew654.weather.models.WeatherViewModel

@Composable
fun SettingsScreen(
    weatherViewModel: WeatherViewModel
) {
    val places = weatherViewModel.places.collectAsState()
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
            Text(text = "Places")
            LazyColumn {
                items(places.value) { place ->
                    Text(
                        text = place.name + ": " + place.latitude + ", " + place.longitude,
                        modifier = Modifier.clickable {
                            weatherViewModel.setSelectedPlace(place)
                            weatherViewModel.movePlaceToFront(place)
                            weatherViewModel.fetchWeather()
                        }
                    )
                }
            }
        }
    }
}
