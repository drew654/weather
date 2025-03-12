package com.drew654.weather.ui.screens.hourly

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.drew654.weather.models.WeatherViewModel
import com.drew654.weather.ui.screens.place.HourRow
import com.drew654.weather.utils.hourIsDay
import java.time.LocalDateTime


@Composable
fun HourlyWeatherScreen(
    weatherViewModel: WeatherViewModel
) {
    val forecast = weatherViewModel.forecast.collectAsState()
    val dailyWeather = weatherViewModel.dailyWeather.collectAsState()
    val currentHour = LocalDateTime.now().hour

    Box(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .fillMaxSize()
    ) {
        if (forecast.value != null) {
            LazyColumn {
                items(1) {
                    Spacer(modifier = Modifier.height(12.dp))
                }
                if (currentHour != 23) {
                    items(1) {
                        DateHeading(hour = currentHour, forecast = forecast.value!!)
                    }
                }
                items(forecast.value?.hourlyTemperature?.size ?: 0) {
                    if (it % 24 == 0 && it != 0) {
                        DateHeading(hour = it, forecast = forecast.value!!)
                    }
                    if (it > currentHour) {
                        HourRow(
                            hour = forecast.value?.hour?.get(it)?.hour!!,
                            weatherCode = forecast.value?.hourlyWeatherCode?.get(it)!!,
                            precipitationProbability = forecast.value?.hourlyPrecipitationProbability?.get(
                                it
                            )!!,
                            temperature = forecast.value?.hourlyTemperature?.get(it)!!,
                            windSpeed = forecast.value?.hourlyWindSpeed?.get(it)!!,
                            windDirection = forecast.value?.hourlyWindDirection?.get(it)!!,
                            isDay = hourIsDay(
                                hour = forecast.value?.hour?.get(it)?.hour!!,
                                sunrise = dailyWeather.value?.sunrise!!,
                                sunset = dailyWeather.value?.sunset!!
                            )
                        )
                    }
                }
            }
        }
    }
}
