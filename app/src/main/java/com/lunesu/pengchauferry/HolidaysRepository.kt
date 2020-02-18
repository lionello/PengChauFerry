package com.lunesu.pengchauferry

import org.joda.time.LocalDate

open class HolidaysRepository(db: DbOpenHelper) {
    private val holidaysDao = HolidaysDao(db)

    open fun getHoliday(day: LocalDate): Boolean {
        return holidaysDao.query(day)
    }

    open fun setHoliday(day: LocalDate, isHoliday: Boolean) {
        if (isHoliday) {
            holidaysDao.insert(day)
        } else {
            holidaysDao.delete(day)
        }
    }

    open suspend fun refresh() {
        holidaysDao.save(HongKongHolidaysFetcher.fetch())
    }

}