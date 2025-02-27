package com.drew654.weather.models

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
class Place(
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val id: String = UUID.randomUUID().toString()
)
