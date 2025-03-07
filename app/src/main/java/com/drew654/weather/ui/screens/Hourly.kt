package com.drew654.weather.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.drew654.weather.models.WeatherViewModel
import com.drew654.weather.ui.screens.place.HourRow
import com.drew654.weather.utils.capitalizeWord
import java.time.LocalDateTime


@Composable
fun HourlyScreen(
    weatherViewModel: WeatherViewModel
) {
    val forecast = weatherViewModel.forecast.collectAsState()
    val currentHour = LocalDateTime.now().hour

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        if (forecast.value?.hourlyTemperature != null && forecast.value?.hourlyWeatherCode != null) {
            LazyColumn {
                items(forecast.value?.hourlyTemperature?.size!!) {
                    if (it % 24 == 0 && it != 0) {
                        val dayOfWeek = forecast.value?.hour?.get(it)?.dayOfWeek.toString()
                            .capitalizeWord()
                        val month =
                            forecast.value?.hour?.get(it)?.month.toString().capitalizeWord()
                        val day = forecast.value?.hour?.get(it)?.dayOfMonth.toString()
                            .capitalizeWord()
                        Text(
                            text = "$dayOfWeek, $month $day",
                            fontSize = 20.sp,
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                        )
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
                            isDay = true
                        )
                    }
                }
            }
        }
    }
}
