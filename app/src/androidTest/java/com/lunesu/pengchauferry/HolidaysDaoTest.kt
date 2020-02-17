package com.lunesu.pengchauferry

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.joda.time.LocalDate
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HolidaysDaoTest {
    private val db = DbOpenHelper(null)
    private val today = LocalDate.now()

    @Test
    fun testQuery() {
        val dao = HolidaysDao(db)
        assertFalse(dao.query(today))
    }

    @Test
    fun testInsert() {
        val dao = HolidaysDao(db)
        dao.insert(today)
        assertTrue(dao.query(today))
    }

    @Test
    fun testDelete() {
        val dao = HolidaysDao(db)
        dao.insert(today)
        dao.delete(today)
        assertFalse(dao.query(today))
    }

    @Test
    fun testSave() {
        val dao = HolidaysDao(db)
        dao.save(listOf(today))
        assertTrue(dao.query(today))
    }
}