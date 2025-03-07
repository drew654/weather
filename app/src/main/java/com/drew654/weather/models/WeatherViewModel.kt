package com.drew654.weather.models

import android.app.Application
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.drew654.weather.data.PlaceListSerializer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
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
import kotlin.collections.plus


class WeatherViewModel(application: Application) : AndroidViewModel(application) {
    private val dataStore: DataStore<List<Place>> = application.dataStore

    private val _places = MutableStateFlow<List<Place>>(emptyList())
    val places: StateFlow<List<Place>> = _places.asStateFlow()

    private val _selectedPlace = MutableStateFlow<Place?>(null)
    val selectedPlace: StateFlow<Place?> = _selectedPlace.asStateFlow()

    private val _nominatimResponse = MutableStateFlow<List<Place>>(emptyList())
    val nominatimResponse: StateFlow<List<Place>> = _nominatimResponse.asStateFlow()

    private val _forecast = MutableStateFlow<Forecast?>(null)
    val forecast: StateFlow<Forecast?> = _forecast.asStateFlow()

    private val _currentWeather = MutableStateFlow<CurrentWeather?>(null)
    val currentWeather: StateFlow<CurrentWeather?> = _currentWeather.asStateFlow()

    private val _dailyWeather = MutableStateFlow<DailyWeather?>(null)
    val dailyWeather: StateFlow<DailyWeather?> = _dailyWeather.asStateFlow()

    init {
        viewModelScope.launch {
            dataStore.data.collect { savedPlaces ->
                _places.update { savedPlaces }
                if (_places.value.isEmpty()) {
                    _places.update {
                        listOf(
                            Place(
                                name = "Reveille Memorial",
                                latitude = 30.6109683,
                                longitude = -96.3414112
                            )
                        )
                    }
                    savePlaces()
                }
                setSelectedPlace(_places.value[0])
                fetchCurrentWeather(_places.value[0])
                fetchForecast(_places.value[0])
                fetchDailyWeather(_places.value[0])
            }
        }
    }

    fun addPlace(place: Place) {
        _places.update { it + place }
        savePlaces()
    }

    fun removePlace(place: Place) {
        _places.update { it - place }
        savePlaces()
    }

    private fun savePlaces() {
        viewModelScope.launch {
            dataStore.updateData {
                _places.value
            }
        }
    }

    fun setSelectedPlace(place: Place) {
        _selectedPlace.value = place
    }

    fun movePlaceToFront(place: Place) {
        _places.update { places ->
            places.filter { it != place } + place
        }
        savePlaces()
    }

    fun fetchWeather() {
        if (_selectedPlace.value != null) {
            fetchCurrentWeather(_selectedPlace.value!!)
            fetchForecast(_selectedPlace.value!!)
            fetchDailyWeather(_selectedPlace.value!!)
        }
    }

    fun getSelectedPlace(): Place? {
        return _selectedPlace.value
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
                    "https://api.open-meteo.com/v1/forecast?latitude=${place.latitude}&longitude=${place.longitude}&hourly=temperature_2m,precipitation_probability,weather_code,wind_speed_10m,wind_direction_10m&temperature_unit=fahrenheit&timezone=auto&forecast_days=15"
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
                        val hours = hourly?.get("time")?.jsonArray
                        val temperatures = hourly?.get("temperature_2m")?.jsonArray
                        val weatherCodes = hourly?.get("weather_code")?.jsonArray
                        val precipitationProbabilities =
                            hourly?.get("precipitation_probability")?.jsonArray
                        val windSpeeds = hourly?.get("wind_speed_10m")?.jsonArray
                        val windDirections = hourly?.get("wind_direction_10m")?.jsonArray

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

                        _forecast.value = Forecast(
                            hour = hour ?: emptyList(),
                            hourlyTemperature = hourlyTemperature ?: emptyList(),
                            hourlyWeatherCode = hourlyWeatherCode ?: emptyList(),
                            hourlyPrecipitationProbability = hourlyPrecipitationProbabilities
                                ?: emptyList(),
                            hourlyWindSpeed = hourlyWindSpeed ?: emptyList(),
                            hourlyWindDirection = hourlyWindDirection ?: emptyList()
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
                val url =
                    "https://api.open-meteo.com/v1/forecast?latitude=${place.latitude}&longitude=${place.longitude}&current=temperature_2m,relative_humidity_2m,apparent_temperature,is_day,precipitation,rain,showers,snowfall,weather_code,wind_speed_10m,wind_direction_10m,wind_gusts_10m&temperature_unit=fahrenheit&wind_speed_unit=mph&precipitation_unit=inch&forecast_days=1"
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
                        val relativeHumidity =
                            current?.get("relative_humidity_2m")?.jsonPrimitive?.double
                        val apparentTemperature =
                            current?.get("apparent_temperature")?.jsonPrimitive?.double
                        val isDay = current?.get("is_day")?.jsonPrimitive?.int == 1
                        val precipitation = current?.get("precipitation")?.jsonPrimitive?.double
                        val rain = current?.get("rain")?.jsonPrimitive?.double
                        val showers = current?.get("showers")?.jsonPrimitive?.double
                        val snowfall = current?.get("snowfall")?.jsonPrimitive?.double
                        val weatherCode = current?.get("weather_code")?.jsonPrimitive?.int
                        val windSpeed = current?.get("wind_speed_10m")?.jsonPrimitive?.double
                        val windDirection =
                            current?.get("wind_direction_10m")?.jsonPrimitive?.int
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

    fun fetchDailyWeather(place: Place) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val client = OkHttpClient()
                val url =
                    "https://api.open-meteo.com/v1/forecast?latitude=${place.latitude}&longitude=${place.longitude}&daily=temperature_2m_max,temperature_2m_min,sunrise,sunset&temperature_unit=fahrenheit&wind_speed_unit=mph&precipitation_unit=inch&timezone=auto&forecast_days=1"
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
                        val daily = jsonObject["daily"]?.jsonObject
                        val maxTemperature = daily?.get("temperature_2m_max")?.jsonArray
                        val minTemperature = daily?.get("temperature_2m_min")?.jsonArray
                        val sunrise =
                            daily?.get("sunrise")?.jsonArray?.get(0)?.jsonPrimitive?.content
                        val sunset =
                            daily?.get("sunset")?.jsonArray?.get(0)?.jsonPrimitive?.content

                        _dailyWeather.value = DailyWeather(
                            maxTemperature = maxTemperature?.get(0)?.jsonPrimitive?.double
                                ?: 0.0,
                            minTemperature = minTemperature?.get(0)?.jsonPrimitive?.double
                                ?: 0.0,
                            sunrise = LocalDateTime.parse(
                                sunrise,
                                DateTimeFormatter.ISO_DATE_TIME
                            ),
                            sunset = LocalDateTime.parse(
                                sunset,
                                DateTimeFormatter.ISO_DATE_TIME
                            )
                        )
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
}

val Application.dataStore by dataStore(
    fileName = "places.json",
    serializer = PlaceListSerializer
)
