package com.drew654.weather.ui.screens.searchPlace

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.drew654.weather.models.Place
import com.drew654.weather.models.WeatherViewModel

@Composable
fun SearchOption(
    weatherViewModel: WeatherViewModel,
    navController: NavHostController,
    place: Place
) {
    val places = weatherViewModel.places.collectAsState()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                weatherViewModel.setSelectedPlace(place)
                weatherViewModel.clearWeather()
                weatherViewModel.fetchWeather()
                weatherViewModel.setSearchPlaceName("")
                weatherViewModel.setIsSearching(false)
                weatherViewModel.clearFetchedPlaces()
                navController.popBackStack()
            }
            .padding(vertical = 16.dp)
    ) {
        Text(
            text = place.name,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .weight(1f)
        )
        if (
            !places.value.any { it.latitude == place.latitude && it.longitude == place.longitude }
            && place.name != "Current Location"
        ) {
            Text(
                text = "Save",
                modifier = Modifier
                    .clickable {
                        weatherViewModel.addPlace(place)
                    }
                    .padding(horizontal = 16.dp)
                    .align(Alignment.CenterVertically)
            )
        }
    }
}
