package com.lunesu.pengchauferry

import org.joda.time.LocalDate

class HolidaysRepository(db: DbOpenHelper) {
    private val holidays = HolidaysDao(db)

    fun getHoliday(day: LocalDate): Boolean {
        return holidays.query(day)
    }

    fun setHoliday(day: LocalDate, isHoliday: Boolean) {
        if (isHoliday) {
            holidays.insert(day)
        } else {
            holidays.delete(day)
        }
    }

    suspend fun refresh() {
        holidays.save(HongKongHolidaysFetcher.fetch())
    }

}