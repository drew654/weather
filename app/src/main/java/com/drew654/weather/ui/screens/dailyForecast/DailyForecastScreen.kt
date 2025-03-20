package com.drew654.weather.ui.screens.dailyForecast

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.drew654.weather.R
import com.drew654.weather.models.WeatherViewModel
import com.drew654.weather.utils.degToHdg
import com.drew654.weather.utils.getWeatherDescription
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun DailyForecastScreen(
    weatherViewModel: WeatherViewModel
) {
    val context = LocalContext.current
    val dailyForecast = weatherViewModel.dailyForecast.collectAsState()
    val selectedDay = weatherViewModel.selectedDay.collectAsState()

    Box(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .fillMaxSize()
    ) {
        Column {
            Spacer(modifier = Modifier.height(20.dp))
            LazyRow {
                items(1) {
                    Spacer(modifier = Modifier.width(16.dp))
                }
                itemsIndexed(dailyForecast.value?.day ?: listOf()) { index, _ ->
                    DailyForecastTile(
                        context = context,
                        onClick = {
                            weatherViewModel.setSelectedDay(index)
                        },
                        isSelected = {
                            selectedDay.value == index
                        },
                        dayOfWeek = dailyForecast.value?.day?.get(index)?.dayOfWeek?.getDisplayName(
                            TextStyle.SHORT,
                            Locale.getDefault()
                        ).toString(),
                        dayOfMonth = dailyForecast.value?.day?.get(
                            index
                        )?.dayOfMonth ?: 0,
                        maxTemperature = dailyForecast.value?.dailyMaxTemperature?.get(index)
                            ?: 0.0,
                        minTemperature = dailyForecast.value?.dailyMinTemperature?.get(index)
                            ?: 0.0,
                        weatherCode = dailyForecast.value?.dailyWeatherCode?.get(index) ?: 0,
                        precipitationProbability = dailyForecast.value?.dailyPrecipitationProbabilityMax?.get(
                            index
                        ) ?: 0
                    )
                }
                items(1) {
                    Spacer(modifier = Modifier.width(16.dp))
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(16.dp)
                ) {
                    Text(text = "Wind")
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${dailyForecast.value?.dailyWindSpeedMax?.get(selectedDay.value)} mph ${
                                degToHdg(
                                    dailyForecast.value?.dailyWindDirectionDominant?.get(selectedDay.value) ?: 0
                                )
                            }",
                            fontWeight = FontWeight.Bold
                        )
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_arrow_right_alt_24),
                            contentDescription = "Wind direction",
                            modifier = Modifier
                                .rotate(
                                    (dailyForecast.value?.dailyWindDirectionDominant
                                        ?.get(selectedDay.value)
                                        ?.toFloat() ?: 0f) + 90f
                                )
                                .align(Alignment.CenterVertically)
                                .padding(start = 4.dp)
                        )
                    }
                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(16.dp)
                ) {
                    Text(text = "Condition")
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = getWeatherDescription(
                                context,
                                dailyForecast.value?.dailyWeatherCode?.get(selectedDay.value)
                                    ?: 0,
                                true
                            ),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(16.dp)
                ) {
                    Text(text = "UV Index")
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${dailyForecast.value?.dailyUvIndexMax?.get(selectedDay.value)}",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(16.dp)
                ) {
                    Text(text = "Precipitation")
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${
                                dailyForecast.value?.dailyPrecipitationProbabilityMax?.get(
                                    selectedDay.value
                                )
                            }%",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(16.dp)
                ) {
                    Text(text = "Sunrise")
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${
                                dailyForecast.value?.dailySunrise?.get(selectedDay.value)?.hour
                            }:${dailyForecast.value?.dailySunrise?.get(selectedDay.value)?.minute}",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(16.dp)
                ) {
                    Text(text = "Sunset")
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${
                                dailyForecast.value?.dailySunset?.get(selectedDay.value)?.hour
                            }:${dailyForecast.value?.dailySunset?.get(selectedDay.value)?.minute}",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
