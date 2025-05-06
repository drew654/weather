package com.drew654.weather.ui.screens.dailyForecast

import android.text.format.DateFormat.is24HourFormat
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
import com.drew654.weather.models.DayForecast
import com.drew654.weather.models.MeasurementUnit
import com.drew654.weather.models.MeasurementUnit.Companion.getDisplayNameFromDataName
import com.drew654.weather.models.WeatherViewModel
import com.drew654.weather.utils.calculateStartIndexForDay
import com.drew654.weather.utils.degToHdg
import com.drew654.weather.utils.formatTime
import com.drew654.weather.utils.getWeatherDescription
import com.drew654.weather.utils.showDouble
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.time.LocalDateTime

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
    val days = weatherForecast.value?.days!!
    val dailyMaxTemperature = weatherForecast.value?.dailyMaxTemperature!!
    val dailyMinTemperature = weatherForecast.value?.dailyMinTemperature!!
    val dailySunrise = weatherForecast.value?.dailySunrise!!
    val dailySunset = weatherForecast.value?.dailySunset!!
    val dailyWeatherCode = weatherForecast.value?.dailyWeatherCode!!
    val dailyPrecipitationProbabilityMax = weatherForecast.value?.dailyPrecipitationProbabilityMax!!
    val dailyWindSpeedMax = weatherForecast.value?.dailyWindSpeedMax!!
    val dailyWindDirectionDominant = weatherForecast.value?.dailyWindDirectionDominant!!
    val dailyUvIndexMax = weatherForecast.value?.dailyUvIndexMax!!

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
                itemsIndexed(days) { index, _ ->
                    val dayForecast = DayForecast(
                        date = days[index],
                        maxTemperature = dailyMaxTemperature[index],
                        minTemperature = dailyMinTemperature[index],
                        sunrise = dailySunrise[index],
                        sunset = dailySunset[index],
                        weatherCode = dailyWeatherCode[index],
                        precipitationProbabilityMax = dailyPrecipitationProbabilityMax[index],
                        windSpeedMax = dailyWindSpeedMax[index],
                        windDirectionDominant = dailyWindDirectionDominant[index],
                        uvIndexMax = dailyUvIndexMax[index]
                    )
                    DailyForecastTile(
                        weatherViewModel = weatherViewModel,
                        context = context,
                        onClick = {
                            weatherViewModel.setSelectedDay(index)
                        },
                        isSelected = {
                            selectedDay.value == index
                        },
                        dayForecast = dayForecast
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
                            text = "${
                                showDouble(dailyWindSpeedMax[selectedDay.value], showDecimal.value)
                            } ${
                                getDisplayNameFromDataName(windUnit.value)
                            } ${
                                degToHdg(dailyWindDirectionDominant[selectedDay.value])
                            }",
                            fontWeight = FontWeight.Bold
                        )
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_arrow_right_alt_24),
                            contentDescription = "Wind direction",
                            modifier = Modifier
                                .rotate(
                                    dailyWindDirectionDominant[selectedDay.value].toFloat() + 90f
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
                                dailyWeatherCode[selectedDay.value],
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
                            text = showDouble(
                                dailyUvIndexMax[selectedDay.value],
                                showDecimal.value
                            ),
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
                            text = "${dailyPrecipitationProbabilityMax[selectedDay.value]}%",
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
                            text = formatTime(
                                localDateTime = dailySunrise[selectedDay.value],
                                is24HourFormat = is24HourFormat(context)
                            ),
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
                            text = formatTime(
                                localDateTime = dailySunset[selectedDay.value],
                                is24HourFormat = is24HourFormat(context)
                            ),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            Text(
                text = "Hourly Forecast",
                color = MaterialTheme.colorScheme.surfaceTint,
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
