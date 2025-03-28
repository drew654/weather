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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrentWeatherScreen(weatherViewModel: WeatherViewModel) {
    val context = LocalContext.current
    val place = weatherViewModel.selectedPlace.collectAsState()
    val currentWeather = weatherViewModel.currentWeather.collectAsState()
    val forecast = weatherViewModel.forecast.collectAsState()
    val dailyForecast = weatherViewModel.dailyForecast.collectAsState()

    Box(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .fillMaxSize()
    ) {
        if (place.value != null && currentWeather.value != null && forecast.value != null && dailyForecast.value != null) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(text = "Now")
                Row {
                    Row {
                        Text(text = "${currentWeather.value?.temperature}°", fontSize = 48.sp)
                        AsyncImage(
                            model = getWeatherIconUrl(
                                weatherCode = currentWeather.value?.weatherCode ?: 0,
                                isDay = currentWeather.value?.isDay!!
                            ),
                            contentDescription = getWeatherDescription(
                                context = context,
                                weatherCode = currentWeather.value?.weatherCode!!,
                                isDay = currentWeather.value?.isDay!!
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
                                weatherCode = currentWeather.value?.weatherCode!!,
                                isDay = currentWeather.value?.isDay!!
                            ),
                            modifier = Modifier.align(Alignment.End)
                        )
                        Text(text = "Feels like ${currentWeather.value?.apparentTemperature}°")
                    }
                }
                Text(text = "High: ${dailyForecast.value?.dailyMaxTemperature?.get(0)}° • Low: ${dailyForecast.value?.dailyMinTemperature?.get(0)}°")
                Spacer(modifier = Modifier.weight(1f))
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    WindTile(
                        windSpeed = currentWeather.value?.windSpeed!!,
                        windDirection = currentWeather.value?.windDirection!!,
                        weatherViewModel = weatherViewModel
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    HumidityTile(
                        humidity = currentWeather.value?.relativeHumidity!!,
                        dewPoint = currentWeather.value?.dewPoint!!,
                    )
                }
            }
        }
    }
}
