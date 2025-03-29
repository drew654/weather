package com.drew654.weather.models

import java.time.LocalDate
import java.time.LocalDateTime

class WeatherForecast(
    val currentTemperature: Double,
    val currentRelativeHumidity: Double,
    val currentDewPoint: Double,
    val currentApparentTemperature: Double,
    val currentIsDay: Boolean,
    val currentWeatherCode: Int,
    val currentWindSpeed: Double,
    val currentWindDirection: Int,
    val hours: List<LocalDateTime>,
    val hourlyTemperature: List<Double>,
    val hourlyWeatherCode: List<Int>,
    val hourlyPrecipitationProbability: List<Int>,
    val hourlyWindSpeed: List<Double>,
    val hourlyWindDirection: List<Int>,
    val days: List<LocalDate>,
    val dailyMaxTemperature: List<Double>,
    val dailyMinTemperature: List<Double>,
    val dailySunrise: List<LocalDateTime>,
    val dailySunset: List<LocalDateTime>,
    val dailyWeatherCode: List<Int>,
    val dailyPrecipitationProbabilityMax: List<Int>,
    val dailyWindSpeedMax: List<Double>,
    val dailyWindDirectionDominant: List<Int>,
    val dailyUvIndexMax: List<Double>
)
