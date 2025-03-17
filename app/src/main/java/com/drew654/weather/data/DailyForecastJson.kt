package com.drew654.weather.data

import com.drew654.weather.models.DailyForecast
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.double
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonPrimitive
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun jsonToDailyForecast(jsonObject: JsonObject): DailyForecast {
    val days = jsonObject["time"]?.jsonArray
    val maxTemperatures = jsonObject["temperature_2m_max"]?.jsonArray
    val minTemperatures = jsonObject["temperature_2m_min"]?.jsonArray
    val sunrises = jsonObject["sunrise"]?.jsonArray
    val sunsets = jsonObject["sunset"]?.jsonArray
    val dailyWeatherCodes = jsonObject["weather_code"]?.jsonArray
    val dailyPrecipitationProbabilityMaxes = jsonObject["precipitation_probability_max"]?.jsonArray
    val dailyWindSpeedMaxes = jsonObject["wind_speed_10m_max"]?.jsonArray
    val dailyWindDirectionDominants = jsonObject["wind_direction_10m_dominant"]?.jsonArray
    val dailyUvIndexMaxes = jsonObject["uv_index_max"]?.jsonArray

    val day = days?.mapIndexed { index, element ->
        days[index].jsonPrimitive.content
    }?.map {
        LocalDate.parse(it, DateTimeFormatter.ISO_DATE)
    }
    val maxTemperature = maxTemperatures?.mapIndexed { index, element ->
        maxTemperatures[index].jsonPrimitive.double
    }
    val minTemperature = minTemperatures?.mapIndexed { index, element ->
        minTemperatures[index].jsonPrimitive.double
    }
    val dailySunrise = sunrises?.mapIndexed { index, element ->
        sunrises[index].jsonPrimitive.content
    }?.map {
        LocalDateTime.parse(it, DateTimeFormatter.ISO_DATE_TIME)
    }
    val dailySunset = sunsets?.mapIndexed { index, element ->
        sunsets[index].jsonPrimitive.content
    }?.map {
        LocalDateTime.parse(it, DateTimeFormatter.ISO_DATE_TIME)
    }
    val dailyWeatherCode = dailyWeatherCodes?.mapIndexed { index, element ->
        dailyWeatherCodes[index].jsonPrimitive.int
    }
    val dailyPrecipitationProbabilityMax = dailyPrecipitationProbabilityMaxes?.mapIndexed { index, element ->
        dailyPrecipitationProbabilityMaxes[index].jsonPrimitive.int
    }
    val dailyWindSpeedMax = dailyWindSpeedMaxes?.mapIndexed { index, element ->
        dailyWindSpeedMaxes[index].jsonPrimitive.double
    }
    val dailyWindDirectionDominant = dailyWindDirectionDominants?.mapIndexed { index, element ->
        dailyWindDirectionDominants[index].jsonPrimitive.int
    }
    val dailyUvIndexMax = dailyUvIndexMaxes?.mapIndexed { index, element ->
        dailyUvIndexMaxes[index].jsonPrimitive.double
    }

    return DailyForecast(
        day = day ?: emptyList(),
        dailyMaxTemperature = maxTemperature ?: emptyList(),
        dailyMinTemperature = minTemperature ?: emptyList(),
        dailySunrise = dailySunrise ?: emptyList(),
        dailySunset = dailySunset ?: emptyList(),
        dailyWeatherCode = dailyWeatherCode ?: emptyList(),
        dailyPrecipitationProbabilityMax = dailyPrecipitationProbabilityMax ?: emptyList(),
        dailyWindSpeedMax = dailyWindSpeedMax ?: emptyList(),
        dailyWindDirectionDominant = dailyWindDirectionDominant ?: emptyList(),
        dailyUvIndexMax = dailyUvIndexMax ?: emptyList()
    )
}
