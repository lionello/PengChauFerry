package com.lunesu.pengchauferry

import kotlinx.coroutines.runBlocking
import org.json.JSONArray
import org.json.JSONObject
import org.junit.Assert.assertNotEquals
import org.junit.BeforeClass
import org.junit.Ignore
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
    fun testKaitoMuiWo() {
        runBlocking {
            assertNotEquals(0, KaitoMuiWoFetcher.fetch().size)
        }
    }

    @Test
    fun testHongKongHolidays() {
        runBlocking {
            assertNotEquals(0, HongKongHolidayFetcher.fetch().size)
        }
    }

    @Test
    @Ignore("Run this manually to export JSON")
    fun testDump() {
        runBlocking {
            val f = PengChauToCentralFetcher.fetch() +
                    InterIslandsFetcher.fetch() +
                    KaitoFetcher.fetch() +
                    KaitoMuiWoFetcher.fetch()
            val ferries = f.map {
                val o = JSONObject()
                o.put("time", it.time.toString())
                o.put("from", it.from.name)
                o.put("to", it.to.name)
                o.put("dur", it.dur.standardMinutes)
                o.put("days", FerryDay.daysToInt(it.days))
                o.put("fare", it.fare)
                o.put("via", it.via?.name)
                o
            }

            val h = HongKongHolidayFetcher.fetch()
            val holidays = h.map {
                it.toString()
            }

            val root = JSONObject()
            root.put("ferries", JSONArray(ferries))
            root.put("holidays", JSONArray(holidays))
            println(root.toString(2))
        }
    }
}
