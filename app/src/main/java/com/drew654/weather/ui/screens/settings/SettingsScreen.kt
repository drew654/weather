package com.drew654.weather.ui.screens.settings

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.drew654.weather.R
import com.drew654.weather.models.MeasurementUnit
import com.drew654.weather.models.MeasurementUnit.Companion.getDataNameFromDisplayName
import com.drew654.weather.models.MeasurementUnit.Companion.getDisplayNameFromDataName
import com.drew654.weather.models.Screen
import com.drew654.weather.models.WeatherViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    weatherViewModel: WeatherViewModel,
    navController: NavController
) {
    val focusManager = LocalFocusManager.current
    val coroutineScope = rememberCoroutineScope()
    val swipeToChangeTabs = weatherViewModel.swipeToChangeTabsFlow.collectAsState(initial = false)
    val showDecimal = weatherViewModel.showDecimalFlow.collectAsState(initial = false)
    val preferences = listOf(
        "Swipe to change tabs" to swipeToChangeTabs.value,
        "Show decimals" to showDecimal.value
    )
    val temperatureUnit =
        weatherViewModel.temperatureUnitFlow.collectAsState(initial = MeasurementUnit.Fahrenheit.dataName)
    val windSpeedUnit =
        weatherViewModel.windSpeedUnitFlow.collectAsState(initial = MeasurementUnit.Mph.dataName)
    val precipitationUnit =
        weatherViewModel.precipitationUnitFlow.collectAsState(initial = MeasurementUnit.Inch.dataName)

    BackHandler {
        navController.navigate(Screen.Weather.route) {
            popUpTo(Screen.Weather.route) {
                inclusive = true
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Settings") },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            navController.navigate(Screen.Weather.route) {
                                popUpTo(Screen.Weather.route) {
                                    inclusive = true
                                }
                            }
                        },
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_arrow_back_24),
                            contentDescription = "Back icon"
                        )
                    }
                },
                windowInsets = WindowInsets(0.dp)
            )
        },
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .fillMaxSize()
            .clickable(
                indication = null,
                interactionSource = null
            ) {
                focusManager.clearFocus()
            }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.padding(innerPadding),
        ) {
            items(preferences.size) {
                SettingsSwitchRow(
                    index = it,
                    preferences = preferences,
                    onClick = {
                        coroutineScope.launch {
                            when (it) {
                                0 -> weatherViewModel.updateSwipeToChangeTabs(!swipeToChangeTabs.value)
                                1 -> weatherViewModel.updateShowDecimal(!showDecimal.value)
                            }
                        }
                    }
                )
            }
            items(1) {
                SettingsDropdownRow(
                    label = "Temperature Unit",
                    value = getDisplayNameFromDataName(temperatureUnit.value),
                    onValueChange = {
                        coroutineScope.launch {
                            weatherViewModel.updateTemperatureUnit(getDataNameFromDisplayName(it))
                            weatherViewModel.fetchWeather()
                            focusManager.clearFocus()
                        }
                    },
                    options = listOf(
                        MeasurementUnit.Fahrenheit.displayName,
                        MeasurementUnit.Celsius.displayName
                    )
                )
                SettingsDropdownRow(
                    label = "Wind Speed Unit",
                    value = getDisplayNameFromDataName(windSpeedUnit.value),
                    onValueChange = {
                        coroutineScope.launch {
                            weatherViewModel.updateWindSpeedUnit(getDataNameFromDisplayName(it))
                            weatherViewModel.fetchWeather()
                            focusManager.clearFocus()
                        }
                    },
                    options = listOf(
                        MeasurementUnit.Mph.displayName,
                        MeasurementUnit.Kph.displayName,
                        MeasurementUnit.Mps.displayName,
                        MeasurementUnit.Knots.displayName
                    )
                )
                SettingsDropdownRow(
                    label = "Precipitation Unit",
                    value = getDisplayNameFromDataName(precipitationUnit.value),
                    onValueChange = {
                        coroutineScope.launch {
                            weatherViewModel.updatePrecipitationUnit(getDataNameFromDisplayName(it))
                            weatherViewModel.fetchWeather()
                            focusManager.clearFocus()
                        }
                    },
                    options = listOf(
                        MeasurementUnit.Inch.displayName,
                        MeasurementUnit.Millimeter.displayName
                    )
                )
            }
        }
    }
}
