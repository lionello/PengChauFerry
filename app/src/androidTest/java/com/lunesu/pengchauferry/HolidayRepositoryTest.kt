package com.lunesu.pengchauferry

import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import org.joda.time.LocalDate
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HolidayRepositoryTest {
    private val db = DbOpenHelper(null)
    private val newYear2020 = LocalDate(2020, 1, 1)

    @Test
    fun testClean() {
        val repo = HolidayRepository(db)
        assertFalse(repo.getHoliday(newYear2020))
    }

    @Test
    fun testRefresh() = runBlocking {
        val repo = HolidayRepository(db)
        repo.refresh()
        assertTrue(repo.getHoliday(newYear2020))
    }

    @Test
    fun testShouldRefresh() = runBlocking {
        val repo = HolidayRepository(db)
        assertTrue(repo.shouldRefresh())
        repo.refresh()
        assertFalse(repo.shouldRefresh())
    }

    @Test
    fun testSet() {
        val repo = HolidayRepository(db)

        repo.setHoliday(newYear2020, true)
        assertTrue(repo.getHoliday(newYear2020))

        repo.setHoliday(newYear2020, false)
        assertFalse(repo.getHoliday(newYear2020))
    }

}
