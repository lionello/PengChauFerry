package com.lunesu.pengchauferry

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.joda.time.LocalDate
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HolidayDaoTest {
    private val db = DbOpenHelper(null)
    private val today = LocalDate.now()

    @Test
    fun testQuery() {
        val dao = HolidayDao(db)
        assertFalse(dao.query(today))
    }

    @Test
    fun testInsert() {
        val dao = HolidayDao(db)
        dao.insert(today)
        assertTrue(dao.query(today))
    }

    @Test
    fun testInsertTwice() {
        val dao = HolidayDao(db)
        dao.insert(today)
        dao.insert(today)
    }

    @Test
    fun testDelete() {
        val dao = HolidayDao(db)
        dao.insert(today)
        dao.delete(today)
        assertFalse(dao.query(today))
    }

    @Test
    fun testSave() {
        val dao = HolidayDao(db)
        dao.save(listOf(today))
        assertTrue(dao.query(today))
    }
}