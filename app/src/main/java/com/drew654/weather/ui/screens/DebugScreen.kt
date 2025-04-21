package com.drew654.weather.ui.screens

import android.text.format.DateFormat.is24HourFormat
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TimeInput
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import com.drew654.weather.data.jsonToWeatherForecast
import com.drew654.weather.ui.components.DateInputField
import com.drew654.weather.utils.OfflineWeather.getOfflinePrecipitationProbability
import com.drew654.weather.utils.OfflineWeather.getOfflineTemperature
import com.drew654.weather.utils.OfflineWeather.getOfflineWeatherCode
import com.drew654.weather.utils.OfflineWeather.getOfflineWindDirection
import com.drew654.weather.utils.OfflineWeather.getOfflineWindSpeed
import com.drew654.weather.utils.OfflineWeather.loadWeatherForecastJson
import kotlinx.serialization.json.Json
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DebugScreen() {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val weatherForecastJson = loadWeatherForecastJson(context = context)
    val weatherForecast = weatherForecastJson?.let { json ->
        jsonToWeatherForecast(Json.decodeFromString(json))
    }
    val currentTime = LocalDateTime.now()
    val dateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm")
    val timePickerState = rememberTimePickerState(
        initialHour = currentTime.hour,
        initialMinute = currentTime.minute,
        is24Hour = is24HourFormat(context)
    )
    val dateInMillis = remember { mutableLongStateOf(System.currentTimeMillis()) }

    Box(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .fillMaxSize()
            .clickable(
                indication = null,
                interactionSource = null
            ) {
                focusManager.clearFocus()
            }
    ) {
        LazyColumn {
            if (weatherForecast != null) {
                items(1) {
                    DateInputField(dateInMillis = dateInMillis)
                    TimeInput(state = timePickerState)
                    Text(text = "Start time: ${weatherForecast.hours[0].format(dateFormatter)}")
                    Text(
                        text = "End time: ${
                            weatherForecast.hours[weatherForecast.hours.lastIndex].format(
                                dateFormatter
                            )
                        }"
                    )
                    Text(
                        text = "Temperature: ${
                            getOfflineTemperature(
                                weatherForecast,
                                LocalDateTime.ofInstant(
                                    Instant.ofEpochMilli(dateInMillis.longValue),
                                    ZoneId.systemDefault()
                                )
                                    .withHour(timePickerState.hour)
                                    .withMinute(timePickerState.minute)
                            )
                        }"
                    )
                    Text(
                        text = "Weather Code: ${
                            getOfflineWeatherCode(
                                weatherForecast,
                                LocalDateTime.ofInstant(
                                    Instant.ofEpochMilli(dateInMillis.longValue),
                                    ZoneId.systemDefault()
                                )
                                    .withHour(timePickerState.hour)
                                    .withMinute(timePickerState.minute)
                            )
                        }"
                    )
                    Text(
                        text = "Precipitation Probability: ${
                            getOfflinePrecipitationProbability(
                                weatherForecast,
                                LocalDateTime.ofInstant(
                                    Instant.ofEpochMilli(dateInMillis.longValue),
                                    ZoneId.systemDefault()
                                )
                                    .withHour(timePickerState.hour)
                                    .withMinute(timePickerState.minute)
                            )
                        }"
                    )
                    Text(
                        text = "Wind Speed: ${
                            getOfflineWindSpeed(
                                weatherForecast,
                                LocalDateTime.ofInstant(
                                    Instant.ofEpochMilli(dateInMillis.longValue),
                                    ZoneId.systemDefault()
                                )
                                    .withHour(timePickerState.hour)
                                    .withMinute(timePickerState.minute)
                            )
                        }"
                    )
                    Text(
                        text = "Wind Direction: ${
                            getOfflineWindDirection(
                                weatherForecast,
                                LocalDateTime.ofInstant(
                                    Instant.ofEpochMilli(dateInMillis.longValue),
                                    ZoneId.systemDefault()
                                )
                                    .withHour(timePickerState.hour)
                                    .withMinute(timePickerState.minute)
                            )
                        }"
                    )
                }
            }
        }
    }
}
