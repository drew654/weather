package com.drew654.weather.data

import com.drew654.weather.models.Forecast
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.double
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonPrimitive
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun jsonToForecast(jsonObject: JsonObject): Forecast {
    val hours = jsonObject["time"]?.jsonArray
    val temperatures = jsonObject["temperature_2m"]?.jsonArray
    val weatherCodes = jsonObject["weather_code"]?.jsonArray
    val precipitationProbabilities =
        jsonObject["precipitation_probability"]?.jsonArray
    val windSpeeds = jsonObject["wind_speed_10m"]?.jsonArray
    val windDirections = jsonObject["wind_direction_10m"]?.jsonArray

    val hour = hours?.mapIndexed { index, element ->
        hours[index].jsonPrimitive.content
    }?.map {
        LocalDateTime.parse(it, DateTimeFormatter.ISO_DATE_TIME)
    }
    val hourlyTemperature =
        temperatures?.mapIndexed { index, element ->
            temperatures[index].jsonPrimitive.double
        }
    val hourlyWeatherCode =
        weatherCodes?.mapIndexed { index, element ->
            weatherCodes[index].jsonPrimitive.int
        }
    val hourlyPrecipitationProbabilities =
        precipitationProbabilities?.mapIndexed { index, element ->
            precipitationProbabilities[index].jsonPrimitive.int
        }
    val hourlyWindSpeed =
        windSpeeds?.mapIndexed { index, element ->
            windSpeeds[index].jsonPrimitive.double
        }
    val hourlyWindDirection =
        windDirections?.mapIndexed { index, element ->
            windDirections[index].jsonPrimitive.int
        }

    return Forecast(
        hour = hour ?: emptyList(),
        hourlyTemperature = hourlyTemperature ?: emptyList(),
        hourlyWeatherCode = hourlyWeatherCode ?: emptyList(),
        hourlyPrecipitationProbability = hourlyPrecipitationProbabilities ?: emptyList(),
        hourlyWindSpeed = hourlyWindSpeed ?: emptyList(),
        hourlyWindDirection = hourlyWindDirection ?: emptyList()
    )
}
