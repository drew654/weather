package com.drew654.weather.models

import kotlinx.serialization.Serializable

@Serializable
class Place(
    val name: String,
    val latitude: Double,
    val longitude: Double
)
