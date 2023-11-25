package com.lunesu.pengchauferry

import org.joda.time.LocalDate

open class HolidayRepository(db: DbOpenHelper) {
    companion object {
        private val BUDDHA = LocalDate(Constants.YEAR, 4, 30)
    }

    private val holidayDao = HolidayDao(db)

    open fun getHoliday(day: LocalDate): Boolean {
        return holidayDao.query(day)
    }

    open fun setHoliday(day: LocalDate, isHoliday: Boolean) {
        if (isHoliday) {
            holidayDao.insert(day)
        } else {
            holidayDao.delete(day)
        }
    }

    open fun shouldRefresh(): Boolean {
        return !getHoliday(BUDDHA)
    }

    open suspend fun refresh() {
        runCatching {
            holidayDao.save(Utils.atLeast(HongKongHolidayFetcher.fetch(), 15) + BUDDHA)
        }
    }
}
