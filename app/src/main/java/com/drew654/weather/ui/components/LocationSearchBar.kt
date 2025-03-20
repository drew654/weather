package com.drew654.weather.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.drew654.weather.R
import com.drew654.weather.models.Place
import com.drew654.weather.models.Screen
import com.drew654.weather.models.WeatherViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationSearchBar(
    weatherViewModel: WeatherViewModel,
    navController: NavHostController
) {
    val focusManager = LocalFocusManager.current
    val selectedPlace = weatherViewModel.selectedPlace.collectAsState()
    val searchPlaceName = weatherViewModel.searchPlaceName.collectAsState()
    val isSearching = weatherViewModel.isSearching.collectAsState()
    val padding = animateDpAsState(
        targetValue = if (isSearching.value) 0.dp else 16.dp,
        label = "padding"
    )
    val places = weatherViewModel.places.collectAsState()
    val fetchedPlaces = weatherViewModel.fetchedPlaces.collectAsState()
    val currentLocation = weatherViewModel.currentLocation.collectAsState()
    val isManagingLocations = remember { mutableStateOf(false) }

    val inputField = @Composable {
        SearchBarDefaults.InputField(
            query = searchPlaceName.value,
            onQueryChange = {
                weatherViewModel.setSearchPlaceName(it)
                weatherViewModel.fetchPlaces(it)
            },
            onSearch = {
                focusManager.clearFocus()
            },
            expanded = isSearching.value,
            onExpandedChange = {
                weatherViewModel.setIsSearching(it)
                if (!isSearching.value) {
                    isManagingLocations.value = false
                }
            },
            placeholder = {
                Text(
                    text = if (isSearching.value) "Search for a location"
                    else selectedPlace.value?.name ?: "",
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )
            },
            leadingIcon = {
                if (isSearching.value) {
                    IconButton(
                        onClick = {
                            weatherViewModel.setIsSearching(false)
                            weatherViewModel.setSearchPlaceName("")
                            weatherViewModel.clearFetchedPlaces()
                        },
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_arrow_back_24),
                            contentDescription = "Back icon"
                        )
                    }
                } else {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_location_pin_24),
                        contentDescription = "Search icon"
                    )
                }
            },
            trailingIcon = {
                if (!isSearching.value) {
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
            },
        )
    }

    SearchBar(
        inputField = inputField,
        expanded = isSearching.value,
        onExpandedChange = {
            weatherViewModel.setIsSearching(it)
            if (!isSearching.value) {
                isManagingLocations.value = false
            }
        },
        windowInsets = WindowInsets(0.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = padding.value)
    ) {
        LazyColumn {
            if (currentLocation.value != null && searchPlaceName.value.isEmpty()) {
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
            if (searchPlaceName.value.isEmpty()) {
                if (places.value.isNotEmpty()) {
                    items(1) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(all = 16.dp)
                        ) {
                            Text(text = "Saved locations")
                            Text(
                                text = if (isManagingLocations.value) "Done" else "Manage",
                                modifier = Modifier
                                    .clickable {
                                        isManagingLocations.value = !isManagingLocations.value
                                    }
                            )
                        }
                    }
                }
                items(places.value.size) {
                    SearchOption(
                        weatherViewModel = weatherViewModel,
                        navController = navController,
                        place = places.value[it],
                        isManagingLocations = isManagingLocations.value
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
