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
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

class WeatherViewModel(application: Application) : AndroidViewModel(application) {
    private val _places = MutableStateFlow<List<Place>>(emptyList())
    val places: StateFlow<List<Place>> = _places.asStateFlow()

    private val _nominatimResponse = MutableStateFlow<List<Place>>(emptyList())
    val nominatimResponse: StateFlow<List<Place>> = _nominatimResponse.asStateFlow()

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
}
