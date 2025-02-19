package com.drew654.weather

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.drew654.weather.models.Screen
import com.drew654.weather.models.WeatherViewModel
import com.drew654.weather.ui.screens.CityScreen
import com.drew654.weather.ui.screens.SettingsScreen
import com.drew654.weather.ui.theme.WeatherTheme

class MainActivity : ComponentActivity() {
    private val weatherViewModel: WeatherViewModel by viewModels<WeatherViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WeatherTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val navController = rememberNavController()
                    NavHost(
                        navController = navController,
                        startDestination = Screen.Place.route,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable(Screen.Place.route) {
                            CityScreen(navController = navController)
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
