package com.lunesu.pengchauferry

import java.util.*

interface Fetcher<T> {

    companion object {
        val mondayToSaturday: EnumSet<FerryDay> = EnumSet.of(
            FerryDay.Monday,
            FerryDay.Tuesday,
            FerryDay.Wednesday,
            FerryDay.Thursday,
            FerryDay.Friday,
            FerryDay.Saturday
        )

        val sundayAndHolidays: EnumSet<FerryDay> =
            EnumSet.of(FerryDay.Sunday, FerryDay.Holiday)

        val everyday: EnumSet<FerryDay> = EnumSet.of(
            FerryDay.Monday,
            FerryDay.Tuesday,
            FerryDay.Wednesday,
            FerryDay.Thursday,
            FerryDay.Friday,
            FerryDay.Saturday,
            FerryDay.Sunday,
            FerryDay.Holiday
        )
    }

    suspend fun fetch(): List<T>
}
