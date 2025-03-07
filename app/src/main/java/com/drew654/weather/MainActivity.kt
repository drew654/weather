package com.drew654.weather

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.drew654.weather.models.Screen
import com.drew654.weather.models.WeatherViewModel
import com.drew654.weather.ui.screens.NewPlaceScreen
import com.drew654.weather.ui.screens.SettingsScreen
import com.drew654.weather.ui.screens.WeatherScreen
import com.drew654.weather.ui.theme.WeatherTheme

class MainActivity : ComponentActivity() {
    private val weatherViewModel: WeatherViewModel by viewModels<WeatherViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route
            WeatherTheme {
                Scaffold(
                    topBar = {
                        if (currentRoute == Screen.Weather.route) {
                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .statusBarsPadding()
                            ) {
                                IconButton(
                                    onClick = {
                                        navController.navigate(Screen.NewPlace.route)
                                    }
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.baseline_add_24),
                                        contentDescription = "Add Place"
                                    )
                                }
                                IconButton(
                                    onClick = {
                                        navController.navigate(Screen.Settings.route)
                                    }
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.baseline_settings_24),
                                        contentDescription = "Settings"
                                    )
                                }
                            }
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = Screen.Weather.route,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable(Screen.Weather.route) {
                            WeatherScreen(
                                weatherViewModel = weatherViewModel
                            )
                        }
                        composable(Screen.NewPlace.route) {
                            NewPlaceScreen(
                                weatherViewModel = weatherViewModel,
                                navController = navController
                            )
                        }
                        composable(Screen.Settings.route) {
                            SettingsScreen(weatherViewModel = weatherViewModel)
                        }
                    }
                }
            }
        }
    }
}
