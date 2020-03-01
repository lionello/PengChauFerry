package com.lunesu.pengchauferry

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.joda.time.Duration
import org.joda.time.LocalTime
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FerryDaoTest {
    private val db = DbOpenHelper(null)
    private val ferry = Ferry(LocalTime.MIDNIGHT, FerryPier.MuiWo, FerryPier.PengChau, Duration.ZERO, FerryDay.MondayToSaturday, "1.2", FerryPier.HeiLingChau)

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun testQuery() {
        val dao = FerryDao(db)
        assertEquals(emptyList<Ferry>(), dao.query(FerryPier.MuiWo, FerryDay.Holiday))
    }

    @Test
    fun testInsert() {
        val dao = FerryDao(db)
        dao.insert(ferry)
        assertEquals(listOf(ferry), dao.query(ferry.from, FerryDay.Monday))
    }

    @Test
    fun testQueryFilter() {
        val dao = FerryDao(db)
        dao.insert(ferry)
        assertEquals(emptyList<Ferry>(), dao.query(ferry.from, FerryDay.Holiday))
        assertEquals(emptyList<Ferry>(), dao.query(FerryPier.ChiMaWan, FerryDay.Monday))
    }

    @Test
    fun testInsertTwice() {
        val dao = FerryDao(db)
        dao.insert(ferry)
        dao.insert(ferry)
    }

    @Test
    fun testDelete() {
        val dao = FerryDao(db)
        dao.insert(ferry)
        dao.delete(ferry.from, ferry.to)
        assertEquals(emptyList<Ferry>(), dao.query(ferry.from, FerryDay.Monday))
    }

    @Test
    fun testSave() {
        val dao = FerryDao(db)
        dao.save(listOf(ferry), ferry.from, ferry.to)
        assertEquals(listOf(ferry), dao.query(FerryPier.MuiWo, FerryDay.Monday))
    }
}