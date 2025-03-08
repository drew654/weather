package com.drew654.weather.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import com.drew654.weather.models.WeatherViewModel

@Composable
fun SettingsScreen(
    weatherViewModel: WeatherViewModel
) {
    val places = weatherViewModel.places.collectAsState()
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn {
            items(1) {
                Text(text = "Places")
            }
            items(places.value.size) {
                Text(text = places.value[it].name)
                Text(text = places.value[it].latitude.toString())
                Text(text = places.value[it].longitude.toString())
            }
        }
    }
}
