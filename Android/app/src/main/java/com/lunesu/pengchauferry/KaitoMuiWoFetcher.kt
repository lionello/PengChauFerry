package com.lunesu.pengchauferry

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.joda.time.Duration
import org.joda.time.LocalTime
import org.joda.time.format.DateTimeFormatterBuilder
import java.lang.IllegalArgumentException
import java.util.*

object KaitoMuiWoFetcher {
    private const val url = "https://www.td.gov.hk/en/transport_in_hong_kong/public_transport/ferries/service_details/"

    private const val fareSat = "12.0"
    private const val fareSun = "15.0"
    private val duration = Duration.standardMinutes(20)
    private val saturday = EnumSet.of(FerryDay.Saturday)
//    private val weekend = EnumSet.of(FerryDay.Saturday, FerryDay.Sunday, FerryDay.Holiday)
    private val formatter = DateTimeFormatterBuilder().appendPattern("h.mm a").toFormatter()

    private fun parse(times: MutableList<Ferry>, text: String, from: FerryPier, to: FerryPier) {
        val time =
            LocalTime.parse(text.trim('*', '#', '.').replace(".m", "m"), formatter)
        // # Operate on Sunday and public holiday only
        val sundaysOnly = text.contains('#')
        if (!sundaysOnly) {
            times.add(
                Ferry(time, from, to, duration, saturday, fareSat, null)
            )
        }
        // * Operate on Saturday (except public holiday) only
        val saturdaysOnly = text.contains('*')
        if (!saturdaysOnly) {
            times.add(
                Ferry(time, from, to, duration, FerryDay.SundayAndHolidays, fareSun, null)
            )
        }
    }

    suspend fun fetch(): List<Ferry> = withContext(Dispatchers.IO) {
        val document = Utils.retryJsoupGet(url)

        val ferryTimes = mutableListOf<Ferry>()
        document
            .select("table table.content_table1 > tbody:contains(From Discovery Bay From Mui Wo) > tr")
            .forEach {
                try {
                    val fromDBay = it.child(0).text()
                    parse(ferryTimes, fromDBay, FerryPier.DiscoveryBay, FerryPier.MuiWo)
                    val fromMuiWo = it.child(1).text()
                    parse(ferryTimes, fromMuiWo, FerryPier.MuiWo, FerryPier.DiscoveryBay)
                } catch (e: IllegalArgumentException) {
                    // Either not a proper time or no such child
                }
            }
        ferryTimes
    }

}