package com.drew654.weather.models

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.drew654.weather.data.PlaceListSerializer
import com.drew654.weather.data.jsonToCurrentWeather
import com.drew654.weather.data.jsonToDailyForecast
import com.drew654.weather.data.jsonToForecast
import com.drew654.weather.models.MeasurementUnit.Companion.getDataNameFromDisplayName
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.double
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import kotlin.collections.plus
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException


class WeatherViewModel(application: Application) : AndroidViewModel(application) {
    private val dataStore: DataStore<List<Place>> = application.dataStore

    private val _places = MutableStateFlow<List<Place>>(emptyList())
    val places: StateFlow<List<Place>> = _places.asStateFlow()

    private val _selectedPlace = MutableStateFlow<Place?>(null)
    val selectedPlace: StateFlow<Place?> = _selectedPlace.asStateFlow()

    private val _searchPlaceName = MutableStateFlow("")
    val searchPlaceName: StateFlow<String> = _searchPlaceName.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching.asStateFlow()

    private val _fetchedPlaces = MutableStateFlow<List<Place>>(emptyList())
    val fetchedPlaces: StateFlow<List<Place>> = _fetchedPlaces.asStateFlow()

    private val _forecast = MutableStateFlow<Forecast?>(null)
    val forecast: StateFlow<Forecast?> = _forecast.asStateFlow()

    private val _currentWeather = MutableStateFlow<CurrentWeather?>(null)
    val currentWeather: StateFlow<CurrentWeather?> = _currentWeather.asStateFlow()

    private val _dailyForecast = MutableStateFlow<DailyForecast?>(null)
    val dailyForecast: StateFlow<DailyForecast?> = _dailyForecast.asStateFlow()

    private val _selectedDay = MutableStateFlow(0)
    val selectedDay: StateFlow<Int> = _selectedDay.asStateFlow()

    private val _currentLocation = MutableStateFlow<Location?>(null)
    val currentLocation: StateFlow<Location?> = _currentLocation.asStateFlow()

    private val preferencesDataStore: DataStore<Preferences> = application.preferencesDataStore

    companion object {
        val swipeToChangeTabs = booleanPreferencesKey("swipe_to_change_tabs")
        val temperatureUnit = stringPreferencesKey("temperature_unit")
        val windSpeedUnit = stringPreferencesKey("wind_speed_unit")
    }

    val swipeToChangeTabsFlow: Flow<Boolean> = preferencesDataStore.data.map { preferences ->
        preferences[swipeToChangeTabs] == true
    }

    val temperatureUnitFlow: Flow<String> = preferencesDataStore.data.map { preferences ->
        preferences[temperatureUnit] ?: MeasurementUnit.Fahrenheit.dataName
    }

    val windSpeedUnitFlow: Flow<String> = preferencesDataStore.data.map { preferences ->
        preferences[windSpeedUnit] ?: MeasurementUnit.Mph.dataName
    }

    init {
        viewModelScope.launch {
            dataStore.data.collect { savedPlaces ->
                _places.update { savedPlaces }
                getCurrentLocationWeather()
            }
        }
    }

    suspend fun getCurrentLocationWeather() {
        if (hasLocationPermission()) {
            val location = getCurrentLocation()
            if (location != null) {
                _currentLocation.value = location
                val place = Place(
                    name = "Current Location",
                    latitude = location.latitude,
                    longitude = location.longitude
                )
                setSelectedPlace(place)
                fetchWeather()
            }
        }
    }

    fun hasLocationPermission(): Boolean {
        val context = getApplication<Application>().applicationContext
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private var searchJob: Job? = null

    private var fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(application)

    @OptIn(ExperimentalCoroutinesApi::class)
    private suspend fun getLastKnownLocation(): Location? =
        suspendCancellableCoroutine { continuation ->
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    continuation.resume(location)
                }
                .addOnFailureListener { e: Exception ->
                    continuation.resumeWithException(e)
                }
                .addOnCanceledListener {
                    continuation.cancel()
                }
        }

    private suspend fun getCurrentLocation(): Location? {
        if (hasLocationPermission()) {
            try {
                return getLastKnownLocation()
            } catch (e: Exception) {
                Log.e("Location", "Error getting location", e)
            }
        }
        return null
    }

    fun addPlace(place: Place) {
        _places.update { it + place }
        savePlaces()
    }

    fun removePlace(place: Place) {
        _places.update { it - place }
        savePlaces()
    }

    fun setSearchPlaceName(name: String) {
        _searchPlaceName.value = name
    }

