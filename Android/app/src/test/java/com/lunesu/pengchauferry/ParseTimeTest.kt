package com.lunesu.pengchauferry

import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Parameterized::class)
class ParseTimeTest(private val row: String) {
    companion object {
        @JvmStatic
        @Parameterized.Parameters
        fun params(): List<String> = listOf(
            " 1.11 p.m.",
            " 11.11 p.m.*",
            "#1.11 p.m.",
            "#1.11 p.m.**",
            "#11.11 a.m.**",
            "#11.11 p.m.",
            "*+1.11 p.m.",
            "*1.11 a.m.",
            "*1.11 p.m.",
            "*11.11 a.m.",
            "*11.11 p.m.",
            "+1.11 p.m.",
            "1.11 a.m. ",
            "1.11 a.m. * ",
            "1.11 a.m. *",
            "1.11 a.m. - 1.11 p.m.",
            "1.11 a.m. - 11.11 p.m",
            "1.11 a.m. - 11.11 p.m.",
            "1.11 a.m.",
            "1.11 a.m.#",
            "1.11 a.m.#*",
            "1.11 a.m.*",
            "1.11 a.m.**",
            "1.11 a.m.@",
            "1.11 a.m.@*",
            "1.11 a.m.^",
            "1.11 am",
            "1.11 p.m",
            "1.11 p.m.  ",
            "1.11 p.m. ",
            "1.11 p.m. *",
            "1.11 p.m. - 1.11 p.m.",
            "1.11 p.m.",
            "1.11 p.m.# ",
            "1.11 p.m.#",
            "1.11 p.m.*",
            "1.11 p.m.*#",
            "1.11 p.m.**",
            "1.11 p.m.*@",
            "1.11 p.m.@*",
            "1.11 pm",
            "11.11 a.m. #",
            "11.11 a.m. *",
            "11.11 a.m.",
            "11.11 a.m.#",
            "11.11 a.m.*",
            "11.11 am",
            "11.11 p.m. ",
            "11.11 p.m. *",
            "11.11 p.m.",
            "11.11 p.m.#",
            "11.11 p.m.*",
            "11.11 pm",
            "@1.11 p.m.**",
            "@11.11 a.m.**",
        )
    }

    @Test
    fun testParseTime() {
        Utils.parseTime(row)
    }
}