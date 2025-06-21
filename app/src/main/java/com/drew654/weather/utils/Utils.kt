package com.drew654.weather.utils

import android.content.Context
import com.drew654.weather.models.MeasurementUnit
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.io.IOException
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

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

fun kphToMph(kph: Double): Double {
    return kph / 1.60934
}

fun kphToMs(kph: Double): Double {
    return kph / 3.6
}

fun kphToKts(kph: Double): Double {
    return kph / 1.852
}

fun msToMph(ms: Double): Double {
    return ms * 2.23694
}

fun ktsToMph(kts: Double): Double {
    return kts * 1.15078
}

fun cToF(c: Double): Double {
    return (c * 9 / 5) + 32
}

fun showDouble(value: Double, showDecimal: Boolean): String {
    return if (showDecimal) {
        value.toString()
    } else {
        value.roundToInt().toString()
    }
}

fun showTemperature(temperature: Double, unit: String, showDecimal: Boolean): String {
    return if (unit == MeasurementUnit.Fahrenheit.dataName) {
        showDouble(cToF(temperature), showDecimal)
    } else if (unit == MeasurementUnit.Celsius.dataName) {
        showDouble(temperature, showDecimal)
    } else {
        ""
    }
}

fun showWindSpeed(windSpeed: Double, unit: String, showDecimal: Boolean): String {
    return if (unit == MeasurementUnit.Kph.dataName) {
        showDouble(windSpeed, showDecimal)
    } else if (unit == MeasurementUnit.Mph.dataName) {
        showDouble(kphToMph(windSpeed), showDecimal)
    } else if (unit == MeasurementUnit.Mps.dataName) {
        showDouble(kphToMs(windSpeed), showDecimal)
    } else if (unit == MeasurementUnit.Knots.dataName) {
        showDouble(kphToKts(windSpeed), showDecimal)
    } else {
        ""
    }
}

fun formatHour(localDateTime: LocalDateTime?, is24HourFormat: Boolean): String {
    return localDateTime?.let {
        val pattern = if (is24HourFormat) "H" else "ha"
        val formatter = DateTimeFormatter.ofPattern(pattern, Locale.getDefault())
        val formattedTime = it.format(formatter)
        formattedTime.replace("AM", "a").replace("PM", "p")
    } ?: ""
}

fun formatTime(localDateTime: LocalDateTime?, is24HourFormat: Boolean): String {
    return localDateTime?.let {
        val pattern = if (is24HourFormat) "HH:mm" else "h:mm a"
        val formatter = DateTimeFormatter.ofPattern(pattern, Locale.getDefault())
        val formattedTime = it.format(formatter)
        formattedTime.replace("AM", "a").replace("PM", "p")
    } ?: ""
}

fun formatTime(hours: Int, minutes: Int, is24HourFormat: Boolean): String {
    val pattern = if (is24HourFormat) "HH:mm" else "h:mm a"
    val formatter = DateTimeFormatter.ofPattern(pattern, Locale.getDefault())
    val formattedTime = LocalDateTime.of(2023, 1, 1, hours, minutes).format(formatter)
    return formattedTime.replace("AM", "a").replace("PM", "p")
}

fun convertMillisToDate(millis: Long): String {
    val formatter = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
    return formatter.format(Date(millis))
}
