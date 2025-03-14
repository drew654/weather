package com.drew654.weather.data

import com.drew654.weather.models.CurrentWeather
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.double
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonPrimitive

fun jsonToCurrentWeather(jsonObject: JsonObject): CurrentWeather {
    val temperature = jsonObject["temperature_2m"]?.jsonPrimitive?.double
    val relativeHumidity = jsonObject["relative_humidity_2m"]?.jsonPrimitive?.double
    val dewPoint = jsonObject["dew_point_2m"]?.jsonPrimitive?.double
    val apparentTemperature = jsonObject["apparent_temperature"]?.jsonPrimitive?.double
    val isDay = jsonObject["is_day"]?.jsonPrimitive?.int == 1
    val precipitation = jsonObject["precipitation"]?.jsonPrimitive?.double
    val rain = jsonObject["rain"]?.jsonPrimitive?.double
    val showers = jsonObject["showers"]?.jsonPrimitive?.double
    val snowfall = jsonObject["snowfall"]?.jsonPrimitive?.double
    val weatherCode = jsonObject["weather_code"]?.jsonPrimitive?.int
    val windSpeed = jsonObject["wind_speed_10m"]?.jsonPrimitive?.double
    val windDirection = jsonObject["wind_direction_10m"]?.jsonPrimitive?.int
    val windGusts = jsonObject["wind_gusts_10m"]?.jsonPrimitive?.double

    return CurrentWeather(
        temperature = temperature ?: 0.0,
        relativeHumidity = relativeHumidity ?: 0.0,
        dewPoint = dewPoint ?: 0.0,
        apparentTemperature = apparentTemperature ?: 0.0,
        isDay = isDay,
        precipitation = precipitation ?: 0.0,
        rain = rain ?: 0.0,
        showers = showers ?: 0.0,
        snowfall = snowfall ?: 0.0,
        weatherCode = weatherCode ?: 0,
        windSpeed = windSpeed ?: 0.0,
        windDirection = windDirection ?: 0,
        windGusts = windGusts ?: 0.0
    )
}
