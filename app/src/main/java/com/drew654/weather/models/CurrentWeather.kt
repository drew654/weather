package com.drew654.weather.models

class CurrentWeather(
    val temperature: Double,
    val relativeHumidity: Double,
    val dewPoint: Double,
    val apparentTemperature: Double,
    val isDay: Boolean,
    val precipitation: Double,
    val rain: Double,
    val showers: Double,
    val snowfall: Double,
    val weatherCode: Int,
    val windSpeed: Double,
    val windDirection: Int,
    val windGusts: Double
)
