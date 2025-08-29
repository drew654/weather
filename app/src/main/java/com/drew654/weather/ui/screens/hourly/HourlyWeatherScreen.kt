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
import com.drew654.weather.models.MeasurementUnit
import com.drew654.weather.models.MeasurementUnit.Companion.getObjectFromDataName
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
    val hours = weatherForecast.value?.hours!!
    val hourlyWeatherCode = weatherForecast.value?.hourlyWeatherCode!!
    val hourlyPrecipitationProbability = weatherForecast.value?.hourlyPrecipitationProbability!!
    val hourlyTemperature = weatherForecast.value?.hourlyTemperature!!
    val hourlyWindSpeed = weatherForecast.value?.hourlyWindSpeed!!
    val hourlyWindDirection = weatherForecast.value?.hourlyWindDirection!!
    val dailySunrise = weatherForecast.value?.dailySunrise!!
    val dailySunset = weatherForecast.value?.dailySunset!!
    val windUnit =
        weatherViewModel.windSpeedUnitFlow.collectAsState(initial = MeasurementUnit.Mph.dataName)
    val showDecimal = weatherViewModel.showDecimalFlow.collectAsState(initial = false)
    val temperatureUnit =
        weatherViewModel.temperatureUnitFlow.collectAsState(initial = MeasurementUnit.Fahrenheit.dataName)

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
                        DateHeading(index = currentHour, hours = hours)
                    }
                }
                items(hours.size) {
                    if (it % 24 == 0 && it != 0) {
                        DateHeading(index = it, hours = hours)
                    }
                    if (it > currentHour) {
                        HourRow(
                            hour = formatHour(hours[it], is24HourFormat(context)),
                            weatherCode = hourlyWeatherCode[it],
                            precipitationProbability = hourlyPrecipitationProbability[it],
                            temperature = hourlyTemperature[it],
                            windSpeed = hourlyWindSpeed[it],
                            windDirection = hourlyWindDirection[it],
                            isDay = hourIsDay(
                                hour = hours[it].hour,
                                sunrise = dailySunrise[0],
                                sunset = dailySunset[0]
                            ),
                            windUnit = getObjectFromDataName(windUnit.value)!!,
                            showDecimal = showDecimal.value,
                            temperatureUnit = getObjectFromDataName(temperatureUnit.value)!!
                        )
                    }
                }
            }
        }
    }
}
