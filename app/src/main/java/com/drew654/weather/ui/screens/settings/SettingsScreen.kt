package com.drew654.weather.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.drew654.weather.models.WeatherViewModel
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    weatherViewModel: WeatherViewModel
) {
    val coroutineScope = rememberCoroutineScope()
    val swipeToChangeTabs = weatherViewModel.swipeToChangeTabsFlow.collectAsState(initial = false)
    val preferences = listOf(
        "Swipe to Change Tabs" to swipeToChangeTabs.value
    )

    Box(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .fillMaxSize()
    ) {
        LazyColumn {
            items(preferences.size) {
                SettingsSwitchRow(
                    index = it,
                    preferences = preferences,
                    onClick = {
                        coroutineScope.launch {
                            when (it) {
                                0 -> weatherViewModel.updateSwipeToChangeTabs(!swipeToChangeTabs.value)
                            }
                        }
                    }
                )
            }
        }
    }
}
