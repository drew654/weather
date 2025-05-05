package com.drew654.weather.models

import java.time.LocalDate
import java.time.LocalDateTime

class DayForecast(
    val date: LocalDate,
    val maxTemperature: Double,
    val minTemperature: Double,
    val sunrise: LocalDateTime,
    val sunset: LocalDateTime,
    val weatherCode: Int,
    val precipitationProbabilityMax: Int,
    val windSpeedMax: Double,
    val windDirectionDominant: Int,
    val uvIndexMax: Double
)
