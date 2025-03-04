package com.drew654.weather.ui.screens.place

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import com.drew654.weather.models.WeatherViewModel
import java.time.format.DateTimeFormatter

@Composable
fun PlaceScreen(id: String, weatherViewModel: WeatherViewModel) {
    val place = weatherViewModel.getPlace(id)
    val currentWeather = weatherViewModel.currentWeather.collectAsState()
    val forecast = weatherViewModel.forecast.collectAsState()
    val dailyWeather = weatherViewModel.dailyWeather.collectAsState()

    LaunchedEffect(place) {
        if (place != null) {
            weatherViewModel.fetchCurrentWeather(place)
            weatherViewModel.fetchForecast(place)
            weatherViewModel.fetchDailyWeather(place)
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
            Text(
                text = "Sunrise: ${
                    if (dailyWeather.value?.sunrise == null) "" else DateTimeFormatter.ofPattern(
                        "HH:mm"
                    ).format(dailyWeather.value?.sunrise)
                }"
            )
            Text(
                text = "Sunset: ${
                    if (dailyWeather.value?.sunset == null) "" else DateTimeFormatter.ofPattern(
                        "HH:mm"
                    ).format(dailyWeather.value?.sunset)
                }"
            )
            Text(text = "Forecast")
            if (forecast.value?.hourlyTemperature != null && forecast.value?.hourlyWeatherCode != null) {
                LazyColumn {
                    items(forecast.value?.hourlyTemperature?.size ?: 0) {
                        HourRow(
                            hour = it,
                            weatherCode = forecast.value?.hourlyWeatherCode?.get(it) ?: 0,
                            precipitationProbability = forecast.value?.hourlyPrecipitationProbability?.get(
                                it
                            ) ?: 0,
                            temperature = forecast.value?.hourlyTemperature?.get(it) ?: 0.0,
                            windSpeed = forecast.value?.hourlyWindSpeed?.get(it) ?: 0.0,
                            windDirection = forecast.value?.hourlyWindDirection?.get(it) ?: 0,
                            isDay = it > (dailyWeather.value?.sunrise?.hour ?: 0)
                                    && it <= (dailyWeather.value?.sunset?.hour ?: 0)
                        )
                    }
                }
            }
        }
    }
}
