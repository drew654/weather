package com.drew654.weather.utils

import android.content.Context
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

fun getBeaufortDescription(speed: Double): String {
    return when {
        speed >= 0.0 && speed < 1.0 -> "Calm"
        speed >= 1.0 && speed < 4.0 -> "Light air"
        speed >= 4.0 && speed < 8.0 -> "Light breeze"
        speed >= 8.0 && speed < 13.0 -> "Gentle breeze"
        speed >= 13.0 && speed < 19.0 -> "Moderate breeze"
        speed >= 19.0 && speed < 25.0 -> "Fresh breeze"
        speed >= 25.0 && speed < 32.0 -> "Strong breeze"
        speed >= 32.0 && speed < 39.0 -> "Near gale"
        speed >= 39.0 && speed < 47.0 -> "Gale"
        speed >= 47.0 && speed < 55.0 -> "Strong gale"
        speed >= 55.0 && speed < 64.0 -> "Whole gale"
        speed >= 64.0 && speed < 75.0 -> "Sorm force"
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
