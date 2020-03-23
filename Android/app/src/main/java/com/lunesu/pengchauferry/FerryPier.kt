package com.lunesu.pengchauferry

enum class FerryPier {
    Central,
    PengChau,
    TrappistMonastery,
    DiscoveryBay,
    MuiWo,
    CheungChau,
    ChiMaWan,
    HeiLingChau;

    val coordinate get() = COORDS.getValue(this)

    companion object {
        internal val ENUMS = values()

        val COORDS = mapOf(
            Central to Coordinate(22.2871, 114.1602),
            PengChau to Coordinate(22.2846, 114.0379),
            TrappistMonastery to Coordinate(22.2819, 114.0225),
            DiscoveryBay to Coordinate(22.2937, 114.0210),
            MuiWo to Coordinate(22.2647, 114.0019),
            CheungChau to Coordinate(22.2084, 114.0287),
            ChiMaWan to Coordinate(22.2393, 113.9995),
            HeiLingChau to Coordinate(22.2579, 114.0280)
        )

        fun findNearest(latitude: Double, longitude: Double, piers: Array<FerryPier> = ENUMS): FerryPier? {
            return piers.minBy { it.coordinate.distance(latitude, longitude) }
        }
    }

}