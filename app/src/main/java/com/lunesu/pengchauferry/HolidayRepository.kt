package com.lunesu.pengchauferry

import org.joda.time.LocalDate

open class HolidayRepository(db: DbOpenHelper) {
    companion object {
        private val BUDDHA = LocalDate(HongKongHolidayFetcher.YEAR, 4, 30)
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
        return !getHoliday(BUDDHA)
    }

    open suspend fun refresh() {
        runCatching {
            holidaysDao.save(Utils.atLeast(HongKongHolidayFetcher.fetch(), 15) + BUDDHA)
        }
    }

}