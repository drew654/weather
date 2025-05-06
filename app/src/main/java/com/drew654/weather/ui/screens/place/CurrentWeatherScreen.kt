package com.drew654.weather.ui.screens.place

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.drew654.weather.models.WeatherViewModel
import com.drew654.weather.utils.getWeatherDescription
import com.drew654.weather.utils.getWeatherIconUrl
import com.drew654.weather.utils.showDouble

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrentWeatherScreen(weatherViewModel: WeatherViewModel) {
    val context = LocalContext.current
    val place = weatherViewModel.selectedPlace.collectAsState()
    val weatherForecast = weatherViewModel.weatherForecast.collectAsState()
    val showDecimal = weatherViewModel.showDecimalFlow.collectAsState(initial = false)
    val currentTemperature = weatherForecast.value?.currentTemperature!!
    val currentWeatherCode = weatherForecast.value?.currentWeatherCode!!
    val currentIsDay = weatherForecast.value?.currentIsDay!!
    val currentApparentTemperature = weatherForecast.value?.currentApparentTemperature!!
    val maxTemperature = weatherForecast.value?.dailyMaxTemperature?.get(0)!!
    val minTemperature = weatherForecast.value?.dailyMinTemperature?.get(0)!!
    val currentWindSpeed = weatherForecast.value?.currentWindSpeed!!
    val currentWindDirection = weatherForecast.value?.currentWindDirection!!
    val currentRelativeHumidity = weatherForecast.value?.currentRelativeHumidity!!
    val currentDewPoint = weatherForecast.value?.currentDewPoint!!

    Box(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .fillMaxSize()
    ) {
        if (place.value != null && weatherForecast.value != null) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(text = "Now")
                Row {
                    Row {
                        Text(
                            text = "${showDouble(currentTemperature, showDecimal.value)}°",
                            fontSize = 48.sp
                        )
                        AsyncImage(
                            model = getWeatherIconUrl(
                                weatherCode = currentWeatherCode,
                                isDay = currentIsDay
                            ),
                            contentDescription = getWeatherDescription(
                                context = context,
                                weatherCode = currentWeatherCode,
                                isDay = currentIsDay
                            ),
                            modifier = Modifier
                                .size(64.dp)
                                .align(Alignment.Bottom)
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Column {
                        Text(
                            text =
                            getWeatherDescription(
                                context = context,
                                weatherCode = currentWeatherCode,
                                isDay = currentIsDay
                            ),
                            modifier = Modifier.align(Alignment.End)
                        )
                        Text(
                            text = "Feels like ${
                                showDouble(
                                    currentApparentTemperature,
                                    showDecimal.value
                                )
                            }°"
                        )
                    }
                }
                Text(
                    text = "High: ${
                        showDouble(
                            maxTemperature,
                            showDecimal.value
                        )
                    }° • Low: ${showDouble(minTemperature, showDecimal.value)}°"
                )
                Spacer(modifier = Modifier.weight(1f))
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    WindTile(
                        windSpeed = currentWindSpeed,
                        windDirection = currentWindDirection,
                        weatherViewModel = weatherViewModel
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    HumidityTile(
                        weatherViewModel = weatherViewModel,
                        humidity = currentRelativeHumidity,
                        dewPoint = currentDewPoint,
                    )
                }
            }
        }
    }
}
