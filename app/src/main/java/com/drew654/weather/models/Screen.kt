package com.drew654.weather.models

sealed class Screen(val route: String) {
    data object Weather : Screen(route = "weather")

    data object Settings : Screen(route = "settings")
}
