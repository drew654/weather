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
import com.drew654.weather.data.jsonToWeatherForecast
import com.drew654.weather.utils.OfflineWeather.saveWeatherForecastJson
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
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
import kotlinx.coroutines.withTimeout
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.double
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.util.concurrent.TimeUnit
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

    private val _isTimedOut = MutableStateFlow(false)
    val isTimedOut: StateFlow<Boolean> = _isTimedOut.asStateFlow()

    private val _weatherForecast = MutableStateFlow<WeatherForecast?>(null)
    val weatherForecast: StateFlow<WeatherForecast?> = _weatherForecast.asStateFlow()

    private val _selectedDay = MutableStateFlow(0)
    val selectedDay: StateFlow<Int> = _selectedDay.asStateFlow()

    private val _currentLocation = MutableStateFlow<Location?>(null)
    val currentLocation: StateFlow<Location?> = _currentLocation.asStateFlow()

    private val _currentWeatherPage = MutableStateFlow(0)
    val currentWeatherPage: StateFlow<Int> = _currentWeatherPage.asStateFlow()

    private val preferencesDataStore: DataStore<Preferences> = application.preferencesDataStore

    companion object {
        val swipeToChangeTabs = booleanPreferencesKey("swipe_to_change_tabs")
        val showDecimal = booleanPreferencesKey("show_decimal")
        val temperatureUnit = stringPreferencesKey("temperature_unit")
        val windSpeedUnit = stringPreferencesKey("wind_speed_unit")
        val precipitationUnit = stringPreferencesKey("precipitation_unit")
    }

    val swipeToChangeTabsFlow: Flow<Boolean> = preferencesDataStore.data.map { preferences ->
        preferences[swipeToChangeTabs] == true
    }

    val showDecimalFlow: Flow<Boolean> = preferencesDataStore.data.map { preferences ->
        preferences[showDecimal] == true
    }

    val temperatureUnitFlow: Flow<String> = preferencesDataStore.data.map { preferences ->
        preferences[temperatureUnit] ?: MeasurementUnit.Fahrenheit.dataName
    }

    val windSpeedUnitFlow: Flow<String> = preferencesDataStore.data.map { preferences ->
        preferences[windSpeedUnit] ?: MeasurementUnit.Mph.dataName
    }

    val precipitationUnitFlow: Flow<String> = preferencesDataStore.data.map { preferences ->
        preferences[precipitationUnit] ?: MeasurementUnit.Inch.dataName
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
            fetchWeatherForecast(_selectedPlace.value!!)
        }
    }

    fun clearWeather() {
        _weatherForecast.value = null
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
        val url =
            "https://api.open-meteo.com/v1/forecast?latitude=${place.latitude}&longitude=${place.longitude}&${weatherDataType.value}=${
                weatherDataFields.joinToString(",")
            }${if (options.isNotEmpty()) "&" else ""}${options.joinToString("&")}${if (temperatureUnit != null) "&temperature_unit=$temperatureUnit" else ""}${if (windSpeedUnit != null) "&wind_speed_unit=$windSpeedUnit" else ""}${if (precipitationUnit != null) "&precipitation_unit=$precipitationUnit" else ""}"
        val request = Request.Builder()
            .url(url)
            .build()
        return request
    }

    private var fetchWeatherJob: Job? = null

    private fun fetchWeatherForecast(place: Place) {
        fetchWeatherJob?.cancel()
        fetchWeatherJob = viewModelScope.launch {
            _isTimedOut.value = false
            try {
                withTimeout(30_000) {
                    withContext(Dispatchers.IO) {
                        val client = OkHttpClient.Builder()
                            .connectTimeout(30, TimeUnit.SECONDS)
                            .readTimeout(30, TimeUnit.SECONDS)
                            .writeTimeout(30, TimeUnit.SECONDS)
                            .build()
                        val selectedTemperatureUnit = temperatureUnitFlow.first()
                        val selectedWindSpeedUnit = windSpeedUnitFlow.first()
                        val selectedPrecipitationUnit = precipitationUnitFlow.first()

                        val currentWeatherDeferred = async {
                            fetchWeatherJson(
                                client,
                                buildWeatherRequest(
                                    place,
                                    WeatherDataType.CURRENT,
                                    listOf(
                                        "temperature_2m",
                                        "relative_humidity_2m",
                                        "dew_point_2m",
                                        "apparent_temperature",
                                        "is_day",
                                        "precipitation",
                                        "rain",
                                        "showers",
                                        "snowfall",
                                        "weather_code",
                                        "wind_speed_10m",
                                        "wind_direction_10m"
                                    ),
                                    emptyList(),
                                    selectedTemperatureUnit,
                                    selectedWindSpeedUnit,
                                    selectedPrecipitationUnit
                                )
                            )?.jsonObject["current"]?.jsonObject
                        }

                        val hourlyForecastDeferred = async {
                            fetchWeatherJson(
                                client,
                                buildWeatherRequest(
                                    place,
                                    WeatherDataType.HOURLY,
                                    listOf(
                                        "temperature_2m",
                                        "precipitation_probability",
                                        "relative_humidity_2m",
                                        "dew_point_2m",
                                        "apparent_temperature",
                                        "weather_code",
                                        "wind_speed_10m",
                                        "wind_direction_10m"
                                    ),
                                    listOf("forecast_days=15", "timezone=auto"),
                                    selectedTemperatureUnit,
                                    selectedWindSpeedUnit
                                )
                            )?.jsonObject["hourly"]?.jsonObject
                        }

                        val dailyForecastDeferred = async {
                            fetchWeatherJson(
                                client,
                                buildWeatherRequest(
                                    place,
                                    WeatherDataType.DAILY,
                                    listOf(
                                        "temperature_2m_max",
                                        "temperature_2m_min",
                                        "sunrise",
                                        "sunset",
                                        "weather_code",
                                        "precipitation_probability_max",
                                        "wind_speed_10m_max",
                                        "wind_direction_10m_dominant",
                                        "uv_index_max"
                                    ),
                                    listOf("forecast_days=15", "timezone=auto"),
                                    selectedTemperatureUnit,
                                    selectedWindSpeedUnit,
                                    selectedPrecipitationUnit
                                )
                            )?.jsonObject["daily"]?.jsonObject
                        }

                        val (currentWeatherJson, hourlyForecastJson, dailyForecastJson) =
                            awaitAll(currentWeatherDeferred, hourlyForecastDeferred, dailyForecastDeferred)

                        if (currentWeatherJson != null && hourlyForecastJson != null && dailyForecastJson != null) {
                            val weatherForecastJson = buildJsonObject {
                                put("current", currentWeatherJson)
                                put("hourly", hourlyForecastJson)
                                put("daily", dailyForecastJson)
                            }
                            _weatherForecast.value = jsonToWeatherForecast(weatherForecastJson)
                            if (place.name != "Current Location") {
                                saveWeatherForecastJson(
                                    getApplication(),
                                    weatherForecastJson.toString(),
                                    "${place.name}.json"
                                )
                            }
                        }
                    }
                }
            } catch (e: TimeoutCancellationException) {
                _isTimedOut.value = true
                e.printStackTrace()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun fetchWeatherJson(client: OkHttpClient, request: Request): JsonObject? {
        return try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    throw IOException("Unexpected code $response")
                }

                val responseBody = response.body?.string() ?: ""
                Json.parseToJsonElement(responseBody).jsonObject
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun setSelectedDay(day: Int) {
        _selectedDay.value = day
    }

    fun setCurrentWeatherPage(page: Int) {
        _currentWeatherPage.value = page
    }

    suspend fun updateSwipeToChangeTabs(value: Boolean) {
        preferencesDataStore.edit { preferences ->
            preferences[swipeToChangeTabs] = value
        }
    }

    suspend fun updateShowDecimal(value: Boolean) {
        preferencesDataStore.edit { preferences ->
            preferences[showDecimal] = value
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

    suspend fun updatePrecipitationUnit(value: String) {
        preferencesDataStore.edit { preferences ->
            preferences[precipitationUnit] = value
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
