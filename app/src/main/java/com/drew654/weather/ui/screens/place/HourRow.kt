package com.drew654.weather.ui.screens.place

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.drew654.weather.R
import com.drew654.weather.models.MeasurementUnit
import com.drew654.weather.models.MeasurementUnit.Companion.getDisplayNameFromDataName
import com.drew654.weather.models.WeatherViewModel
import com.drew654.weather.utils.degToHdg
import com.drew654.weather.utils.getWeatherDescription
import com.drew654.weather.utils.getWeatherIconUrl
import com.drew654.weather.utils.showTemperature
import com.drew654.weather.utils.showWindSpeed

@Composable
fun HourRow(
    weatherViewModel: WeatherViewModel,
    hour: String,
    weatherCode: Int,
    precipitationProbability: Int,
    temperature: Double,
    windSpeed: Double,
    windDirection: Int,
    isDay: Boolean
) {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val windUnit =
        weatherViewModel.windSpeedUnitFlow.collectAsState(initial = MeasurementUnit.Mph.dataName)
    val showDecimal = weatherViewModel.showDecimalFlow.collectAsState(initial = false)
    val temperatureUnit =
        weatherViewModel.temperatureUnitFlow.collectAsState(initial = MeasurementUnit.Fahrenheit.dataName)

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(vertical = 8.dp)
                .fillMaxWidth()
        ) {
            Spacer(Modifier.width(screenWidth * 0.05f))
            Text(text = hour)
        }
        Row(
            modifier = Modifier
                .padding(vertical = 8.dp)
                .fillMaxWidth()
        ) {
            Spacer(Modifier.width(screenWidth * 0.2f))
            AsyncImage(
                model = getWeatherIconUrl(weatherCode = weatherCode, isDay = isDay),
                contentDescription = getWeatherDescription(
                    context = context,
                    weatherCode = weatherCode,
                    isDay = isDay
                ),
                modifier = Modifier.size(32.dp)
            )
        }
        Row(
            modifier = Modifier
                .padding(vertical = 8.dp)
                .fillMaxWidth()
        ) {
            Spacer(Modifier.width(screenWidth * 0.3f))
            Icon(
                painter = painterResource(id = R.drawable.baseline_water_drop_24),
                contentDescription = "Precipitation",
                modifier = Modifier
                    .size(12.dp)
                    .align(Alignment.CenterVertically)
            )
            Spacer(Modifier.width(2.dp))
            Text(text = "$precipitationProbability%")
        }
        Row(
            modifier = Modifier
                .padding(vertical = 8.dp)
                .fillMaxWidth()
        ) {
            Spacer(Modifier.width(screenWidth * 0.45f))
            Text(
                text = "${
                    showTemperature(temperature, temperatureUnit.value, showDecimal.value)
                }°"
            )
        }
        Row(
            modifier = Modifier
                .padding(vertical = 8.dp)
                .fillMaxWidth()
        ) {
            Spacer(Modifier.width(screenWidth * 0.6f))
            Icon(
                painter = painterResource(id = R.drawable.baseline_arrow_right_alt_24),
                contentDescription = "Wind direction",
                modifier = Modifier
                    .rotate(windDirection.toFloat() + 90)
                    .align(Alignment.CenterVertically)
            )
            Text(
                text = "${showWindSpeed(windSpeed, windUnit.value, showDecimal.value)} ${
                    getDisplayNameFromDataName(
                        windUnit.value
                    )
                } ${
                    degToHdg(windDirection)
                }"
            )
        }
    }
}
