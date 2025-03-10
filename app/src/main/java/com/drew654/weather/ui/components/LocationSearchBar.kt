package com.drew654.weather.ui.components

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
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

    SearchBar(
        query = searchPlaceName.value,
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
        },
        placeholder = {
            Text(
                text = selectedPlace.value?.name ?: "",
                overflow = TextOverflow.Ellipsis
            )
        },
        leadingIcon = {
            if (isSearching.value) {
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
            } else {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_search_24),
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
