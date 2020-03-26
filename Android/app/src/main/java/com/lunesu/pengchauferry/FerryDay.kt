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
        val ENUMS = values()

        fun fromDate(date: LocalDate) = ENUMS[date.dayOfWeek - 1]

        fun today() = fromDate(LocalDate.now())

        val MondayToSaturday: FerryDays = EnumSet.of(Monday, Tuesday, Wednesday, Thursday, Friday, Saturday)
        val SundayAndHolidays: FerryDays = EnumSet.of(Sunday, Holiday)
        val EVERYDAY: FerryDays = EnumSet.allOf(FerryDay::class.java)

        fun intToDays(int: Int): FerryDays =
            EnumSet.copyOf(ENUMS.mapNotNull { if ((int and it.flag) != 0) it else null })

        fun daysToInt(days: FerryDays) = days.sumBy { it.flag }
   }
}

typealias FerryDays = EnumSet<FerryDay>
