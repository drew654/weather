package com.drew654.weather.ui.screens.searchPlace

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.drew654.weather.R
import com.drew654.weather.models.Place
import com.drew654.weather.models.WeatherViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchPlaceScreen(
    weatherViewModel: WeatherViewModel,
    navController: NavHostController
) {
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }
    val places = weatherViewModel.places.collectAsState()
    val placeSearchName = weatherViewModel.searchPlaceName.collectAsState()
    val isSearching = weatherViewModel.isSearching.collectAsState()
    val fetchedPlaces = weatherViewModel.fetchedPlaces.collectAsState()
    val currentLocation = weatherViewModel.currentLocation.collectAsState()

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Box(
        modifier = Modifier
            .background(color = MaterialTheme.colorScheme.background)
            .fillMaxSize()
    ) {
        SearchBar(
            query = placeSearchName.value,
            onQueryChange = {
                weatherViewModel.setSearchPlaceName(it)
                weatherViewModel.fetchPlaces(it)
            },
            onSearch = {
                focusManager.clearFocus()
            },
            active = isSearching.value,
            onActiveChange = {
                weatherViewModel.setIsSearching(it)
                if (!it) {
                    navController.popBackStack()
                }
            },
            windowInsets = WindowInsets(0.dp),
            placeholder = {
                Text(text = "Search for a location")
            },
            leadingIcon = {
                IconButton(
                    onClick = {
                        weatherViewModel.setIsSearching(false)
                        weatherViewModel.setSearchPlaceName("")
                        weatherViewModel.clearFetchedPlaces()
                        navController.popBackStack()
                    },
                    modifier = Modifier.padding(8.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_arrow_back_24),
                        contentDescription = "Back icon"
                    )
                }
            },
            modifier = Modifier.focusRequester(focusRequester)
        ) {
            LazyColumn {
                if (currentLocation.value != null && fetchedPlaces.value.isEmpty()) {
                    items(1) {
                        SearchOption(
                            weatherViewModel = weatherViewModel,
                            navController = navController,
                            place = Place(
                                name = "Current Location",
                                latitude = currentLocation.value?.latitude!!,
                                longitude = currentLocation.value?.longitude!!
                            )
                        )
                    }
                }
                if (fetchedPlaces.value.isEmpty()) {
                    items(places.value.size) {
                        SearchOption(
                            weatherViewModel = weatherViewModel,
                            navController = navController,
                            place = places.value[it]
                        )
                    }
                } else {
                    items(fetchedPlaces.value.size) {
                        SearchOption(
                            weatherViewModel = weatherViewModel,
                            navController = navController,
                            place = fetchedPlaces.value[it]
                        )
                    }
                }
            }
        }
    }
}
