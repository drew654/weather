package com.drew654.weather.models

sealed class Screen(val route: String) {
    data object City : Screen(route = "city")

    data object Settings : Screen(route = "settings")
}
