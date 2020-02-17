package com.lunesu.pengchauferry

import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import org.joda.time.LocalDate
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HolidaysRepositoryTest {
    private val db = DbOpenHelper(null)
    private val newYear2020 = LocalDate(2020, 1, 1)

    @Test
    fun testHolidaysClean() {
        val repo = HolidaysRepository(db)
        assertFalse(repo.getHoliday(newYear2020))
    }

    @Test
    fun testHolidaysRefresh() {
        val repo = HolidaysRepository(db)
        runBlocking {
            repo.refresh()
        }
        assertTrue(repo.getHoliday(newYear2020))
    }

    @Test
    fun testHolidaysSet() {
        val repo = HolidaysRepository(db)

        repo.setHoliday(newYear2020, true)
        assertTrue(repo.getHoliday(newYear2020))

        repo.setHoliday(newYear2020, false)
        assertFalse(repo.getHoliday(newYear2020))
    }

}
