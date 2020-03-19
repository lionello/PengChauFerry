package com.lunesu.pengchauferry

import android.location.Location
import java.lang.Math.toRadians
import kotlin.math.*

enum class FerryPier(val latitude: Double, val longitude: Double) {
    Central(22.2871, 114.1602),
    PengChau(22.2846, 114.0379),
    TrappistMonastery(22.2819, 114.0225),
    DiscoveryBay(22.2937, 114.0210),
    MuiWo(22.2647, 114.0019),
    CheungChau(22.2084, 114.0287),
    ChiMaWan(22.2393, 113.9995),
    HeiLingChau(22.2579, 114.0280);

    companion object {
        internal val ENUMS = values()

        fun findNearest(latitude: Double, longitude: Double, piers: Array<FerryPier> = ENUMS): FerryPier? {
            return piers.minBy { it.distance(latitude, longitude) }
        }

        private const val RADIUS = 6372.8e3F

        @Suppress("NonAsciiCharacters")
        private fun hav(Θ: Double) = sin(Θ / 2).pow(2)

        @Suppress("NonAsciiCharacters")
        private fun haversine(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Float {
            val φ1 = toRadians(lat1)
            val φ2 = toRadians(lat2)
            val λ1 = toRadians(lon1)
            val λ2 = toRadians(lon2)
            // From https://en.wikipedia.org/wiki/Haversine_formula; similar to Location.distanceBetween
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