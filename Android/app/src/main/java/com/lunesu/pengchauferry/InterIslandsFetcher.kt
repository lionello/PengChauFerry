package com.lunesu.pengchauferry

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.joda.time.Duration
import org.joda.time.LocalTime

object InterIslandsFetcher {
    private const val url = "http://www.nwff.com.hk/route/get_route.php?id=2e2c0154-902a-4c11-9405-f7743f6e6d2e&route_id=0&submenu_num=3"

    // TODO: fetch prices from web page as well
    private const val fareWD = "14.2"
    private const val farePH = "21.2" // TODO
    private val durPengChauMuiWo: Duration = Duration.standardMinutes(20)
//        val durMuiWoChiMaWan = Duration.standardMinutes(15) TODO: support other piers too
//        val durChiMaWanCheungChau = Duration.standardMinutes(20)
//        val durMuiWoCheungChau = Duration.standardMinutes(35)

    suspend fun fetch(): List<Ferry> = withContext(Dispatchers.IO) {
        val document = Utils.retryJsoupGet(url)

        document
            .select("table.calresult > tbody > tr")
            .mapNotNull { tr ->
                val th = tr.parent().firstElementSibling().child(0).child(0)
                if (th.text() == "Peng Chau") {
                    val from = FerryPier.PengChau
                    val via = FerryPier.MuiWo
                    val to = FerryPier.CheungChau
                    val td = tr.child(0)
                    try {
                        Ferry(LocalTime.parse(td.text()), from, to, durPengChauMuiWo, FerryDay.EVERYDAY, fareWD, via)
                    }
                    catch (e: IllegalArgumentException) {
                        null
                    }
                } else {
                    val from = FerryPier.MuiWo
                    val to = FerryPier.PengChau
                    val td = tr.child(2)
                    Ferry(LocalTime.parse(td.text()), from, to, durPengChauMuiWo, FerryDay.EVERYDAY, fareWD, null)
                }
            }
    }
}
