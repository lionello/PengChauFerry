package com.lunesu.pengchauferry

import org.joda.time.LocalDate
import java.util.*

enum class FerryDay(val flag: Int) {
    Monday(1),
    Tuesday(2),
    Wednesday(4),
    Thursday(8),
    Friday(16),
    Saturday(32),
    Sunday(64),
    Holiday(128);

    companion object {
        private val ENUMS = values()

        fun fromDate(date: LocalDate) = ENUMS[date.dayOfWeek - 1]

        fun today() = fromDate(LocalDate.now())

        fun tomorrow() = ENUMS[LocalDate.now().dayOfWeek % 7]

        fun intToDays(int: Int): EnumSet<FerryDay> =
            EnumSet.copyOf(ENUMS.mapIndexedNotNull { it, day -> if ((int and (1 shl it)) != 0) day else null })

        fun daysToInt(days: EnumSet<FerryDay>) =
            days.map { it.ordinal }.sumBy { 1 shl it }
   }
}