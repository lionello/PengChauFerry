package com.lunesu.pengchauferry

import org.junit.Assert.assertEquals
import org.junit.Test

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

    @Test
    fun testToString() {
        assertEquals("Monday", FerryDay.Monday.toString())
        assertEquals("Tuesday", FerryDay.Tuesday.toString())
        assertEquals("Wednesday", FerryDay.Wednesday.toString())
        assertEquals("Thursday", FerryDay.Thursday.toString())
        assertEquals("Friday", FerryDay.Friday.toString())
        assertEquals("Saturday", FerryDay.Saturday.toString())
        assertEquals("Sunday", FerryDay.Sunday.toString())
        assertEquals("Holiday", FerryDay.Holiday.toString())
    }
}
