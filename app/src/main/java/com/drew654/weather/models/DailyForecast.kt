package com.drew654.weather.models

import java.time.LocalDate
import java.time.LocalDateTime

class DailyForecast(
    val day: List<LocalDate>,
    val maxTemperature: List<Double>,
    val minTemperature: List<Double>,
    val dailySunrise: List<LocalDateTime>,
    val dailySunset: List<LocalDateTime>,
    val dailyWeatherCode: List<Int>,
    val dailyPrecipitationProbabilityMax: List<Int>,
    val dailyWindSpeedMax: List<Double>,
)
