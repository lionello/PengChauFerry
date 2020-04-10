package com.lunesu.pengchauferry

import java.lang.Math.toRadians
import kotlin.math.*

data class Coordinate(val latitude: Double, val longitude: Double) {
    companion object {
        private const val RADIUS = 6372.8e3F

        @Suppress("NonAsciiCharacters")
        private fun hav(Θ: Double) = sin(Θ / 2).pow(2)

        @Suppress("NonAsciiCharacters")
        private fun haversine(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Float {
            val φ1 = toRadians(lat1)
            val φ2 = toRadians(lat2)
            val λ1 = toRadians(lon1)
            val λ2 = toRadians(lon2)
            // From https://en.wikipedia.org/wiki/Haversine_formula
            return 2 * RADIUS * asin(sqrt(hav(φ1 - φ2) + cos(φ1) * cos(φ2) * hav(λ1 - λ2))).toFloat()
        }
    }

    fun distance(lat: Double, long: Double): Float {
//        if (!Utils.isEmulator) {
//            val result = floatArrayOf(0.0F)
//            Location.distanceBetween(lat, long, latitude, longitude, result) // not mocked
//            return result[0]
//        }
        return haversine(lat, long, latitude, longitude)
    }

}
