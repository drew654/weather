package com.drew654.weather.ui.screens.place

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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

@Composable
fun PlaceScreen(weatherViewModel: WeatherViewModel) {
    val context = LocalContext.current
    val place = weatherViewModel.selectedPlace.collectAsState()
    val currentWeather = weatherViewModel.currentWeather.collectAsState()
    val forecast = weatherViewModel.forecast.collectAsState()
    val dailyWeather = weatherViewModel.dailyWeather.collectAsState()

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        if (place.value != null && currentWeather.value != null && forecast.value != null && dailyWeather.value != null) {
            Column {
                Text(
                    text = place.value?.name!!,
                    fontSize = 24.sp,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp)
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
                    Text(text = "High: ${dailyWeather.value?.maxTemperature}° • Low: ${dailyWeather.value?.minTemperature}°")
                }
            }
        }
    }
}
