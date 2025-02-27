package com.drew654.weather.data

import androidx.datastore.core.Serializer
import com.drew654.weather.models.Place
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream


object PlaceListSerializer : Serializer<List<Place>> {
    override val defaultValue: List<Place>
        get() = emptyList()

    override suspend fun readFrom(input: InputStream): List<Place> {
        return try {
            Json.decodeFromString(
                deserializer = kotlinx.serialization.builtins.ListSerializer(Place.serializer()),
                string = input.readBytes().decodeToString()
            )
        } catch (e: SerializationException) {
            e.printStackTrace()
            emptyList()
        }
    }

    override suspend fun writeTo(t: List<Place>, output: OutputStream) {
        output.write(
            Json.encodeToString(
                serializer = kotlinx.serialization.builtins.ListSerializer(Place.serializer()),
                value = t
            ).encodeToByteArray()
        )
    }
}
