package com.drew654.weather.models

class Forecast(
    val hourlyTemperature: List<Double>,
    val hourlyWeatherCode: List<Int>,
    val hourlyPrecipitationProbability: List<Int>,
    val hourlyWindSpeed: List<Double>,
    val hourlyWindDirection: List<Int>
)
