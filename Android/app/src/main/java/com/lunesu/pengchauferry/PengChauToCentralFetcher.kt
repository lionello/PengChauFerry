package com.lunesu.pengchauferry

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.joda.time.Duration
import org.joda.time.LocalTime
import org.joda.time.format.DateTimeFormatterBuilder
import org.jsoup.nodes.Document

object PengChauToCentralFetcher {
    // TODO: fetch prices from web page as well
    private const val fareSlowWD = "19.8"
    private const val fareSlowPH = "28.4"
    private const val fareFastWD = "36.9"
    private const val fareFastPH = "54.3"
    private val durationFast = Duration.standardMinutes(27)
    private val durationSlow = Duration.standardMinutes(40)

    suspend fun fetch(): List<Ferry> = withContext(Dispatchers.IO) {
        val document = Utils.retryJsoupGet(Constants.ferryServiceUrl)
        parse(document)
    }

    fun parse(document: Document): List<Ferry> {
        val ferryTimes = mutableListOf<Ferry>()
        document
            .select("table table > tbody:contains(From Central From Peng Chau) > tr")
            .forEachIndexed { i, tr ->
                val pairs = tr.select("td").mapNotNull { tn -> Utils.parseTime(tn.text()) }

                val from = if ((i and 2) == 0) FerryPier.Central else FerryPier.PengChau
                val to = if (from == FerryPier.Central) FerryPier.PengChau else FerryPier.Central

                // FIXME: when the time is after MIDNIGHT (00:30) we need to fix-up the days as well
                val monToSat = tr.parent().child(0).text().contains("Mondays to Saturdays")
                val days = if (monToSat) FerryDay.MondayToSaturday else FerryDay.SundayAndHolidays
                val fareSlow = if (monToSat) fareSlowWD else fareSlowPH
                val fareFast = if (monToSat) fareFastWD else fareFastPH

                pairs.forEach { pair ->
                    val slow = pair.second.contains("*")
                    val hlc = false//pair.first.contains("#")
                    ferryTimes.add(
                        Ferry(
                            pair.first,
                            from,
                            if (hlc) FerryPier.HeiLingChau else to,
                            if (slow) durationSlow else durationFast,
                            days,
                            if (slow) fareSlow else fareFast,
                            if (hlc) to else null
                        )
                    )
                }
            } // .flatten()
        return ferryTimes
    }
}
