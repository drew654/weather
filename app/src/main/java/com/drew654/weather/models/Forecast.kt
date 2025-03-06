package com.drew654.weather.models

import java.time.LocalDateTime

class Forecast(
    val hour: List<LocalDateTime>,
    val hourlyTemperature: List<Double>,
    val hourlyWeatherCode: List<Int>,
    val hourlyPrecipitationProbability: List<Int>,
    val hourlyWindSpeed: List<Double>,
    val hourlyWindDirection: List<Int>
)
