package com.lunesu.pengchauferry

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.joda.time.Duration
import org.joda.time.LocalTime
import org.joda.time.format.DateTimeFormatterBuilder
import org.jsoup.Jsoup
import java.util.*

object KaitoFetcher : Fetcher<Ferry> {
    private const val url = "https://www.td.gov.hk/en/transport_in_hong_kong/public_transport/ferries/kaito_services_map/service_details/index.html"

    private val duration = Duration.standardMinutes(10)
    private val saturday = EnumSet.of(FerryDay.Saturday)
    private val formatter = DateTimeFormatterBuilder().appendPattern("h.mm a").toFormatter()

    override suspend fun fetch(): List<Ferry> = withContext(Dispatchers.IO) {
        val document = Jsoup.connect(url).get()

        val ferryTimes = mutableListOf<Ferry>()
        document
            .select("table.content_table1:contains(Peng Chau) > tbody > tr > td > p:contains(From)")
            .forEachIndexed { i, p ->
                val fromText = p.child(0).text().trim()
                val from = when (fromText) {
                    "From Peng Chau" -> FerryPier.PengChau
                    "From Trappist Monastery" -> FerryPier.TrappistMonastery
                    "From Discovery Bay" -> FerryPier.DiscoveryBay
                    else -> throw Error(fromText)
                }

                // Trips to/from Trappist Monastery are derived from the '*' so we know the direction of travel
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
                        val time =
                            LocalTime.parse(text.trim('*', '+', '.').replace(".m", "m"), formatter)
                        ferryTimes.add(
                            Ferry(
                                time,
                                from,
                                if (viaTrappistMonastery) FerryPier.TrappistMonastery else to,
                                duration,
                                if (saturdaysOnly) saturday else days
                            )
                        )
                    }
                }
            }
        ferryTimes
    }

}