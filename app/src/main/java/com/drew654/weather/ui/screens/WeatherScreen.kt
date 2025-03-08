package com.drew654.weather.ui.screens

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.drew654.weather.R
import com.drew654.weather.models.Screen
import com.drew654.weather.models.WeatherViewModel
import com.drew654.weather.ui.screens.hourly.HourlyScreen
import com.drew654.weather.ui.screens.place.PlaceScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherScreen(
    weatherViewModel: WeatherViewModel,
    weatherNavController: NavHostController
) {
    val navBackStackEntry by weatherNavController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            NavigationBar {
                listOf(
                    NavigationBarItem(
                        label = {
                            Text(text = "Home")
                        },
                        selected = currentRoute == Screen.Place.route,
                        onClick = {
                            weatherNavController.navigate(Screen.Place.route) {
                                popUpTo(Screen.Place.route) {
                                    inclusive = true
                                }
                            }
                        },
                        icon = {
                            Icon(
                                painter = painterResource(id = R.drawable.baseline_sunny_24),
                                contentDescription = "Settings"
                            )
                        }
                    ),
                    NavigationBarItem(
                        label = {
                            Text(text = "Hourly")
                        },
                        selected = currentRoute == Screen.Hourly.route,
                        onClick = {
                            weatherNavController.navigate(Screen.Hourly.route) {
                                popUpTo(Screen.Hourly.route) {
                                    inclusive = true
                                }
                            }
                        },
                        icon = {
                            Icon(
                                painter = painterResource(id = R.drawable.baseline_access_time_24),
                                contentDescription = "Settings"
                            )
                        }
                    )
                )
            }
        },
        contentWindowInsets = WindowInsets(0.dp)
    ) { innerPadding ->
        NavHost(
            navController = weatherNavController,
            startDestination = Screen.Place.route,
            enterTransition = {
                EnterTransition.None
            },
            exitTransition = {
                ExitTransition.None
            },
            popEnterTransition = {
                EnterTransition.None
            },
            popExitTransition = {
                ExitTransition.None
            },
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Place.route) {
                PlaceScreen(
                    weatherViewModel = weatherViewModel
                )
            }
            composable(Screen.Hourly.route) {
                HourlyScreen(
                    weatherViewModel = weatherViewModel
                )
            }
        }
    }
}
