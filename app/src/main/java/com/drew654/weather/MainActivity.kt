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
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.drew654.weather.models.Screen
import com.drew654.weather.models.WeatherViewModel
import com.drew654.weather.ui.screens.NewPlaceScreen
import com.drew654.weather.ui.screens.PlaceListScreen
import com.drew654.weather.ui.screens.place.PlaceScreen
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
                        startDestination = Screen.PlaceList.route,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable(Screen.PlaceList.route) {
                            PlaceListScreen(
                                navController = navController,
                                weatherViewModel = weatherViewModel
                            )
                        }
                        composable(
                            route = "${Screen.Place.route}/{id}",
                            arguments = listOf(
                                navArgument(name = "id") {
                                    type = NavType.StringType
                                }
                            )
                        ) {
                            PlaceScreen(
                                id = it.arguments?.getString("id")!!,
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
