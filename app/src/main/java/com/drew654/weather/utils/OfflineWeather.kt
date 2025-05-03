package com.drew654.weather.utils

import android.content.Context
import com.drew654.weather.models.WeatherForecast
import java.io.IOException
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.math.round

object OfflineWeather {
    fun saveWeatherForecastJson(context: Context, json: String, fileName: String) {
        context.openFileOutput(fileName, Context.MODE_PRIVATE).use {
            it.write(json.toByteArray())
        }
    }

    fun loadWeatherForecastJson(context: Context, fileName: String): String? {
        return try {
            context.openFileInput(fileName).bufferedReader().use {
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

    fun getOfflineTemperature(
        weatherForecast: WeatherForecast,
        currentTime: LocalDateTime
    ): Double? {
        val (index1, index2, percentage) = getIndicesAndPercentage(
            weatherForecast.hours,
            currentTime
        )
            ?: return null
        val temperature =
            weatherForecast.hourlyTemperature[index1] + (weatherForecast.hourlyTemperature[index2] - weatherForecast.hourlyTemperature[index1]) * percentage / 100
        return round(temperature * 10) / 10
    }

    fun getOfflineRelativeHumidity(
        weatherForecast: WeatherForecast,
        currentTime: LocalDateTime
    ): Int? {
        val (index1, index2, percentage) = getIndicesAndPercentage(
            weatherForecast.hours,
            currentTime
        )
            ?: return null
        val relativeHumidity =
            weatherForecast.hourlyRelativeHumidity[index1] + (weatherForecast.hourlyRelativeHumidity[index2] - weatherForecast.hourlyRelativeHumidity[index1]) * percentage / 100
        return round(relativeHumidity).toInt()
    }

    fun getOfflineDewPoint(weatherForecast: WeatherForecast, currentTime: LocalDateTime): Double? {
        val (index1, index2, percentage) = getIndicesAndPercentage(
            weatherForecast.hours,
            currentTime
        )
            ?: return null
        val dewPoint =
            weatherForecast.hourlyDewPoint[index1] + (weatherForecast.hourlyDewPoint[index2] - weatherForecast.hourlyDewPoint[index1]) * percentage / 100
        return round(dewPoint * 10) / 10
    }

    fun getOfflineApparentTemperature(
        weatherForecast: WeatherForecast,
        currentTime: LocalDateTime
    ): Double? {
        val (index1, index2, percentage) = getIndicesAndPercentage(
            weatherForecast.hours,
            currentTime
        )
            ?: return null
        val apparentTemperature =
            weatherForecast.hourlyApparentTemperature[index1] + (weatherForecast.hourlyApparentTemperature[index2] - weatherForecast.hourlyApparentTemperature[index1]) * percentage / 100
        return round(apparentTemperature * 10) / 10
    }

    fun getOfflineWeatherCode(weatherForecast: WeatherForecast, currentTime: LocalDateTime): Int? {
        val index1 =
            getIndicesAndPercentage(weatherForecast.hours, currentTime)?.first ?: return null
        return weatherForecast.hourlyWeatherCode[index1]
    }

    fun getOfflinePrecipitationProbability(
        weatherForecast: WeatherForecast,
        currentTime: LocalDateTime
    ): Int? {
        val (index1, index2, percentage) = getIndicesAndPercentage(
            weatherForecast.hours,
            currentTime
        )
            ?: return null
        val precipitationProbability =
            weatherForecast.hourlyPrecipitationProbability[index1] + (weatherForecast.hourlyPrecipitationProbability[index2] - weatherForecast.hourlyPrecipitationProbability[index1]) * percentage / 100
        return round(precipitationProbability).toInt()
    }

    fun getOfflineWindSpeed(weatherForecast: WeatherForecast, currentTime: LocalDateTime): Double? {
        val (index1, index2, percentage) = getIndicesAndPercentage(
            weatherForecast.hours,
            currentTime
        )
            ?: return null
        val windSpeed =
            weatherForecast.hourlyWindSpeed[index1] + (weatherForecast.hourlyWindSpeed[index2] - weatherForecast.hourlyWindSpeed[index1]) * percentage / 100
        return round(windSpeed * 10) / 10
    }

    fun getOfflineWindDirection(
        weatherForecast: WeatherForecast,
        currentTime: LocalDateTime
    ): Int? {
        val (index1, index2, percentage) = getIndicesAndPercentage(
            weatherForecast.hours,
            currentTime
        )
            ?: return null
        val windDirection =
            weatherForecast.hourlyWindDirection[index1] + (weatherForecast.hourlyWindDirection[index2] - weatherForecast.hourlyWindDirection[index1]) * percentage / 100
        return round(windDirection).toInt()
    }

    fun getOfflineMaxTemperature(
        weatherForecast: WeatherForecast,
        currentTime: LocalDateTime
    ): Double? {
        val day = currentTime.toLocalDate()
        val index = weatherForecast.days.indexOf(day)
        return weatherForecast.dailyMaxTemperature[index]
    }

    fun getOfflineMinTemperature(
        weatherForecast: WeatherForecast,
        currentTime: LocalDateTime
    ): Double? {
        val day = currentTime.toLocalDate()
        val index = weatherForecast.days.indexOf(day)
        return weatherForecast.dailyMinTemperature[index]
    }

    fun getOfflineWeatherDays(
        weatherForecast: WeatherForecast,
        currentTime: LocalDateTime
    ): List<LocalDate> {
        val day = currentTime.toLocalDate()
        val index = weatherForecast.days.indexOf(day)
        return weatherForecast.days.subList(index, weatherForecast.days.size)
    }
}
