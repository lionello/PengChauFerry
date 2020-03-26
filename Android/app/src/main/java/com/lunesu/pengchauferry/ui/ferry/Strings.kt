package com.lunesu.pengchauferry.ui.ferry

import com.lunesu.pengchauferry.FerryDay
import com.lunesu.pengchauferry.FerryPier
import com.lunesu.pengchauferry.R

object Strings {
    val PIERS = mapOf(
        FerryPier.Central to R.string.Central,
        FerryPier.PengChau to R.string.PengChau,
        FerryPier.TrappistMonastery to R.string.TrappistMonastery,
        FerryPier.DiscoveryBay to R.string.DiscoveryBay,
        FerryPier.MuiWo to R.string.MuiWo,
        FerryPier.CheungChau to R.string.CheungChau,
        FerryPier.ChiMaWan to R.string.ChiMaWan,
        FerryPier.HeiLingChau to R.string.HeiLingChau
    )

    val DAYS = mapOf(
        FerryDay.Monday to R.string.monday,
        FerryDay.Tuesday to R.string.tuesday,
        FerryDay.Wednesday to R.string.wednesday,
        FerryDay.Thursday to R.string.thursday,
        FerryDay.Friday to R.string.friday,
        FerryDay.Saturday to R.string.saturday,
        FerryDay.Sunday to R.string.sunday,
        FerryDay.Holiday to R.string.holiday
    )

}
