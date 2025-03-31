package com.drew654.weather.ui.screens.place

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.drew654.weather.R
import com.drew654.weather.models.MeasurementUnit
import com.drew654.weather.models.MeasurementUnit.Companion.getDisplayNameFromDataName
import com.drew654.weather.models.WeatherViewModel
import com.drew654.weather.utils.degToHdg
import com.drew654.weather.utils.getBeaufortDescription
import com.drew654.weather.utils.showDouble

@Composable
fun WindTile(
    windSpeed: Double,
    windDirection: Int,
    weatherViewModel: WeatherViewModel,
    modifier: Modifier = Modifier
) {
    val windSpeedUnit =
        weatherViewModel.windSpeedUnitFlow.collectAsState(initial = MeasurementUnit.Mph.dataName)
    val showDecimal = weatherViewModel.showDecimalFlow.collectAsState(initial = false)

    Box(
        modifier = modifier
            .border(1.dp, MaterialTheme.colorScheme.onBackground, RoundedCornerShape(8.dp))
            .padding(8.dp)
    ) {
        Text(text = "Wind")
        Column {
            Spacer(modifier = Modifier.height(24.dp))
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column(
                    verticalArrangement = Arrangement.Bottom
                ) {
                    Row {
                        Text(text = showDouble(windSpeed, showDecimal.value), fontSize = 24.sp)
                        Text(
                            text = " ${getDisplayNameFromDataName(windSpeedUnit.value)}",
                            modifier = Modifier.align(Alignment.Bottom)
                        )
                    }
                    Text(
                        text = getBeaufortDescription(windSpeed, windSpeedUnit.value),
                        fontSize = 12.sp
                    )
                }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = degToHdg(windDirection))
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_arrow_right_alt_24),
                        contentDescription = "Wind direction",
                        modifier = Modifier
                            .rotate(windDirection.toFloat() + 90)
                            .size(64.dp)
                    )
                }
            }
        }
    }
}
