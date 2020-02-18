package com.lunesu.pengchauferry

import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertNotEquals
import org.junit.BeforeClass
import org.junit.Test

class OnlineFetchTest {
    companion object {
        @BeforeClass
        @JvmStatic
        fun setup() {
            // SSL verification fails: ignore the certs.
            TrustAllCertificates.setup()
        }
    }

    @Test
    fun testPengChauToCentral() {
        runBlocking {
            assertNotEquals(0, PengChauToCentralFetcher.fetch().size)
        }
    }

    @Test
    fun testKaito() {
        runBlocking {
            assertNotEquals(0, KaitoFetcher.fetch().size)
        }
    }

    @Test
    fun testInterIslands() {
        runBlocking {
            assertNotEquals(0, InterIslandsFetcher.fetch().size)
        }
    }

    @Test
    fun testHongKongHolidays() {
        runBlocking {
            assertNotEquals(0, HongKongHolidaysFetcher.fetch().size)
        }
    }
}
