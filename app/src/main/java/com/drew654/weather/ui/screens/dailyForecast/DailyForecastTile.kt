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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.drew654.weather.R
import com.drew654.weather.utils.getWeatherDescription
import com.drew654.weather.utils.getWeatherIconUrl

@Composable
fun DailyForecastTile(
    context: Context,
    onClick: () -> Unit,
    isSelected: () -> Boolean,
    dayOfWeek: String,
    dayOfMonth: Int,
    maxTemperature: Double,
    minTemperature: Double,
    weatherCode: Int,
    precipitationProbability: Int,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(MaterialTheme.shapes.medium)
            .clickable { onClick() }
            .background(if (isSelected()) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Text(
            text = "$dayOfWeek $dayOfMonth"
        )
        Text(
            text = "$maxTemperature°",
            fontWeight = FontWeight.Bold
        )
        Text(text = "$minTemperature°")
        AsyncImage(
            model = getWeatherIconUrl(
                weatherCode = weatherCode,
                isDay = true
            ),
            contentDescription = getWeatherDescription(
                context = context,
                weatherCode = weatherCode,
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
                text = "$precipitationProbability%"
            )
        }
    }
}
