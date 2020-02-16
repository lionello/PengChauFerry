package com.lunesu.pengchauferry

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.joda.time.LocalDate
import org.joda.time.format.DateTimeFormatterBuilder
import org.jsoup.Jsoup

object HongKongHolidaysFetcher: Fetcher<LocalDate> {
    private const val url = "https://www.gov.hk/en/about/abouthk/holiday/2020.htm"

    private val formatter = DateTimeFormatterBuilder().appendPattern("dd MMMM").toFormatter()

    override suspend fun fetch(): List<LocalDate> = withContext(Dispatchers.IO) {
        val document = Jsoup.connect(url).get()

        document
            .select("article section table tbody tr td.date")
            .mapNotNull {
                try {
                    // Replace &nbsp; with space
                    LocalDate.parse(it.text().replace('\u00a0', ' '), formatter).withYear(2020)
                } catch (e: IllegalArgumentException) { null }
            }
    }
}