package com.drew654.weather.models

sealed class MeasurementUnit(val dataName: String, val displayName: String) {
    data object Fahrenheit : MeasurementUnit(dataName = "fahrenheit", displayName = "Fahrenheit")

    data object Celsius : MeasurementUnit(dataName = "celsius", displayName = "Celsius")

    data object Mph : MeasurementUnit(dataName = "mph", displayName = "mph")

    data object Kph : MeasurementUnit(dataName = "kmh", displayName = "km/h")

    data object Knots : MeasurementUnit(dataName = "kn", displayName = "kn")

    data object Mps : MeasurementUnit(dataName = "ms", displayName = "m/s")

    data object Inch : MeasurementUnit(dataName = "inch", displayName = "in")

    data object Millimeter : MeasurementUnit(dataName = "mm", displayName = "mm")

    companion object {
        fun getDataNameFromDisplayName(displayName: String): String {
            return MeasurementUnit::class.sealedSubclasses.firstOrNull {
                it.objectInstance?.displayName == displayName
            }?.objectInstance?.dataName ?: ""
        }

        fun getDisplayNameFromDataName(dataName: String): String {
            return MeasurementUnit::class.sealedSubclasses.firstOrNull {
                it.objectInstance?.dataName == dataName
            }?.objectInstance?.displayName ?: ""
        }

        fun getObjectFromDataName(dataName: String): MeasurementUnit? {
            return MeasurementUnit::class.sealedSubclasses.firstOrNull {
                it.objectInstance?.dataName == dataName
            }?.objectInstance
        }
    }
}
