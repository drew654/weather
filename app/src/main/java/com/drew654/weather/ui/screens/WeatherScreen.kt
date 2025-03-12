package com.drew654.weather.ui.screens

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import com.drew654.weather.R
import com.drew654.weather.models.WeatherViewModel
import com.drew654.weather.ui.screens.hourly.HourlyWeatherScreen
import com.drew654.weather.ui.screens.place.CurrentWeatherScreen
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherScreen(
    weatherViewModel: WeatherViewModel
) {
    val pagerState = rememberPagerState(pageCount = { 2 })
    val coroutineScope = rememberCoroutineScope()
    val swipeToChangeTabs = weatherViewModel.swipeToChangeTabsFlow.collectAsState(initial = false)

    Scaffold(
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
                    )
                )
            }
        },
        contentWindowInsets = WindowInsets(0.dp)
    ) { innerPadding ->
        HorizontalPager(
            state = pagerState,
            userScrollEnabled = swipeToChangeTabs.value,
            modifier = Modifier.padding(innerPadding)
        ) { page ->
            when (page) {
                0 -> CurrentWeatherScreen(weatherViewModel = weatherViewModel)
                1 -> HourlyWeatherScreen(weatherViewModel = weatherViewModel)
            }
        }
    }
}
