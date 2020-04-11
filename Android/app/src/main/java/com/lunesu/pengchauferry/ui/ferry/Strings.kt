package com.lunesu.pengchauferry.ui.ferry

import android.content.res.Resources
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

    val PIERS_DUAL = mapOf(
        FerryPier.Central to R.string.Central2,
        FerryPier.PengChau to R.string.PengChau2,
        FerryPier.TrappistMonastery to R.string.TrappistMonastery2,
        FerryPier.DiscoveryBay to R.string.DiscoveryBay2,
        FerryPier.MuiWo to R.string.MuiWo2,
        FerryPier.CheungChau to R.string.CheungChau2,
        FerryPier.ChiMaWan to R.string.ChiMaWan2,
        FerryPier.HeiLingChau to R.string.HeiLingChau2
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

    fun localized(pier: FerryPier, resources: Resources) : String =
        resources.getString(PIERS.getValue(pier))

    fun localized(day: FerryDay, resources: Resources) : String =
        resources.getString(DAYS.getValue(day))
}
