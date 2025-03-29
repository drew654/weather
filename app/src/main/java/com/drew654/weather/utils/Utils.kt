package com.drew654.weather.utils

import android.content.Context
import com.drew654.weather.models.MeasurementUnit
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.io.IOException
import java.time.LocalDateTime

fun degToHdg(deg: Int): String {
    val directions = listOf(
        "N", "NNE", "NE", "ENE", "E", "ESE", "SE", "SSE",
        "S", "SSW", "SW", "WSW", "W", "WNW", "NW", "NNW"
    )
    val index = ((deg / 22.5) + 0.5).toInt() % 16
    return directions[index]
}

fun getBeaufortDescription(speed: Double, unit: String): String {
    val mphSpeed = when (unit) {
        MeasurementUnit.Mph.dataName -> speed
        MeasurementUnit.Kph.dataName -> kphToMph(speed)
        MeasurementUnit.Mps.dataName -> msToMph(speed)
        MeasurementUnit.Knots.dataName -> ktsToMph(speed)
        else -> speed
    }
    return when {
        mphSpeed >= 0.0 && mphSpeed < 1.0 -> "Calm"
        mphSpeed >= 1.0 && mphSpeed < 4.0 -> "Light air"
        mphSpeed >= 4.0 && mphSpeed < 8.0 -> "Light breeze"
        mphSpeed >= 8.0 && mphSpeed < 13.0 -> "Gentle breeze"
        mphSpeed >= 13.0 && mphSpeed < 19.0 -> "Moderate breeze"
        mphSpeed >= 19.0 && mphSpeed < 25.0 -> "Fresh breeze"
        mphSpeed >= 25.0 && mphSpeed < 32.0 -> "Strong breeze"
        mphSpeed >= 32.0 && mphSpeed < 39.0 -> "Near gale"
        mphSpeed >= 39.0 && mphSpeed < 47.0 -> "Gale"
        mphSpeed >= 47.0 && mphSpeed < 55.0 -> "Strong gale"
        mphSpeed >= 55.0 && mphSpeed < 64.0 -> "Whole gale"
        mphSpeed >= 64.0 && mphSpeed < 75.0 -> "Sorm force"
        else -> "Hurricane force"
    }
}

fun getWeatherIconUrl(weatherCode: Int, isDay: Boolean): String {
    return "file:///android_asset/images/${weatherCode}_${if (isDay) "day" else "night"}.jpg"
}

fun getWeatherDescription(context: Context, weatherCode: Int, isDay: Boolean): String {
    return try {
        val inputStream = context.assets.open("weather_code_descriptions.json")
        val descriptions = inputStream.bufferedReader().use { it.readText() }
        val jsonObject = Json.parseToJsonElement(descriptions).jsonObject
        jsonObject[weatherCode.toString()]
            ?.jsonObject[if (isDay) "day" else "night"]
            ?.jsonObject["description"]?.jsonPrimitive?.content
            ?: ""
    } catch (e: IOException) {
        e.printStackTrace()
        ""
    }
}

fun hourIsDay(hour: Int, sunrise: LocalDateTime, sunset: LocalDateTime): Boolean {
    return hour > sunrise.hour && hour <= sunset.hour
}

fun String.capitalizeWord(): String {
    return this.lowercase().replaceFirstChar { it.uppercase() }
}

fun calculateStartIndexForDay(targetDay: Int, currentHour: Int): Int {
    if (targetDay == 0) {
        return if (currentHour == 23) 0 else 1
    }
    return if (currentHour == 23) {
        targetDay * 24 + 1
    } else {
        targetDay * 24 + 2
    }
}

fun mphToKph(mph: Double): Double {
    return mph * 1.60934
}

fun kphToMph(kph: Double): Double {
    return kph / 1.60934
}

fun mphToMs(mph: Double): Double {
    return mph / 2.23694
}

fun msToMph(ms: Double): Double {
    return ms * 2.23694
}

fun mphToKts(mph: Double): Double {
    return mph / 1.15078
}

fun ktsToMph(kts: Double): Double {
    return kts * 1.15078
}
