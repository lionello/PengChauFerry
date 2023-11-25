package com.lunesu.pengchauferry

import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.runBlocking
import org.json.JSONArray
import org.json.JSONObject
import org.junit.Assert.assertNotEquals
import org.junit.Test

class OfflineParseTest {
    private val appContext = InstrumentationRegistry.getInstrumentation().context

    @Test
    fun testPengChauToCentral() {
        runBlocking {
            val html = Utils.jsoupLoad(appContext, "index.html")
            assertNotEquals(0, PengChauToCentralFetcher.parse(html).size)
        }
    }

    @Test
    fun testInterIslands() {
        runBlocking {
            val html = Utils.jsoupLoad(appContext, "index.html")
            assertNotEquals(0, InterIslandsFetcher.parse(html).size)
        }
    }

    @Test
    fun testKaito() {
        runBlocking {
            val html = Utils.jsoupLoad(appContext, "index.html")
            assertNotEquals(0, KaitoFetcher.parse(html).size)
        }
    }

    @Test
    fun testKaitoMuiWo() {
        runBlocking {
            val html = Utils.jsoupLoad(appContext, "index.html")
            assertNotEquals(0, KaitoMuiWoFetcher.parse(html).size)
        }
    }

    @Test
    fun testDumpJson() {
        runBlocking {
            val html = Utils.jsoupLoad(appContext, "index.html")
            val f = PengChauToCentralFetcher.parse(html) +
                InterIslandsFetcher.parse(html) +
                KaitoFetcher.parse(html) +
                KaitoMuiWoFetcher.parse(html)
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

            val h = HongKongHolidayFetcher.parse(Utils.jsoupLoad(appContext, "2023.htm"))
            assertNotEquals(0, h.size)
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
