package com.drew654.weather.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import com.drew654.weather.models.WeatherViewModel

@Composable
fun PlaceScreen(id: String, weatherViewModel: WeatherViewModel) {
    val place = weatherViewModel.getPlace(id)
    val forecast = weatherViewModel.forecast.collectAsState()

    LaunchedEffect(place) {
        if (place != null) {
            weatherViewModel.fetchForecast(place)
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column {
            Text(text = place?.name ?: "")
            Text(text = "Location: ${place?.latitude}, ${place?.longitude}")
            Text(text = "Forecast")
            LazyColumn {
                items(forecast.value?.hourlyTemperature ?: emptyList()) { hour ->
                    Text(text = "${hour.first} - ${hour.second}")
                }
            }
        }
    }
}