    fun setIsSearching(isSearching: Boolean) {
        _isSearching.value = isSearching
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
            fetchDailyForecast(_selectedPlace.value!!)
        }
    }

    fun clearWeather() {
        _forecast.value = null
        _currentWeather.value = null
        _dailyForecast.value = null
    }

    fun clearFetchedPlaces() {
        _fetchedPlaces.value = emptyList()
    }

    fun getSelectedPlace(): Place? {
        return _selectedPlace.value
    }

    fun fetchPlaces(name: String) {
        searchJob?.cancel()

        searchJob = viewModelScope.launch {
            delay(300)
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
                            _fetchedPlaces.value = places
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun buildWeatherRequest(
        place: Place,
        weatherDataType: WeatherDataType,
        weatherDataFields: List<String>,
        options: List<String> = emptyList(),
        temperatureUnit: String? = null,
        windSpeedUnit: String? = null,
        precipitationUnit: String? = null
    ): Request {
        val url = "https://api.open-meteo.com/v1/forecast?latitude=${place.latitude}&longitude=${place.longitude}&${weatherDataType.value}=${weatherDataFields.joinToString(",")}${if (options.isNotEmpty()) "&" else ""}${options.joinToString("&")}${if (temperatureUnit != null) "&temperature_unit=$temperatureUnit" else ""}${if (windSpeedUnit != null) "&wind_speed_unit=$windSpeedUnit" else ""}${if (precipitationUnit != null) "&precipitation_unit=$precipitationUnit" else ""}"
        val request = Request.Builder()
            .url(url)
            .build()
        return request
    }

    private fun fetchForecast(place: Place) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val client = OkHttpClient()
                val selectedTemperatureUnit = temperatureUnitFlow.first().lowercase()
                val selectedWindSpeedUnit = getDataNameFromDisplayName(windSpeedUnitFlow.first())
                val request = buildWeatherRequest(
                    place,
                    WeatherDataType.HOURLY,
                    listOf("temperature_2m", "precipitation_probability", "weather_code", "wind_speed_10m", "wind_direction_10m"),
                    listOf("forecast_days=15", "timezone=auto"),
                    selectedTemperatureUnit,
                    selectedWindSpeedUnit
                )

                try {
                    client.newCall(request).execute().use { response ->
                        if (!response.isSuccessful) {
                            throw IOException("Unexpected code $response")
                        }

                        val responseBody = response.body?.string() ?: ""
                        val jsonObject = Json.parseToJsonElement(responseBody).jsonObject
                        val hourly = jsonObject["hourly"]?.jsonObject

                        _forecast.value = jsonToForecast(hourly ?: jsonObject)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun fetchCurrentWeather(place: Place) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val client = OkHttpClient()
                val selectedTemperatureUnit = temperatureUnitFlow.first().lowercase()
                val selectedWindSpeedUnit = getDataNameFromDisplayName(windSpeedUnitFlow.first())
                val request = buildWeatherRequest(
                    place,
                    WeatherDataType.CURRENT,
                    listOf("temperature_2m", "relative_humidity_2m", "dew_point_2m", "apparent_temperature", "is_day", "precipitation", "rain", "showers", "snowfall", "weather_code", "wind_speed_10m", "wind_direction_10m", "wind_gusts_10m"),
                    emptyList(),
                    selectedTemperatureUnit,
                    selectedWindSpeedUnit,
                    "inch"
                )

                try {
                    client.newCall(request).execute().use { response ->
                        if (!response.isSuccessful) {
                            throw IOException("Unexpected code $response")
                        }

                        val responseBody = response.body?.string() ?: ""
                        val jsonObject = Json.parseToJsonElement(responseBody).jsonObject
                        val current = jsonObject["current"]?.jsonObject

                        _currentWeather.value = jsonToCurrentWeather(current ?: jsonObject)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun fetchDailyForecast(place: Place) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val client = OkHttpClient()
                val selectedTemperatureUnit = temperatureUnitFlow.first().lowercase()
                val selectedWindSpeedUnit = getDataNameFromDisplayName(windSpeedUnitFlow.first())
                val request = buildWeatherRequest(
                    place,
                    WeatherDataType.DAILY,
                    listOf("temperature_2m_max", "temperature_2m_min", "sunrise", "sunset", "weather_code", "precipitation_probability_max", "wind_speed_10m_max", "wind_direction_10m_dominant", "uv_index_max"),
                    listOf("forecast_days=15", "timezone=auto"),
                    selectedTemperatureUnit,
                    selectedWindSpeedUnit,
                    "inch"
                )

                try {
                    client.newCall(request).execute().use { response ->
                        if (!response.isSuccessful) {
                            throw IOException("Unexpected code $response")
                        }

                        val responseBody = response.body?.string() ?: ""
                        val jsonObject = Json.parseToJsonElement(responseBody).jsonObject
                        val dailyForecast = jsonObject["daily"]?.jsonObject

                        _dailyForecast.value = jsonToDailyForecast(dailyForecast ?: jsonObject)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun setSelectedDay(day: Int) {
        _selectedDay.value = day
    }

    suspend fun updateSwipeToChangeTabs(value: Boolean) {
        preferencesDataStore.edit { preferences ->
            preferences[swipeToChangeTabs] = value
        }
    }

    suspend fun updateTemperatureUnit(value: String) {
        preferencesDataStore.edit { preferences ->
            preferences[temperatureUnit] = value
        }
    }

    suspend fun updateWindSpeedUnit(value: String) {
        preferencesDataStore.edit { preferences ->
            preferences[windSpeedUnit] = value
        }
    }
}

val Application.dataStore by dataStore(
    fileName = "places.json",
    serializer = PlaceListSerializer
)

val Application.preferencesDataStore by preferencesDataStore(
    name = "preferences"
)
