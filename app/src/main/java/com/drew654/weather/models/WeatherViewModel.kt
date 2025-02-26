package com.drew654.weather.models

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.double
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class WeatherViewModel(application: Application) : AndroidViewModel(application) {
    private val _places = MutableStateFlow<List<Place>>(emptyList())
    val places: StateFlow<List<Place>> = _places.asStateFlow()

    private val _nominatimResponse = MutableStateFlow<List<Place>>(emptyList())
    val nominatimResponse: StateFlow<List<Place>> = _nominatimResponse.asStateFlow()

    private val _forecast = MutableStateFlow<Forecast?>(null)
    val forecast: StateFlow<Forecast?> = _forecast.asStateFlow()

    private val _currentWeather = MutableStateFlow<CurrentWeather?>(null)
    val currentWeather: StateFlow<CurrentWeather?> = _currentWeather.asStateFlow()

    fun addPlace(place: Place) {
        _places.value = _places.value + place
    }

    fun removePlace(place: Place) {
        _places.value = _places.value - place
    }

    fun getPlace(id: String): Place? {
        return _places.value.find { it.id == id }
    }

    fun searchNominatim(name: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val client = OkHttpClient()
                val url = "https://nominatim.openstreetmap.org/search?q=$name&format=json"
                val request = Request.Builder()
                    .url(url)
                    .build()

                try {
                    client.newCall(request).execute().use { response ->
                        if (!response.isSuccessful) {
                            throw IOException("Unexpected code $response")
                        }

                        val responseBody = response.body?.string() ?: ""
                        val jsonArray = Json.parseToJsonElement(responseBody).jsonArray
                        if (jsonArray.isNotEmpty()) {
                            val places = jsonArray.map {
                                val latitude = it.jsonObject["lat"]?.jsonPrimitive?.double
                                val longitude = it.jsonObject["lon"]?.jsonPrimitive?.double
                                val name = it.jsonObject["display_name"]?.jsonPrimitive?.content
                                Place(name ?: "", latitude ?: 0.0, longitude ?: 0.0)
                            }
                            _nominatimResponse.value = places
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun clearNominatimResponse() {
        _nominatimResponse.value = emptyList()
    }

    fun fetchForecast(place: Place) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val client = OkHttpClient()
                val url =
                    "https://api.open-meteo.com/v1/forecast?latitude=${place.latitude}&longitude=${place.longitude}&hourly=temperature_2m,weather_code&temperature_unit=fahrenheit&timezone=auto&forecast_days=1"
                val request = Request.Builder()
                    .url(url)
                    .build()

                try {
                    client.newCall(request).execute().use { response ->
                        if (!response.isSuccessful) {
                            throw IOException("Unexpected code $response")
                        }

                        val responseBody = response.body?.string() ?: ""
                        val jsonObject = Json.parseToJsonElement(responseBody).jsonObject
                        val hourly = jsonObject["hourly"]?.jsonObject
                        val time = hourly?.get("time")?.jsonArray
                        val temperature = hourly?.get("temperature_2m")?.jsonArray
                        val weatherCode = hourly?.get("weather_code")?.jsonArray
                        val formatter = DateTimeFormatter.ISO_DATE_TIME
                        val hourlyTemperature = time?.mapIndexed { index, element ->
                            val localDateTime =
                                LocalDateTime.parse(element.jsonPrimitive.content, formatter)
                            val temperatureValue =
                                temperature?.get(index)?.jsonPrimitive?.double ?: 0.0
                            Pair(localDateTime, temperatureValue)
                        }
                        val hourlyWeatherCode = time?.mapIndexed { index, element ->
                            val localDateTime =
                                LocalDateTime.parse(element.jsonPrimitive.content, formatter)
                            val weatherCodeValue =
                                weatherCode?.get(index)?.jsonPrimitive?.int ?: 0
                            Pair(localDateTime, weatherCodeValue)
                        }

                        _forecast.value = Forecast(
                            hourlyTemperature = hourlyTemperature ?: emptyList(),
                            hourlyWeatherCode = hourlyWeatherCode ?: emptyList()
                        )
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun fetchCurrentWeather(place: Place) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val client = OkHttpClient()
                val url = "https://api.open-meteo.com/v1/forecast?latitude=${place.latitude}&longitude=${place.longitude}&current=temperature_2m,relative_humidity_2m,apparent_temperature,is_day,precipitation,rain,showers,snowfall,weather_code,wind_speed_10m,wind_direction_10m,wind_gusts_10m&temperature_unit=fahrenheit&wind_speed_unit=mph&precipitation_unit=inch&forecast_days=1"
                val request = Request.Builder()
                    .url(url)
                    .build()

                try {
                    client.newCall(request).execute().use { response ->
                        if (!response.isSuccessful) {
                            throw IOException("Unexpected code $response")
                        }

                        val responseBody = response.body?.string() ?: ""
                        val jsonObject = Json.parseToJsonElement(responseBody).jsonObject
                        val current = jsonObject["current"]?.jsonObject
                        val temperature = current?.get("temperature_2m")?.jsonPrimitive?.double
                        val relativeHumidity = current?.get("relative_humidity_2m")?.jsonPrimitive?.double
                        val apparentTemperature = current?.get("apparent_temperature")?.jsonPrimitive?.double
                        val isDay = current?.get("is_day")?.jsonPrimitive?.int == 1
                        val precipitation = current?.get("precipitation")?.jsonPrimitive?.double
                        val rain = current?.get("rain")?.jsonPrimitive?.double
                        val showers = current?.get("showers")?.jsonPrimitive?.double
                        val snowfall = current?.get("snowfall")?.jsonPrimitive?.double
                        val weatherCode = current?.get("weather_code")?.jsonPrimitive?.int
                        val windSpeed = current?.get("wind_speed_10m")?.jsonPrimitive?.double
                        val windDirection = current?.get("wind_direction_10m")?.jsonPrimitive?.int
                        val windGusts = current?.get("wind_gusts_10m")?.jsonPrimitive?.double

                        _currentWeather.value = CurrentWeather(
                            temperature = temperature ?: 0.0,
                            relativeHumidity = relativeHumidity ?: 0.0,
                            apparentTemperature = apparentTemperature ?: 0.0,
                            isDay = isDay == true,
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
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
}
