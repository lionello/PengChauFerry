package com.lunesu.pengchauferry

import org.junit.Assert.assertEquals
import org.junit.Test

class FerryPierTest {
    @Test
    fun testName() {
        assertEquals("PengChau", FerryPier.PengChau.name)
    }

    @Test
    fun testToString() {
        assertEquals("PengChau", FerryPier.PengChau.toString())
    }

    @Test
    fun testValueOf() {
        assertEquals(FerryPier.PengChau, FerryPier.valueOf("PengChau"))
    }

    @Test
    fun testNearest() {
        assertEquals(FerryPier.PengChau, FerryPier.findNearest(22.279, 114.046))
        assertEquals(FerryPier.Central, FerryPier.findNearest(22.286, 114.161))
    }
}