package com.drew654.weather.data

import com.drew654.weather.models.DailyWeather
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.double
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonPrimitive
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun jsonToDailyWeather(jsonObject: JsonObject): DailyWeather {
    val maxTemperature = jsonObject["temperature_2m_max"]?.jsonArray
    val minTemperature = jsonObject["temperature_2m_min"]?.jsonArray
    val sunrise = jsonObject["sunrise"]?.jsonArray?.get(0)?.jsonPrimitive?.content
    val sunset = jsonObject["sunset"]?.jsonArray?.get(0)?.jsonPrimitive?.content

    return DailyWeather(
        maxTemperature = maxTemperature?.get(0)?.jsonPrimitive?.double ?: 0.0,
        minTemperature = minTemperature?.get(0)?.jsonPrimitive?.double ?: 0.0,
        sunrise = LocalDateTime.parse(sunrise, DateTimeFormatter.ISO_DATE_TIME),
        sunset = LocalDateTime.parse(sunset, DateTimeFormatter.ISO_DATE_TIME)
    )
}
