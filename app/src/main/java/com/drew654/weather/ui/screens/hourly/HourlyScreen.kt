package com.drew654.weather.ui.screens.hourly

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import com.drew654.weather.models.WeatherViewModel
import com.drew654.weather.ui.screens.place.HourRow
import com.drew654.weather.utils.hourIsDay
import java.time.LocalDateTime


@Composable
fun HourlyScreen(
    weatherViewModel: WeatherViewModel
) {
    val forecast = weatherViewModel.forecast.collectAsState()
    val dailyWeather = weatherViewModel.dailyWeather.collectAsState()
    val currentHour = LocalDateTime.now().hour

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        if (forecast.value != null) {
            LazyColumn {
                items(1) {
                    DateHeading(hour = currentHour, forecast = forecast.value!!)
                }
                items(forecast.value?.hourlyTemperature?.size!!) {
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
