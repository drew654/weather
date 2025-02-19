package com.drew654.weather.models

sealed class Screen(val route: String) {
    data object PlaceList : Screen(route = "place-list")

    data object Place : Screen(route = "place")

    data object Settings : Screen(route = "settings")
}
