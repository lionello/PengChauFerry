package com.lunesu.pengchauferry

import java.util.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.joda.time.Duration
import org.jsoup.nodes.Document

object KaitoFetcher {
    // TODO: fetch prices from web page as well (although currently it has the wrong price)
    private const val fare = "7.5"
    private const val fareSpecialDeparture = "38.0"
    private val durationSlow = Duration.standardMinutes(20)
    private val durationFast = Duration.standardMinutes(10)
    private val saturday = EnumSet.of(FerryDay.Saturday)

    suspend fun fetch(): List<Ferry> = withContext(Dispatchers.IO) {
        val document = Utils.retryJsoupGet(Constants.ferryServiceUrl)
        parse(document)
    }

    fun parse(document: Document): List<Ferry> {
        // Price xpath: *[@id="content"]/table[31]/tbody/tr[2]/td[2]/p/text()[1]

        val ferryTimes = mutableListOf<Ferry>()
        document
            .select("table.content_table1:contains(Peng Chau) > tbody > tr > td > p:contains(From)")
            .forEachIndexed { i, p ->
                val from = when (val fromText = p.child(0).text().trim()) {
                    "From Peng Chau" -> FerryPier.PengChau
                    "From Trappist Monastery" -> FerryPier.TrappistMonastery
                    "From Discovery Bay" -> FerryPier.DiscoveryBay
                    else -> throw Error(fromText)
                }

                // Trips to/from Trappist Monastery are derived from the '*' so we know the direction of travel
                // TODO: support TrappistMonastery origin
                if (from != FerryPier.TrappistMonastery) {
                    val to = when (from) {
                        FerryPier.DiscoveryBay -> FerryPier.PengChau
                        FerryPier.PengChau -> FerryPier.DiscoveryBay
                        else -> throw AssertionError(from)
                    }
                    val days = if (i <= 2) FerryDay.MondayToSaturday else FerryDay.SundayAndHolidays
                    p.textNodes().forEach {
                        // *   Via Trappist Monastery
                        // +   Saturdays only
                        val text = it.text()
                        val viaTrappistMonastery = text.contains('*')
                        val saturdaysOnly = text.contains('+')
                        val specialDeparture = text.contains('#')
                        val time = Utils.parseTime(text)!!.first
                        ferryTimes.add(
                            Ferry(
                                time,
                                from,
                                to,
                                if (viaTrappistMonastery) durationSlow else durationFast,
                                if (saturdaysOnly) saturday else days,
                                if (specialDeparture) fareSpecialDeparture else fare,
                                if (viaTrappistMonastery) FerryPier.TrappistMonastery else null
                            )
                        )
                    }
                }
            }
        return ferryTimes
    }
}
