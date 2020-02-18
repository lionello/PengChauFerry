package com.lunesu.pengchauferry

import org.joda.time.LocalDate

open class HolidayRepository(db: DbOpenHelper) {
    companion object {
        private val REFRESHED = LocalDate(1918, 5, 11)
    }

    private val holidaysDao = HolidayDao(db)

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

    open fun shouldRefresh(): Boolean {
        return !getHoliday(REFRESHED)
    }

    open suspend fun refresh() {
        holidaysDao.save(HongKongHolidayFetcher.fetch() + REFRESHED)
    }

}