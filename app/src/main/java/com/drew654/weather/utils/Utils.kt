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
