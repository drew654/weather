package com.drew654.weather.models

import java.util.UUID

class Place(
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val id: String = UUID.randomUUID().toString()
)
