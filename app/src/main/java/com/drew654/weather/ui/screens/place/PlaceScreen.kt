package com.drew654.weather.ui.screens.place

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import com.drew654.weather.models.WeatherViewModel

@Composable
fun PlaceScreen(id: String, weatherViewModel: WeatherViewModel) {
    val place = weatherViewModel.getPlace(id)
    val currentWeather = weatherViewModel.currentWeather.collectAsState()
    val forecast = weatherViewModel.forecast.collectAsState()

    LaunchedEffect(place) {
        if (place != null) {
            weatherViewModel.fetchCurrentWeather(place)
            weatherViewModel.fetchForecast(place)
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column {
            Text(text = place?.name ?: "")
            Text(text = "Location: ${place?.latitude}, ${place?.longitude}")
            Text(text = "Current Weather")
            Text(text = "Temperature: ${currentWeather.value?.temperature}")
            Text(text = "Relative Humidity: ${currentWeather.value?.relativeHumidity}")
            Text(text = "Apparent Temperature: ${currentWeather.value?.apparentTemperature}")
            Text(text = "Is Day: ${currentWeather.value?.isDay}")
            Text(text = "Precipitation: ${currentWeather.value?.precipitation}")
            Text(text = "Rain: ${currentWeather.value?.rain}")
            Text(text = "Showers: ${currentWeather.value?.showers}")
            Text(text = "Snowfall: ${currentWeather.value?.snowfall}")
            Text(text = "Weather Code: ${currentWeather.value?.weatherCode}")
            Text(text = "Wind Speed: ${currentWeather.value?.windSpeed}")
            Text(text = "Wind Direction: ${currentWeather.value?.windDirection}")
            Text(text = "Wind Gusts: ${currentWeather.value?.windGusts}")
            Text(text = "Forecast")
            LazyColumn {
                itemsIndexed(forecast.value?.hourlyTemperature ?: emptyList()) { index, hour ->
                    Text(text = "$index - $hour")
                }
                itemsIndexed(forecast.value?.hourlyWeatherCode ?: emptyList()) { index, hour ->
                    Text(text = "$index - $hour")
                }
            }
        }
    }
}
