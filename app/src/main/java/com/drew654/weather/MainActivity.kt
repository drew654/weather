package com.drew654.weather

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.drew654.weather.models.Screen
import com.drew654.weather.models.WeatherViewModel
import com.drew654.weather.ui.screens.SettingsScreen
import com.drew654.weather.ui.screens.WeatherScreen
import com.drew654.weather.ui.screens.searchPlace.SearchPlaceScreen
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
            val navBackStackEntry by weatherNavController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route
            val selectedPlace = weatherViewModel.selectedPlace.collectAsState()
            val searchPlaceName = weatherViewModel.searchPlaceName.collectAsState()
            val isSearching = weatherViewModel.isSearching.collectAsState()

            WeatherTheme {
                Scaffold(
                    topBar = {
                        if (!isSearching.value && currentRoute == Screen.Place.route) {
                            SearchBar(
                                query = searchPlaceName.value,
                                onQueryChange = { weatherViewModel.setSearchPlaceName(it) },
                                onSearch = {},
                                active = isSearching.value,
                                onActiveChange = {
                                    weatherViewModel.setIsSearching(it)
                                    navController.navigate(Screen.SearchPlace.route)
                                },
                                placeholder = {
                                    Text(
                                        text = selectedPlace.value?.name ?: "",
                                        overflow = TextOverflow.Ellipsis
                                    )
                                },
                                leadingIcon = {
                                    Icon(
                                        painter = painterResource(id = R.drawable.baseline_search_24),
                                        contentDescription = "Search icon"
                                    )
                                },
                                trailingIcon = {
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
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp)
                            ) {}
                        }
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
                        composable(Screen.SearchPlace.route) {
                            SearchPlaceScreen(
                                weatherViewModel = weatherViewModel,
                                navController = navController
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
