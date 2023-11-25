package com.lunesu.pengchauferry

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.joda.time.Duration
import org.joda.time.LocalTime
import org.jsoup.nodes.Document

object InterIslandsFetcher {
    // TODO: fetch prices from web page as well
    private const val fareWD = "14.5"
    private const val farePH = "14.5" // TODO
    private val durPengChauMuiWo: Duration = Duration.standardMinutes(20)
//        val durMuiWoChiMaWan = Duration.standardMinutes(15) TODO: support other piers too
//        val durChiMaWanCheungChau = Duration.standardMinutes(20)
//        val durMuiWoCheungChau = Duration.standardMinutes(35)

    suspend fun fetch(): List<Ferry> = withContext(Dispatchers.IO) {
        val document = Utils.retryJsoupGet(Constants.ferryServiceUrl)
        parse(document)
    }

    fun parse(document: Document): List<Ferry> {
        //#content > div > div > table:nth-child(208) > tbody > tr:nth-child(1)
        return document
            .select("table > tbody:contains(From Peng Chau From Mui Wo From Chi Ma Wan) > tr")
            .mapNotNull { tr ->
                try {
                    when (tr.child(1).text()) {
                        "" -> null // skip
                        "---->" -> {
                            val pcTime = tr.child(0).text().replace(".m.", "m")
                            Ferry(
                                Utils.parseTime(pcTime)!!.first,
                                FerryPier.PengChau,
                                FerryPier.CheungChau,
                                durPengChauMuiWo,
                                FerryDay.EVERYDAY,
                                fareWD,
                                FerryPier.MuiWo
                            )
                        }
                        "<----" -> {
                            val mwTime = tr.child(2).text().replace(".m.", "m")
                            Ferry(
                                Utils.parseTime(mwTime)!!.first,
                                FerryPier.MuiWo, // TODO: could be from CheungChau,
                                FerryPier.PengChau,
                                durPengChauMuiWo,
                                FerryDay.EVERYDAY,
                                fareWD,
                                null
                            )
                        }
                        else -> null
                    }
                }
                catch (e: IllegalArgumentException) {
                    null // TODO: detect "note3"
                }
                catch (e: IndexOutOfBoundsException) {
                    null
                }
            }
    }
}
