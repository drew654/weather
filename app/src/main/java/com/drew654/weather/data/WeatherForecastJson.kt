package com.drew654.weather.data

import com.drew654.weather.models.WeatherForecast
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.double
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun jsonToWeatherForecast(
    weatherForecastJson: JsonObject
): WeatherForecast {
    val currentWeatherJson = weatherForecastJson["current"]?.jsonObject ?: JsonObject(emptyMap())
    val hourlyForecastJson = weatherForecastJson["hourly"]?.jsonObject ?: JsonObject(emptyMap())
    val dailyForecastJson = weatherForecastJson["daily"]?.jsonObject ?: JsonObject(emptyMap())

    val currentTemperature = currentWeatherJson["temperature_2m"]?.jsonPrimitive?.double
    val currentRelativeHumidity = currentWeatherJson["relative_humidity_2m"]?.jsonPrimitive?.int
    val currentDewPoint = currentWeatherJson["dew_point_2m"]?.jsonPrimitive?.double
    val currentApparentTemperature =
        currentWeatherJson["apparent_temperature"]?.jsonPrimitive?.double
    val currentIsDay = currentWeatherJson["is_day"]?.jsonPrimitive?.int == 1
    val currentWeatherCode = currentWeatherJson["weather_code"]?.jsonPrimitive?.int
    val currentWindSpeed = currentWeatherJson["wind_speed_10m"]?.jsonPrimitive?.double
    val currentWindDirection = currentWeatherJson["wind_direction_10m"]?.jsonPrimitive?.int

    val hours = hourlyForecastJson["time"]?.jsonArray?.map {
        LocalDateTime.parse(
            it.jsonPrimitive.content,
            DateTimeFormatter.ISO_DATE_TIME
        )
    }
    val hourlyTemperature =
        hourlyForecastJson["temperature_2m"]?.jsonArray?.map { it.jsonPrimitive.double }
    val hourlyWeatherCode =
        hourlyForecastJson["weather_code"]?.jsonArray?.map { it.jsonPrimitive.int }
    val hourlyPrecipitationProbability =
        hourlyForecastJson["precipitation_probability"]?.jsonArray?.map { it.jsonPrimitive.int }
    val hourlyWindSpeed =
        hourlyForecastJson["wind_speed_10m"]?.jsonArray?.map { it.jsonPrimitive.double }
    val hourlyWindDirection =
        hourlyForecastJson["wind_direction_10m"]?.jsonArray?.map { it.jsonPrimitive.int }

    val days = dailyForecastJson["time"]?.jsonArray?.map {
        LocalDate.parse(it.jsonPrimitive.content, DateTimeFormatter.ISO_DATE)
    }
    val dailyMaxTemperature =
        dailyForecastJson["temperature_2m_max"]?.jsonArray?.map { it.jsonPrimitive.double }
    val dailyMinTemperature =
        dailyForecastJson["temperature_2m_min"]?.jsonArray?.map { it.jsonPrimitive.double }
    val dailySunrise = dailyForecastJson["sunrise"]?.jsonArray?.map {
        LocalDateTime.parse(
            it.jsonPrimitive.content,
            DateTimeFormatter.ISO_DATE_TIME
        )
    }
    val dailySunset = dailyForecastJson["sunset"]?.jsonArray?.map {
        LocalDateTime.parse(
            it.jsonPrimitive.content,
            DateTimeFormatter.ISO_DATE_TIME
        )
    }
    val dailyWeatherCode =
        dailyForecastJson["weather_code"]?.jsonArray?.map { it.jsonPrimitive.int }
    val dailyPrecipitationProbabilityMax =
        dailyForecastJson["precipitation_probability_max"]?.jsonArray?.map { it.jsonPrimitive.int }
    val dailyWindSpeedMax =
        dailyForecastJson["wind_speed_10m_max"]?.jsonArray?.map { it.jsonPrimitive.double }
    val dailyWindDirectionDominant =
        dailyForecastJson["wind_direction_10m_dominant"]?.jsonArray?.map { it.jsonPrimitive.int }
    val dailyUvIndexMax =
        dailyForecastJson["uv_index_max"]?.jsonArray?.map { it.jsonPrimitive.double }

    return WeatherForecast(
        currentTemperature = currentTemperature ?: 0.0,
        currentRelativeHumidity = currentRelativeHumidity ?: 0,
        currentDewPoint = currentDewPoint ?: 0.0,
        currentApparentTemperature = currentApparentTemperature ?: 0.0,
        currentIsDay = currentIsDay,
        currentWeatherCode = currentWeatherCode ?: 0,
        currentWindSpeed = currentWindSpeed ?: 0.0,
        currentWindDirection = currentWindDirection ?: 0,
        hours = hours ?: emptyList(),
        hourlyTemperature = hourlyTemperature ?: emptyList(),
        hourlyWeatherCode = hourlyWeatherCode ?: emptyList(),
        hourlyPrecipitationProbability = hourlyPrecipitationProbability ?: emptyList(),
        hourlyWindSpeed = hourlyWindSpeed ?: emptyList(),
        hourlyWindDirection = hourlyWindDirection ?: emptyList(),
        days = days ?: emptyList(),
        dailyMaxTemperature = dailyMaxTemperature ?: emptyList(),
        dailyMinTemperature = dailyMinTemperature ?: emptyList(),
        dailySunrise = dailySunrise ?: emptyList(),
        dailySunset = dailySunset ?: emptyList(),
        dailyWeatherCode = dailyWeatherCode ?: emptyList(),
        dailyPrecipitationProbabilityMax = dailyPrecipitationProbabilityMax ?: emptyList(),
        dailyWindSpeedMax = dailyWindSpeedMax ?: emptyList(),
        dailyWindDirectionDominant = dailyWindDirectionDominant ?: emptyList(),
        dailyUvIndexMax = dailyUvIndexMax ?: emptyList()
    )
}
