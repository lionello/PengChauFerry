package com.lunesu.pengchauferry

import org.joda.time.Duration
import org.joda.time.LocalTime


data class Ferry(val time: LocalTime, val from: FerryPier, val to: FerryPier, val dur: Duration, val days: FerryDays) {
    val endTime : LocalTime get() = time.plus(dur.toPeriod())
}
