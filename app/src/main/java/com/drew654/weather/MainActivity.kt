package com.drew654.weather

import android.Manifest
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.drew654.weather.models.Screen
import com.drew654.weather.models.WeatherViewModel
import com.drew654.weather.ui.screens.DebugScreen
import com.drew654.weather.ui.screens.WeatherScreen
import com.drew654.weather.ui.screens.settings.SettingsScreen
import com.drew654.weather.ui.theme.WeatherTheme

class MainActivity : ComponentActivity() {
    private val weatherViewModel: WeatherViewModel by viewModels<WeatherViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            val isOfflineMode = weatherViewModel.isOfflineMode.collectAsState()

            WeatherTheme {
                Scaffold(
                    bottomBar = {
                        if (isOfflineMode.value) {
                            Text(
                                text = "Offline",
                                fontSize = 12.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colorScheme.background)
                                    .padding(
                                        bottom = WindowInsets.navigationBars
                                            .asPaddingValues()
                                            .calculateBottomPadding()
                                    )
                            )
                        }
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = Screen.Weather.route,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable(Screen.Weather.route) {
                            WeatherScreen(
                                weatherViewModel = weatherViewModel,
                                navController = navController
                            )
                        }
                        composable(Screen.Settings.route) {
                            SettingsScreen(
                                weatherViewModel = weatherViewModel,
                                navController = navController
                            )
                        }
                        composable(Screen.Debug.route) {
                            DebugScreen()
                        }
                    }
                    if (!weatherViewModel.hasLocationPermission()) {
                        ActivityCompat.requestPermissions(
                            this,
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            ),
                            1
                        )
                    }
                }
            }
        }
    }
}
