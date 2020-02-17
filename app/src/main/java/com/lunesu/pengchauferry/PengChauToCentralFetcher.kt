package com.lunesu.pengchauferry

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.joda.time.Duration
import org.joda.time.LocalTime
import org.jsoup.Jsoup

object PengChauToCentralFetcher : Fetcher<Ferry> {
    private const val url = "http://hkkf.com.hk/index.php?op=timetable&page=pengchau&style=en"

    private val durationFast = Duration.standardMinutes(27)
    private val durationSlow = Duration.standardMinutes(40)

    override suspend fun fetch(): List<Ferry> = withContext(Dispatchers.IO) {
        val document = Jsoup.connect(url).get()

        val ferryTimes = mutableListOf<Ferry>()
        document
            .select("div table tr td table tr td div table tr td table tbody tr")
            .forEachIndexed { i, tr ->
                val modifiers = mutableListOf<String>()
                val div = tr.child(0).child(0)
                // Handle initial <br> without whitespace
                if ("br" == div.childNode(0).nodeName()) {
                    modifiers.add("")
                }
                modifiers.addAll(div.textNodes().map { text -> text.text() })

                val times = tr.child(1).textNodes().map { text -> text.text().trim() }

                val from = if ((i and 2) == 0) FerryPier.Central else FerryPier.PengChau
                val to = if (from == FerryPier.Central) FerryPier.PengChau else FerryPier.Central

                // FIXME: when the time is after MIDNIGHT (00:30) we need to fix-up the days as well
                val days = if ((i and 4) == 0) FerryDay.MondayToSaturday else FerryDay.SundayAndHolidays

                modifiers.zip(times).forEach { pair ->
                    ferryTimes.add(
                        Ferry(
                            LocalTime.parse(pair.second),
                            from,
                            to,
                            if (pair.first.contains("*")) durationSlow else durationFast,
                            days
                        )
                    )
                }
            }//.flatten()
        ferryTimes
    }

}