package com.drew654.weather.ui.screens.place

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
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
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(text = "$hour")
        Text(text = "$weatherCode")
        Text(text = "$precipitationProbability%")
        Text(text = "$temperature")
        Text(text = "$windSpeed mph")
        Text(text = degToHdg(windDirection))
        Icon(
            painter = painterResource(id = R.drawable.baseline_arrow_right_alt_24),
            contentDescription = "Add Place",
            modifier = Modifier.rotate(windDirection.toFloat() - 90)
        )
    }
}
