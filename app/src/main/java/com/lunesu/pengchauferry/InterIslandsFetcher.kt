package com.lunesu.pengchauferry

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.joda.time.Duration
import org.joda.time.LocalTime
import org.jsoup.Jsoup

object InterIslandsFetcher : Fetcher<Ferry> {
    private const val url = "http://www.nwff.com.hk/route/get_route.php?id=2e2c0154-902a-4c11-9405-f7743f6e6d2e&route_id=0&submenu_num=3"

    private const val fare = "13.4"
    private val durPengChauMuiWo: Duration = Duration.standardMinutes(20)
//        val durMuiWoChiMaWan = Duration.standardMinutes(15) TODO: support other piers too
//        val durChiMaWanCheungChau = Duration.standardMinutes(20)
//        val durMuiWoCheungChau = Duration.standardMinutes(35)

    override suspend fun fetch(): List<Ferry> = withContext(Dispatchers.IO) {
        val document = Utils.retryJsoupGet(url)

        document
            .select("table.calresult > tbody > tr")
            .map { tr ->
                val th = tr.parent().firstElementSibling().child(0).child(0)
                if (th.text() == "Peng Chau") {
                    val from = FerryPier.PengChau
                    val via = FerryPier.MuiWo
                    val to = FerryPier.CheungChau
                    val td = tr.child(0)
                    Ferry(LocalTime.parse(td.text()), from, to, durPengChauMuiWo, FerryDay.EVERYDAY, fare, via)
                } else {
                    val from = FerryPier.MuiWo
                    val to = FerryPier.PengChau
                    val td = tr.child(2)
                    Ferry(LocalTime.parse(td.text()), from, to, durPengChauMuiWo, FerryDay.EVERYDAY, fare, null)
                }
            }
    }

}