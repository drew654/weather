package com.drew654.weather.ui.screens.hourly

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
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
    weatherViewModel: WeatherViewModel,
    hourlyListState: LazyListState,
) {
    val hourlyForecast = weatherViewModel.hourlyForecast.collectAsState()
    val dailyForecast = weatherViewModel.dailyForecast.collectAsState()
    val currentHour = LocalDateTime.now().hour

    Box(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .fillMaxSize()
    ) {
        if (hourlyForecast.value != null) {
            LazyColumn(
                state = hourlyListState
            ) {
                items(1) {
                    Spacer(modifier = Modifier.height(12.dp))
                }
                if (currentHour != 23) {
                    items(1) {
                        DateHeading(hour = currentHour, hourlyForecast = hourlyForecast.value!!)
                    }
                }
                items(hourlyForecast.value?.hourlyTemperature?.size ?: 0) {
                    if (it % 24 == 0 && it != 0) {
                        DateHeading(hour = it, hourlyForecast = hourlyForecast.value!!)
                    }
                    if (it > currentHour) {
                        HourRow(
                            weatherViewModel = weatherViewModel,
                            hour = hourlyForecast.value?.hour?.get(it)?.hour!!,
                            weatherCode = hourlyForecast.value?.hourlyWeatherCode?.get(it)!!,
                            precipitationProbability = hourlyForecast.value?.hourlyPrecipitationProbability?.get(
                                it
                            )!!,
                            temperature = hourlyForecast.value?.hourlyTemperature?.get(it)!!,
                            windSpeed = hourlyForecast.value?.hourlyWindSpeed?.get(it)!!,
                            windDirection = hourlyForecast.value?.hourlyWindDirection?.get(it)!!,
                            isDay = hourIsDay(
                                hour = hourlyForecast.value?.hour?.get(it)?.hour!!,
                                sunrise = dailyForecast.value?.dailySunrise?.get(0)!!,
                                sunset = dailyForecast.value?.dailySunset?.get(0)!!
                            )
                        )
                    }
                }
            }
        }
    }
}
