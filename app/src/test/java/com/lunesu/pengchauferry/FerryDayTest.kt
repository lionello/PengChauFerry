package com.lunesu.pengchauferry

import org.junit.Test

import org.junit.Assert.*
import java.util.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class FerryDayTest {
    @Test
    fun everyday() {
        assertEquals(8, FerryDay.intToDays(255))
    }

    @Test
    fun today() {
        val weekdays = FerryDay.intToDays(127)
        assertEquals(true, weekdays.contains(FerryDay.today()))
    }

    @Test
    fun intAndBack() {
        for (i in 1..255) {
            assertEquals(i, FerryDay.daysToInt(FerryDay.intToDays(i)))
        }
    }
}
