package com.drew654.weather.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.drew654.weather.data.jsonToWeatherForecast
import com.drew654.weather.utils.getOfflinePrecipitationProbability
import com.drew654.weather.utils.getOfflineTemperature
import com.drew654.weather.utils.getOfflineWeatherCode
import com.drew654.weather.utils.getOfflineWindDirection
import com.drew654.weather.utils.getOfflineWindSpeed
import com.drew654.weather.utils.loadWeatherForecastJson
import kotlinx.serialization.json.Json
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun OfflineWeatherDebugScreen() {
    val context = LocalContext.current
    val weatherForecastJson = loadWeatherForecastJson(context = context)
    val weatherForecast = weatherForecastJson?.let { json ->
        jsonToWeatherForecast(Json.decodeFromString(json))
    }
    val currentTime = LocalDateTime.now()
    val dateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm")

    Box(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .fillMaxSize()
    ) {
        LazyColumn {
            if (weatherForecast != null) {
                items(1) {
                    Text(text = "Start time: ${weatherForecast.hours[0].format(dateFormatter)}")
                    Text(text = "End time: ${weatherForecast.hours[weatherForecast.hours.lastIndex].format(dateFormatter)}")
                    Text(
                        text = "Current Temperature: ${
                            getOfflineTemperature(
                                weatherForecast,
                                currentTime
                            )
                        }"
                    )
                    Text(
                        text = "Current Weather Code: ${
                            getOfflineWeatherCode(
                                weatherForecast,
                                currentTime
                            )
                        }"
                    )
                    Text(
                        text = "Current Precipitation Probability: ${
                            getOfflinePrecipitationProbability(
                                weatherForecast,
                                currentTime
                            )
                        }"
                    )
                    Text(
                        text = "Current Wind Speed: ${
                            getOfflineWindSpeed(
                                weatherForecast,
                                currentTime
                            )
                        }"
                    )
                    Text(
                        text = "Current Wind Direction: ${
                            getOfflineWindDirection(
                                weatherForecast,
                                currentTime
                            )
                        }"
                    )
                }
            }
        }
    }
}
