package com.drew654.weather.models

import java.time.LocalDateTime

class Forecast(
    val hourlyTemperature: List<Pair<LocalDateTime, Double>>
)
