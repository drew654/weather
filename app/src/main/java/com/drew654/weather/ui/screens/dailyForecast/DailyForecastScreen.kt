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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.PagerState
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
import com.drew654.weather.models.MeasurementUnit
import com.drew654.weather.models.MeasurementUnit.Companion.getDisplayNameFromDataName
import com.drew654.weather.models.WeatherViewModel
import com.drew654.weather.utils.calculateStartIndexForDay
import com.drew654.weather.utils.degToHdg
import com.drew654.weather.utils.getWeatherDescription
import com.drew654.weather.utils.showDouble
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun DailyForecastScreen(
    weatherViewModel: WeatherViewModel,
    pagerState: PagerState,
    hourlyListState: LazyListState,
    coroutineScope: CoroutineScope
) {
    val context = LocalContext.current
    val weatherForecast = weatherViewModel.weatherForecast.collectAsState()
    val selectedDay = weatherViewModel.selectedDay.collectAsState()
    val currentHour = LocalDateTime.now().hour
    val windUnit =
        weatherViewModel.windSpeedUnitFlow.collectAsState(initial = MeasurementUnit.Mph.dataName)
    val showDecimal = weatherViewModel.showDecimalFlow.collectAsState(initial = false)

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
                itemsIndexed(weatherForecast.value?.days ?: listOf()) { index, _ ->
                    DailyForecastTile(
                        weatherViewModel = weatherViewModel,
                        context = context,
                        onClick = {
                            weatherViewModel.setSelectedDay(index)
                        },
                        isSelected = {
                            selectedDay.value == index
                        },
                        dayOfWeek = weatherForecast.value?.days?.get(index)?.dayOfWeek?.getDisplayName(
                            TextStyle.SHORT,
                            Locale.getDefault()
                        ).toString(),
                        dayOfMonth = weatherForecast.value?.days?.get(
                            index
                        )?.dayOfMonth ?: 0,
                        maxTemperature = weatherForecast.value?.dailyMaxTemperature?.get(index)
                            ?: 0.0,
                        minTemperature = weatherForecast.value?.dailyMinTemperature?.get(index)
                            ?: 0.0,
                        weatherCode = weatherForecast.value?.dailyWeatherCode?.get(index) ?: 0,
                        precipitationProbability = weatherForecast.value?.dailyPrecipitationProbabilityMax?.get(
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
                            text = "${showDouble(weatherForecast.value?.dailyWindSpeedMax?.get(selectedDay.value)!!, showDecimal.value)} ${
                                getDisplayNameFromDataName(windUnit.value)
                            } ${
                                degToHdg(
                                    weatherForecast.value?.dailyWindDirectionDominant?.get(
                                        selectedDay.value
                                    ) ?: 0
                                )
                            }",
                            fontWeight = FontWeight.Bold
                        )
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_arrow_right_alt_24),
                            contentDescription = "Wind direction",
                            modifier = Modifier
                                .rotate(
                                    (weatherForecast.value?.dailyWindDirectionDominant
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
                                weatherForecast.value?.dailyWeatherCode?.get(selectedDay.value)
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
                            text = showDouble(weatherForecast.value?.dailyUvIndexMax?.get(selectedDay.value)!!, showDecimal.value),
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
                                weatherForecast.value?.dailyPrecipitationProbabilityMax?.get(
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
                                weatherForecast.value?.dailySunrise?.get(selectedDay.value)?.hour
                            }:${weatherForecast.value?.dailySunrise?.get(selectedDay.value)?.minute}",
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
                                weatherForecast.value?.dailySunset?.get(selectedDay.value)?.hour
                            }:${weatherForecast.value?.dailySunset?.get(selectedDay.value)?.minute}",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            Text(
                text = "Hourly Forecast",
                modifier = Modifier
                    .padding(16.dp)
                    .clickable {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(1)
                            hourlyListState.animateScrollToItem(
                                index = calculateStartIndexForDay(
                                    selectedDay.value,
                                    currentHour
                                )
                            )
                        }
                    }
            )
        }
    }
}
