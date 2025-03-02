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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.drew654.weather.R

fun degToHdg(deg: Int): String {
    val directions = listOf(
        "N", "NNE", "NE", "ENE", "E", "ESE", "SE", "SSE",
        "S", "SSW", "SW", "WSW", "W", "WNW", "NW", "NNW"
    )
    val index = ((deg / 22.5) + 0.5).toInt() % 16
    return directions[index]
}

@Composable
fun HourRow(
    hour: Int,
    weatherCode: Int,
    precipitationProbability: Int,
    temperature: Double,
    windSpeed: Double,
    windDirection: Int
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp

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
            Text(text = "$hour")
        }
        Row(
            modifier = Modifier
                .padding(vertical = 8.dp)
                .fillMaxWidth()
        ) {
            Spacer(Modifier.width(screenWidth * 0.2f))
            Text(text = "$weatherCode")
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
            Text(text = "$temperature°")
        }
        Row(
            modifier = Modifier
                .padding(vertical = 8.dp)
                .fillMaxWidth()
        ) {
            Spacer(Modifier.width(screenWidth * 0.6f))
            Icon(
                painter = painterResource(id = R.drawable.baseline_arrow_right_alt_24),
                contentDescription = "Add Place",
                modifier = Modifier
                    .rotate(windDirection.toFloat() + 90)
                    .align(Alignment.CenterVertically)
            )
            Text(text = "$windSpeed mph ${degToHdg(windDirection)}")
        }
    }
}
