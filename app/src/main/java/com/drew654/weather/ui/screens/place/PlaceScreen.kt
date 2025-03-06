package com.drew654.weather.ui.screens.place

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.drew654.weather.models.WeatherViewModel
import com.drew654.weather.utils.capitalizeWord
import com.drew654.weather.utils.getWeatherDescription
import com.drew654.weather.utils.getWeatherIconUrl
import java.time.LocalDateTime

@Composable
fun PlaceScreen(id: String, weatherViewModel: WeatherViewModel) {
    val context = LocalContext.current
    val place = weatherViewModel.getPlace(id)
    val currentWeather = weatherViewModel.currentWeather.collectAsState()
    val forecast = weatherViewModel.forecast.collectAsState()
    val dailyWeather = weatherViewModel.dailyWeather.collectAsState()
    val currentHour = LocalDateTime.now().hour

    LaunchedEffect(place) {
        if (place != null) {
            weatherViewModel.fetchCurrentWeather(place)
            weatherViewModel.fetchForecast(place)
            weatherViewModel.fetchDailyWeather(place)
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        if (place != null && currentWeather.value != null && forecast.value != null && dailyWeather.value != null) {
            Column {
                Text(
                    text = place.name,
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
                Spacer(modifier = Modifier.height(16.dp))
                if (forecast.value?.hourlyTemperature != null && forecast.value?.hourlyWeatherCode != null) {
                    LazyColumn {
                        items(forecast.value?.hourlyTemperature?.size!!) {
                            if (it % 24 == 0) {
                                val dayOfWeek = forecast.value?.hour?.get(it)?.dayOfWeek.toString()
                                    .capitalizeWord()
                                val month =
                                    forecast.value?.hour?.get(it)?.month.toString().capitalizeWord()
                                val day = forecast.value?.hour?.get(it)?.dayOfMonth.toString()
                                    .capitalizeWord()
                                Text(
                                    text = "$dayOfWeek, $month $day",
                                    fontSize = 20.sp,
                                    modifier = Modifier.padding(horizontal = 20.dp)
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
    }
}
