package com.drew654.weather.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.drew654.weather.R
import com.drew654.weather.models.Screen
import com.drew654.weather.models.WeatherViewModel

@Composable
fun PlaceListScreen(navController: NavController, weatherViewModel: WeatherViewModel) {
    val places = weatherViewModel.places.collectAsState()

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column {
            Text(text = "Places")
            LazyColumn {
                items(places.value) { place ->
                    Text(
                        text = place.name,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { navController.navigate("${Screen.Place.route}/${place.id}") })
                }
            }
        }
        IconButton(
            onClick = {
                navController.navigate(Screen.Settings.route)
            },
            modifier = Modifier.align(Alignment.TopEnd)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.baseline_settings_24),
                contentDescription = "Settings"
            )
        }
        FloatingActionButton(
            onClick = {
                navController.navigate(Screen.NewPlace.route)
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.baseline_add_24),
                contentDescription = "Add Place"
            )
        }
    }
}
