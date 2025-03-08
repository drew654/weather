package com.drew654.weather.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.drew654.weather.R
import com.drew654.weather.models.WeatherViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchPlaceScreen(
    weatherViewModel: WeatherViewModel,
    navController: NavHostController
) {
    val focusRequester = remember { FocusRequester() }
    val places = weatherViewModel.places.collectAsState()
    val placeSearchName = weatherViewModel.searchPlaceName.collectAsState()
    val isSearching = weatherViewModel.isSearching.collectAsState()

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
            onQueryChange = { weatherViewModel.setSearchPlaceName(it) },
            onSearch = {
                weatherViewModel.searchPlaceAndAdd(it)
                weatherViewModel.setSearchPlaceName("")
                weatherViewModel.setIsSearching(false)
                navController.popBackStack()
            },
            active = isSearching.value,
            onActiveChange = {
                weatherViewModel.setIsSearching(it)
                if (!it) {
                    navController.popBackStack()
                }
            },
            windowInsets = androidx.compose.foundation.layout.WindowInsets(0.dp),
            placeholder = {
                Text(text = "Search for a location")
            },
            leadingIcon = {
                IconButton(
                    onClick = {
                        weatherViewModel.setIsSearching(false)
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
                items(places.value.size) {
                    Text(
                        text = places.value[it].name,
                        modifier = Modifier
                            .clickable {
                                weatherViewModel.setSelectedPlace(places.value[it])
                                weatherViewModel.clearWeather()
                                weatherViewModel.fetchWeather()
                                weatherViewModel.setSearchPlaceName("")
                                weatherViewModel.setIsSearching(false)
                                weatherViewModel.movePlaceToFront(places.value[it])
                                navController.popBackStack()
                            }
                            .fillMaxWidth()
                            .padding(16.dp)
                    )
                }
            }
        }
    }
}
