package com.lunesu.pengchauferry

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.joda.time.LocalDate
import org.joda.time.format.DateTimeFormatterBuilder

object HongKongHolidayFetcher {
    const val YEAR = 2021
    private const val url = "https://www.gov.hk/en/about/abouthk/holiday/$YEAR.htm"

    private val formatter = DateTimeFormatterBuilder().appendPattern("dd MMMM").toFormatter()

    suspend fun fetch(): List<LocalDate> = withContext(Dispatchers.IO) {
        val document = Utils.retryJsoupGet(url)

        document
            .select("article section table tbody tr td ~ td")
            .mapNotNull {
                try {
                    // Replace &nbsp; with space
                    LocalDate.parse(it.text().replace('\u00a0', ' '), formatter).withYear(YEAR)
                } catch (e: IllegalArgumentException) { null }
            }
    }
}
