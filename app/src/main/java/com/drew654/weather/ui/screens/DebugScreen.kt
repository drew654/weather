package com.drew654.weather.ui.screens

import android.text.format.DateFormat.is24HourFormat
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import com.drew654.weather.data.jsonToWeatherForecast
import com.drew654.weather.ui.components.DateInputField
import com.drew654.weather.ui.components.DropdownMenu
import com.drew654.weather.ui.components.TimeInputField
import com.drew654.weather.utils.OfflineWeather.getOfflineApparentTemperature
import com.drew654.weather.utils.OfflineWeather.getOfflineDewPoint
import com.drew654.weather.utils.OfflineWeather.getOfflineMaxTemperature
import com.drew654.weather.utils.OfflineWeather.getOfflineMinTemperature
import com.drew654.weather.utils.OfflineWeather.getOfflinePrecipitationProbability
import com.drew654.weather.utils.OfflineWeather.getOfflineRelativeHumidity
import com.drew654.weather.utils.OfflineWeather.getOfflineTemperature
import com.drew654.weather.utils.OfflineWeather.getOfflineWeatherCode
import com.drew654.weather.utils.OfflineWeather.getOfflineWeatherDays
import com.drew654.weather.utils.OfflineWeather.getOfflineWindDirection
import com.drew654.weather.utils.OfflineWeather.getOfflineWindSpeed
import com.drew654.weather.utils.OfflineWeather.loadWeatherForecastJson
import com.drew654.weather.utils.formatTime
import kotlinx.serialization.json.Json
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DebugScreen() {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val fileName = remember { mutableStateOf("") }
    val jsonFileNames =
        context.filesDir.listFiles()?.map { it.name }?.filter { it.endsWith(".json") }
            ?: emptyList()
    val dropdownMenuIsExpanded = remember { mutableStateOf(false) }
    val weatherForecastJson = loadWeatherForecastJson(context = context, fileName = fileName.value)
    val weatherForecast = weatherForecastJson?.let { json ->
        jsonToWeatherForecast(Json.decodeFromString(json))
    }
    val dateTimeFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm")
    val timePickerState = rememberTimePickerState(
        initialHour = 12,
        initialMinute = 0,
        is24Hour = is24HourFormat(context)
    )
    val dateInMillis = remember { mutableLongStateOf(System.currentTimeMillis()) }
    val currentTime = LocalDateTime.now()
    val dateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy")

    LaunchedEffect(jsonFileNames) {
        fileName.value = jsonFileNames.firstOrNull() ?: ""
    }

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
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = 16.dp)
        ) {
            if (weatherForecast != null) {
                item {
                    DropdownMenu(
                        selectedValue = fileName.value,
                        options = jsonFileNames,
                        label = "Select a file",
                        onValueChange = {
                            fileName.value = it
                            dropdownMenuIsExpanded.value = false
                        }
                    )
                }
                items(1) {
                    DateInputField(dateInMillis = dateInMillis)
                    TimeInputField(
                        timePickerState = timePickerState
                    )
                    Text(text = "Start time: ${weatherForecast.hours[0].format(dateTimeFormatter)}")
                    Text(
                        text = "End time: ${
                            weatherForecast.hours[weatherForecast.hours.lastIndex].format(
                                dateTimeFormatter
                            )
                        }"
                    )
                    Text(
                        text = "Temperature: ${
                            getOfflineTemperature(
                                weatherForecast,
                                currentTime.withHour(timePickerState.hour)
                                    .withMinute(timePickerState.minute)
                            )
                        }"
                    )
                    Text(
                        text = "Relative Humidity: ${
                            getOfflineRelativeHumidity(
                                weatherForecast,
                                currentTime.withHour(timePickerState.hour)
                                    .withMinute(timePickerState.minute)
                            )
                        }"
                    )
                    Text(
                        text = "Dew Point: ${
                            getOfflineDewPoint(
                                weatherForecast,
                                currentTime.withHour(timePickerState.hour)
                                    .withMinute(timePickerState.minute)
                            )
                        }"
                    )
                    Text(
                        text = "Apparent Temperature: ${
                            getOfflineApparentTemperature(
                                weatherForecast,
                                currentTime.withHour(timePickerState.hour)
                                    .withMinute(timePickerState.minute)
                            )
                        }"
                    )
                    Text(
                        text = "Weather Code: ${
                            getOfflineWeatherCode(
                                weatherForecast,
                                currentTime.withHour(timePickerState.hour)
                                    .withMinute(timePickerState.minute)
                            )
                        }"
                    )
                    Text(
                        text = "Precipitation Probability: ${
                            getOfflinePrecipitationProbability(
                                weatherForecast,
                                currentTime.withHour(timePickerState.hour)
                                    .withMinute(timePickerState.minute)
                            )
                        }"
                    )
                    Text(
                        text = "Wind Speed: ${
                            getOfflineWindSpeed(
                                weatherForecast,
                                currentTime.withHour(timePickerState.hour)
                                    .withMinute(timePickerState.minute)
                            )
                        }"
                    )
                    Text(
                        text = "Wind Direction: ${
                            getOfflineWindDirection(
                                weatherForecast,
                                currentTime.withHour(timePickerState.hour)
                                    .withMinute(timePickerState.minute)
                            )
                        }"
                    )
                    Text(
                        text = "Max Temperature: ${
                            getOfflineMaxTemperature(
                                weatherForecast,
                                currentTime.withHour(timePickerState.hour)
                                    .withMinute(timePickerState.minute)
                            )
                        }"
                    )
                    Text(
                        text = "Min Temperature: ${
                            getOfflineMinTemperature(
                                weatherForecast,
                                currentTime.withHour(timePickerState.hour)
                                    .withMinute(timePickerState.minute)
                            )
                        }"
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
                itemsIndexed(getOfflineWeatherDays(weatherForecast, currentTime)) { index, day ->
                    val originalIndex = weatherForecast.days.indexOf(day)
                    Text(
                        weatherForecast.days[originalIndex].format(
                            dateFormatter
                        )
                    )
                    Text(
                        "Max Temperature: ${
                            weatherForecast.dailyMaxTemperature[originalIndex]
                        }"
                    )
                    Text(
                        "Min Temperature: ${
                            weatherForecast.dailyMinTemperature[originalIndex]
                        }"
                    )
                    Text(
                        "Weather Code: ${
                            weatherForecast.dailyWeatherCode[originalIndex]
                        }"
                    )
                    Text(
                        "Precipitation Probability: ${
                            weatherForecast.dailyPrecipitationProbabilityMax[originalIndex]
                        }%"
                    )
                    Text(
                        "Wind Speed: ${
                            weatherForecast.dailyWindSpeedMax[originalIndex]
                        }"
                    )
                    Text(
                        "Wind Direction: ${
                            weatherForecast.dailyWindDirectionDominant[originalIndex]
                        }"
                    )
                    Text(
                        "UV Index: ${
                            weatherForecast.dailyUvIndexMax[originalIndex]
                        }"
                    )
                    Text(
                        "Sunrise: ${
                            formatTime(
                                weatherForecast.dailySunrise[originalIndex],
                                is24HourFormat(context)
                            )
                        }"
                    )
                    Text(
                        "Sunset: ${
                            formatTime(
                                weatherForecast.dailySunset[originalIndex],
                                is24HourFormat(context)
                            )
                        }"
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}
