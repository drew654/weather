package com.drew654.weather.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.drew654.weather.R
import com.drew654.weather.models.WeatherViewModel
import com.drew654.weather.ui.components.LocationSearchBar
import com.drew654.weather.ui.screens.dailyForecast.DailyForecastScreen
import com.drew654.weather.ui.screens.hourly.HourlyWeatherScreen
import com.drew654.weather.ui.screens.place.CurrentWeatherScreen
import kotlinx.coroutines.launch

@Composable
fun WeatherScreen(
    weatherViewModel: WeatherViewModel,
    navController: NavHostController
) {
    val pagerState = rememberPagerState(pageCount = { 3 })
    val coroutineScope = rememberCoroutineScope()
    val swipeToChangeTabs = weatherViewModel.swipeToChangeTabsFlow.collectAsState(initial = false)
    val hourlyListState = rememberLazyListState()
    val weatherForecast = weatherViewModel.weatherForecast.collectAsState()

    Scaffold(
        topBar = {
            LocationSearchBar(
                weatherViewModel = weatherViewModel,
                navController = navController
            )
        },
        bottomBar = {
            NavigationBar {
                listOf(
                    NavigationBarItem(
                        label = {
                            Text(text = "Current")
                        },
                        selected = pagerState.currentPage == 0,
                        onClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(0)
                            }
                        },
                        icon = {
                            Icon(
                                painter = painterResource(id = R.drawable.baseline_sunny_24),
                                contentDescription = "Current weather"
                            )
                        }
                    ),
                    NavigationBarItem(
                        label = {
                            Text(text = "Hourly")
                        },
                        selected = pagerState.currentPage == 1,
                        onClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(1)
                            }
                        },
                        icon = {
                            Icon(
                                painter = painterResource(id = R.drawable.baseline_access_time_24),
                                contentDescription = "Hourly weather"
                            )
                        }
                    ),
                    NavigationBarItem(
                        label = {
                            Text(text = "Daily")
                        },
                        selected = pagerState.currentPage == 2,
                        onClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(2)
                            }
                        },
                        icon = {
                            Icon(
                                painter = painterResource(id = R.drawable.baseline_calendar_today_24),
                                contentDescription = "Daily weather"
                            )
                        }
                    )
                )
            }
        }
    ) { innerPadding ->
        if (weatherForecast.value == null) {
            Box(
                contentAlignment = androidx.compose.ui.Alignment.Center,
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.width(64.dp),
                    color = MaterialTheme.colorScheme.secondary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                )
            }
        } else {
            HorizontalPager(
                state = pagerState,
                userScrollEnabled = swipeToChangeTabs.value,
                modifier = Modifier.padding(innerPadding)
            ) { page ->
                when (page) {
                    0 -> CurrentWeatherScreen(weatherViewModel = weatherViewModel)
                    1 -> HourlyWeatherScreen(
                        weatherViewModel = weatherViewModel,
                        hourlyListState = hourlyListState,
                    )

                    2 -> DailyForecastScreen(
                        weatherViewModel = weatherViewModel,
                        pagerState = pagerState,
                        hourlyListState = hourlyListState,
                        coroutineScope = coroutineScope,
                    )
                }
            }
        }
    }
}
