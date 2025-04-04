package com.drew654.weather.ui.screens.hourly

import android.text.format.DateFormat.is24HourFormat
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.drew654.weather.models.WeatherViewModel
import com.drew654.weather.ui.screens.place.HourRow
import com.drew654.weather.utils.formatHour
import com.drew654.weather.utils.hourIsDay
import java.time.LocalDateTime


@Composable
fun HourlyWeatherScreen(
    weatherViewModel: WeatherViewModel,
    hourlyListState: LazyListState,
) {
    val weatherForecast = weatherViewModel.weatherForecast.collectAsState()
    val currentHour = LocalDateTime.now().hour
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .fillMaxSize()
    ) {
        if (weatherForecast.value != null) {
            LazyColumn(
                state = hourlyListState
            ) {
                items(1) {
                    Spacer(modifier = Modifier.height(12.dp))
                }
                if (currentHour != 23) {
                    items(1) {
                        DateHeading(index = currentHour, hours = weatherForecast.value!!.hours)
                    }
                }
                items(weatherForecast.value?.hourlyTemperature?.size ?: 0) {
                    if (it % 24 == 0 && it != 0) {
                        DateHeading(index = it, hours = weatherForecast.value!!.hours)
                    }
                    if (it > currentHour) {
                        HourRow(
                            weatherViewModel = weatherViewModel,
                            hour = formatHour(weatherForecast.value?.hours?.get(it), is24HourFormat(context)),
                            weatherCode = weatherForecast.value?.hourlyWeatherCode?.get(it)!!,
                            precipitationProbability = weatherForecast.value
                                ?.hourlyPrecipitationProbability?.get(it)!!,
                            temperature = weatherForecast.value?.hourlyTemperature?.get(it)!!,
                            windSpeed = weatherForecast.value?.hourlyWindSpeed?.get(it)!!,
                            windDirection = weatherForecast.value?.hourlyWindDirection?.get(it)!!,
                            isDay = hourIsDay(
                                hour = weatherForecast.value?.hours?.get(it)?.hour!!,
                                sunrise = weatherForecast.value?.dailySunrise?.get(0)!!,
                                sunset = weatherForecast.value?.dailySunset?.get(0)!!
                            )
                        )
                    }
                }
            }
        }
    }
}
