package com.drew654.weather.models

sealed class Screen(val route: String) {
    data object Place : Screen(route = "weather/place")

    data object NewPlace : Screen(route = "new-place")

    data object Settings : Screen(route = "settings")

    data object Weather : Screen(route = "weather")

    data object Hourly : Screen(route = "weather/hourly")
}
