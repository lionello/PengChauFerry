package com.lunesu.pengchauferry

import org.junit.Test

import org.junit.Assert.*

class FerryDayTest {
    @Test
    fun testEveryday() {
        assertEquals(8, FerryDay.intToDays(255).size)
    }

    @Test
    fun testToday() {
        val weekdays = FerryDay.intToDays(127)
        assertEquals(true, weekdays.contains(FerryDay.today()))
    }

    @Test
    fun testIntAndBack() {
        for (i in 1..255) {
            assertEquals(i, FerryDay.daysToInt(FerryDay.intToDays(i)))
        }
    }
}
