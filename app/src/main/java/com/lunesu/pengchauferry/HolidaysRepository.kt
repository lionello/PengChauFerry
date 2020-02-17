package com.lunesu.pengchauferry

import org.joda.time.LocalDate

class HolidaysRepository(db: DbOpenHelper) {
    private val holidaysDao = HolidaysDao(db)

    fun getHoliday(day: LocalDate): Boolean {
        return holidaysDao.query(day)
    }

    fun setHoliday(day: LocalDate, isHoliday: Boolean) {
        if (isHoliday) {
            holidaysDao.insert(day)
        } else {
            holidaysDao.delete(day)
        }
    }

    suspend fun refresh() {
        holidaysDao.save(HongKongHolidaysFetcher.fetch())
    }

}