package com.drew654.weather.models

import java.time.LocalDateTime

class DailyWeather(
    val maxTemperature: Double,
    val minTemperature: Double,
    val sunrise: LocalDateTime,
    val sunset: LocalDateTime
)
