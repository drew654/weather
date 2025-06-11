package com.drew654.weather.ui.screens.place

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.drew654.weather.models.MeasurementUnit
import com.drew654.weather.models.WeatherViewModel
import com.drew654.weather.ui.components.VerticalProgressBar
import com.drew654.weather.utils.showTemperature

@Composable
fun HumidityTile(
    weatherViewModel: WeatherViewModel,
    humidity: Int,
    dewPoint: Double,
    modifier: Modifier = Modifier
) {
    val showDouble = weatherViewModel.showDecimalFlow.collectAsState(initial = false)
    val temperatureUnit =
        weatherViewModel.temperatureUnitFlow.collectAsState(initial = MeasurementUnit.Fahrenheit.dataName)

    Box(
        modifier = modifier
            .border(1.dp, MaterialTheme.colorScheme.onBackground, RoundedCornerShape(8.dp))
            .padding(8.dp)
    ) {
        Text(text = "Humidity")
        Column {
            Spacer(modifier = Modifier.height(32.dp))
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column(
                    verticalArrangement = Arrangement.Bottom
                ) {
                    Row {
                        Text(text = "$humidity", fontSize = 24.sp)
                        Text(text = "%", modifier = Modifier.align(Alignment.Bottom))
                    }
                    Text(text = "Dew point")
                    Text(
                        text = "${
                            showTemperature(
                                dewPoint,
                                temperatureUnit.value,
                                showDouble.value
                            )
                        }°"
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    VerticalProgressBar(
                        progress = humidity
                    )
                }
            }
        }
    }
}
