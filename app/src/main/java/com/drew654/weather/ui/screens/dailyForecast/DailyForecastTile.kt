package com.drew654.weather.ui.screens.dailyForecast

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.drew654.weather.R
import com.drew654.weather.models.DayForecast
import com.drew654.weather.models.MeasurementUnit
import com.drew654.weather.models.WeatherViewModel
import com.drew654.weather.utils.getWeatherDescription
import com.drew654.weather.utils.getWeatherIconUrl
import com.drew654.weather.utils.showTemperature
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun DailyForecastTile(
    weatherViewModel: WeatherViewModel,
    context: Context,
    onClick: () -> Unit,
    isSelected: () -> Boolean,
    dayForecast: DayForecast
) {
    val showDecimal = weatherViewModel.showDecimalFlow.collectAsState(initial = false)
    val temperatureUnit =
        weatherViewModel.temperatureUnitFlow.collectAsState(initial = MeasurementUnit.Fahrenheit.dataName)
    val dayOfWeek = dayForecast.date.dayOfWeek?.getDisplayName(
        TextStyle.SHORT,
        Locale.getDefault()
    ).toString()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(MaterialTheme.shapes.medium)
            .clickable { onClick() }
            .background(if (isSelected()) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Text(
            text = "$dayOfWeek ${dayForecast.date.dayOfMonth}"
        )
        Text(
            text = "${
                showTemperature(
                    dayForecast.maxTemperature,
                    temperatureUnit.value,
                    showDecimal.value
                )
            }°",
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "${
                showTemperature(
                    dayForecast.minTemperature,
                    temperatureUnit.value,
                    showDecimal.value
                )
            }°"
        )
        AsyncImage(
            model = getWeatherIconUrl(
                weatherCode = dayForecast.weatherCode,
                isDay = true
            ),
            contentDescription = getWeatherDescription(
                context = context,
                weatherCode = dayForecast.weatherCode,
                isDay = true
            ),
            modifier = Modifier.size(48.dp)
        )
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.baseline_water_drop_24),
                contentDescription = "Precipitation",
                modifier = Modifier
                    .size(16.dp)
                    .padding(end = 2.dp)
                    .align(Alignment.CenterVertically)
            )
            Text(
                text = "${dayForecast.precipitationProbabilityMax}%"
            )
        }
    }
}
