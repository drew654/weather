package com.drew654.weather.utils

import android.content.Context
import com.drew654.weather.models.MeasurementUnit
import com.drew654.weather.models.WeatherForecast
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.io.IOException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.round
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

fun showDouble(value: Double, showDecimal: Boolean): String {
    return if (showDecimal) {
        value.toString()
    } else {
        value.roundToInt().toString()
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

fun saveWeatherForecastJson(context: Context, json: String) {
    context.openFileOutput("weather_forecast.json", Context.MODE_PRIVATE).use {
        it.write(json.toByteArray())
    }
}

fun loadWeatherForecastJson(context: Context): String? {
    return try {
        context.openFileInput("weather_forecast.json").bufferedReader().use {
            it.readText()
        }
    } catch (e: IOException) {
        e.printStackTrace()
        null
    }
}

private fun getIndicesAndPercentage(
    hours: List<LocalDateTime>,
    currentTime: LocalDateTime
): Triple<Int, Int, Double>? {
    if (currentTime < hours[0] || currentTime > hours[hours.lastIndex]) {
        return null
    }

    var index1 = 0
    var index2 = 0
    var intervalFound = false

    for (i in 0 until hours.size - 1) {
        if (currentTime >= hours[i] && currentTime < hours[i + 1]) {
            index1 = i
            index2 = i + 1
            intervalFound = true
            break
        }
    }

    if (!intervalFound && currentTime == hours.last()) {
        index1 = hours.size - 2
        index2 = hours.size - 1
        intervalFound = true
    }

    if (!intervalFound) {
        return null
    }

    val time1 = hours[index1]
    val time2 = hours[index2]

    val totalDuration = java.time.temporal.ChronoUnit.MINUTES.between(time1, time2)
    val currentDuration = java.time.temporal.ChronoUnit.MINUTES.between(time1, currentTime)

    val percentage = currentDuration.toDouble() / totalDuration.toDouble() * 100

    return Triple(index1, index2, percentage)
}

fun getOfflineTemperature(weatherForecast: WeatherForecast, currentTime: LocalDateTime): Double? {
    val (index1, index2, percentage) = getIndicesAndPercentage(weatherForecast.hours, currentTime)
        ?: return null
    val temperature =
        weatherForecast.hourlyTemperature[index1] + (weatherForecast.hourlyTemperature[index2] - weatherForecast.hourlyTemperature[index1]) * percentage / 100
    return round(temperature * 10) / 10
}

fun getOfflineWeatherCode(weatherForecast: WeatherForecast, currentTime: LocalDateTime): Int? {
    val (index1, index2, percentage) = getIndicesAndPercentage(weatherForecast.hours, currentTime)
        ?: return null
    return weatherForecast.hourlyWeatherCode[index1]
}

fun getOfflinePrecipitationProbability(
    weatherForecast: WeatherForecast,
    currentTime: LocalDateTime
): Int? {
    val (index1, index2, percentage) = getIndicesAndPercentage(weatherForecast.hours, currentTime)
        ?: return null
    val precipitationProbability =
        weatherForecast.hourlyPrecipitationProbability[index1] + (weatherForecast.hourlyPrecipitationProbability[index2] - weatherForecast.hourlyPrecipitationProbability[index1]) * percentage / 100
    return round(precipitationProbability).toInt()
}

fun getOfflineWindSpeed(weatherForecast: WeatherForecast, currentTime: LocalDateTime): Double? {
    val (index1, index2, percentage) = getIndicesAndPercentage(weatherForecast.hours, currentTime)
        ?: return null
    val windSpeed = weatherForecast.hourlyWindSpeed[index1] + (weatherForecast.hourlyWindSpeed[index2] - weatherForecast.hourlyWindSpeed[index1]) * percentage / 100
    return round(windSpeed * 10) / 10
}

fun getOfflineWindDirection(weatherForecast: WeatherForecast, currentTime: LocalDateTime): Int? {
    val (index1, index2, percentage) = getIndicesAndPercentage(weatherForecast.hours, currentTime)
        ?: return null
    val windDirection = weatherForecast.hourlyWindDirection[index1] + (weatherForecast.hourlyWindDirection[index2] - weatherForecast.hourlyWindDirection[index1]) * percentage / 100
    return round(windDirection).toInt()
}
