package com.lunesu.pengchauferry

import java.lang.IllegalArgumentException
import java.util.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.joda.time.Duration
import org.joda.time.LocalTime
import org.jsoup.nodes.Document

object KaitoMuiWoFetcher {
    // TODO: fetch prices from web page as well
    private const val fareSat = "12.0"
    private const val fareSun = "15.0"
    private val duration = Duration.standardMinutes(25)
    private val saturday = EnumSet.of(FerryDay.Saturday)
    private val schoolDays = EnumSet.of(FerryDay.Monday, FerryDay.Tuesday, FerryDay.Wednesday, FerryDay.Thursday, FerryDay.Friday)

    private fun parse(times: MutableList<Ferry>, text: String, from: FerryPier, to: FerryPier) {
        Utils.parseTime(text)?.let {
            val sundaysOnly = text.contains('#')// # Operate on Sunday and public holiday only
            if (!sundaysOnly) {
                times.add(
                    Ferry(it.first, from, to, duration, saturday, fareSat, null)
                )
            }
            val saturdaysOnly = text.contains('*')// * Operate on Saturday (except public holiday) only
            if (!saturdaysOnly) {
                times.add(
                    Ferry(it.first, from, to, duration, FerryDay.SundayAndHolidays, fareSun, null)
                )
            }
        }
    }

    suspend fun fetch(): List<Ferry> = withContext(Dispatchers.IO) {
        val document = Utils.retryJsoupGet(Constants.ferryServiceUrl)
        parse(document)
    }

    fun parse(document: Document): List<Ferry> {
        val ferryTimes = mutableListOf<Ferry>()
        // TODO: add schedule for Mondays to Fridays (School Days only) via Peng Chau
        // ferryTimes.add(Ferry(LocalTime.parse("7.20am"), FerryPier.MuiWo, FerryPier.DiscoveryBay, Duration.standardMinutes(30), schoolDays, fareSat, FerryPier.PengChau))
        // ferryTimes.add(Ferry(LocalTime.parse("7.45am"), FerryPier.PengChau, FerryPier.DiscoveryBay, Duration.standardMinutes(10), schoolDays, fareSat))
        // ferryTimes.add(Ferry(LocalTime.parse("3.10pm"), FerryPier.DiscoveryBay, FerryPier.MuiWo, Duration.standardMinutes(30), schoolDays, fareSat, FerryPier.PengChau))
        // ferryTimes.add(Ferry(LocalTime.parse("3.20pm"), FerryPier.DiscoveryBay, FerryPier.PengChau, Duration.standardMinutes(10), schoolDays, fareSat))
        document
            .select("table table.content_table1 > tbody:contains(From Discovery Bay From Mui Wo) > tr")
            .forEach {
                try {
                    val fromDBay = it.child(0).text()
                    parse(ferryTimes, fromDBay, FerryPier.DiscoveryBay, FerryPier.MuiWo)
                    val fromMuiWo = it.child(1).text()
                    parse(ferryTimes, fromMuiWo, FerryPier.MuiWo, FerryPier.DiscoveryBay)
                } catch (e: IndexOutOfBoundsException) {
                    // No such child
                }
            }
        return ferryTimes
    }
}
