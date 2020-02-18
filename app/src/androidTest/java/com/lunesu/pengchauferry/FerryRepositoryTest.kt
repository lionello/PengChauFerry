package com.lunesu.pengchauferry

import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FerryRepositoryTest {
    private val db = DbOpenHelper(null)

    @Test
    fun testFerriesClean() {
        val repo = FerryRepository(db)
        assertEquals(0, repo.getFerries(FerryPier.PengChau, FerryDay.Monday).size)
    }

    @Test
    fun testFerriesRefresh() {
        val repo = FerryRepository(db)
        runBlocking {
            repo.refresh()
        }
        assertNotEquals(0, repo.getFerries(FerryPier.PengChau, FerryDay.Monday).size)
    }

}
