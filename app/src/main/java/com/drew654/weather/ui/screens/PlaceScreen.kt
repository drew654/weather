package com.drew654.weather.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.drew654.weather.models.WeatherViewModel

@Composable
fun PlaceScreen(id: String, weatherViewModel: WeatherViewModel) {
    val place = weatherViewModel.getPlace(id)

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column {
            Text(text = place?.name ?: "")
            Text(text = "Location: ${place?.latitude}, ${place?.longitude}")
        }
    }
}
