package com.drew654.weather.ui.screens.dailyForecast

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.drew654.weather.R
import com.drew654.weather.models.WeatherViewModel
import com.drew654.weather.utils.degToHdg
import com.drew654.weather.utils.getWeatherDescription
import com.drew654.weather.utils.getWeatherIconUrl
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
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clip(MaterialTheme.shapes.medium)
                            .clickable {
                                weatherViewModel.setSelectedDay(index)
                            }
                            .background(if (selectedDay.value == index) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.background)
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "${
                                dailyForecast.value?.day?.get(index)?.dayOfWeek?.getDisplayName(
                                    TextStyle.SHORT,
                                    Locale.getDefault()
                                )
                            } ${
                                dailyForecast.value?.day?.get(
                                    index
                                )?.dayOfMonth.toString()
                            }"
                        )
                        Text(
                            text = "${dailyForecast.value?.dailyMaxTemperature?.get(index)}°",
                            fontWeight = FontWeight.Bold
                        )
                        Text(text = "${dailyForecast.value?.dailyMinTemperature?.get(index)}°")
                        AsyncImage(
                            model = getWeatherIconUrl(
                                weatherCode = dailyForecast.value?.dailyWeatherCode?.get(index)
                                    ?: 0,
                                isDay = true
                            ),
                            contentDescription = getWeatherDescription(
                                context = context,
                                weatherCode = dailyForecast.value?.dailyWeatherCode?.get(index)
                                    ?: 0,
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
                                text = "${
                                    dailyForecast.value?.dailyPrecipitationProbabilityMax?.get(
                                        index
                                    )
                                }%"
                            )
                        }
                    }
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
