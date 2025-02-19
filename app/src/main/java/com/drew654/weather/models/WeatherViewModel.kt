package com.drew654.weather.models

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow


class WeatherViewModel(application: Application) : AndroidViewModel(application) {
    private val _places = MutableStateFlow<List<Place>>(emptyList())
    val places: StateFlow<List<Place>> = _places.asStateFlow()

    fun addPlace(place: Place) {
        _places.value = _places.value + place
    }

    fun removePlace(place: Place) {
        _places.value = _places.value - place
    }
}
