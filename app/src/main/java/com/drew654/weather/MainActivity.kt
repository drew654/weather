package com.drew654.weather

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.drew654.weather.models.Screen
import com.drew654.weather.models.WeatherViewModel
import com.drew654.weather.ui.components.LocationSearchBar
import com.drew654.weather.ui.screens.SettingsScreen
import com.drew654.weather.ui.screens.WeatherScreen
import com.drew654.weather.ui.theme.WeatherTheme

class MainActivity : ComponentActivity() {
    private val weatherViewModel: WeatherViewModel by viewModels<WeatherViewModel>()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            val weatherNavController = rememberNavController()

            WeatherTheme {
                Scaffold(
                    topBar = {
                        LocationSearchBar(
                            weatherViewModel = weatherViewModel,
                            navController = navController
                        )
                    },
                    modifier = Modifier.fillMaxSize(),
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = Screen.Weather.route,
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
                        composable(Screen.Weather.route) {
                            WeatherScreen(
                                weatherViewModel = weatherViewModel,
                                weatherNavController = weatherNavController
                            )
                        }
                        composable(Screen.Settings.route) {
                            SettingsScreen(
                                weatherViewModel = weatherViewModel
                            )
                        }
                    }
                }
            }
        }
    }
}
