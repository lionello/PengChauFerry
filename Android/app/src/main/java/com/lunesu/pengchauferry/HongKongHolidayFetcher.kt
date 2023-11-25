package com.lunesu.pengchauferry

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.joda.time.LocalDate
import org.joda.time.format.DateTimeFormatterBuilder
import org.jsoup.nodes.Document

object HongKongHolidayFetcher {
    private val formatter = DateTimeFormatterBuilder().appendPattern("dd MMMM").toFormatter()

    suspend fun fetch(): List<LocalDate> = withContext(Dispatchers.IO) {
        val document = Utils.retryJsoupGet(Constants.holidayUrl)
        parse(document)
    }

    fun parse(document: Document): List<LocalDate> {
        return document
            .select("article section table tbody tr td ~ td")
            .mapNotNull {
                try {
                    // Replace &nbsp; with space
                    LocalDate.parse(it.text().replace('\u00a0', ' '), formatter).withYear(Constants.YEAR)
                } catch (e: IllegalArgumentException) { null }
            }
    }
}
