package com.drew654.weather.ui.screens.dailyForecast

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import com.drew654.weather.models.WeatherViewModel

@Composable
fun DailyForecastScreen(
    weatherViewModel: WeatherViewModel
) {
    val dailyForecast = weatherViewModel.dailyForecast.collectAsState()

    Box(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .fillMaxSize()
    ) {
        LazyColumn {
            items(dailyForecast.value?.day?.size ?: 0) {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = dailyForecast.value?.day?.get(it).toString())
                    Text(text = "Max: ${dailyForecast.value?.maxTemperature?.get(it)}")
                    Text(text = "Min: ${dailyForecast.value?.minTemperature?.get(it)}")
                    Text(
                        text = "Sunrise: ${
                            dailyForecast.value?.dailySunrise?.get(it)?.hour
                        }:${dailyForecast.value?.dailySunrise?.get(it)?.minute}"
                    )
                    Text(
                        text = "Sunset: ${
                            dailyForecast.value?.dailySunset?.get(it)?.hour
                        }:${dailyForecast.value?.dailySunset?.get(it)?.minute}"
                    )
                    Text(text = "Weather Code: ${dailyForecast.value?.dailyWeatherCode?.get(it)}")
                    Text(
                        text = "Precipitation Probability: ${
                            dailyForecast.value?.dailyPrecipitationProbabilityMax?.get(it)
                        }%"
                    )
                    Text(text = "Wind Speed: ${dailyForecast.value?.dailyWindSpeedMax?.get(it)}")
                    Text(text = "---------------------------------")
                }
            }
        }
    }
}
