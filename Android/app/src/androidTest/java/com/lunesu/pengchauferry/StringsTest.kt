package com.lunesu.pengchauferry

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.lunesu.pengchauferry.ui.ferry.Strings
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class StringsTest {
    private val appContext = InstrumentationRegistry.getInstrumentation().targetContext

    @Test
    fun testPiers() {
        val nonAscii = Regex("[^a-zA-Z]")
        for (pier in FerryPier.ENUMS) {
            assertEquals(pier.toString(), appContext.getString(Strings.PIERS.getValue(pier)).replace(nonAscii, ""))
        }
    }

    @Test
    fun testDays() {
        for (day in FerryDay.ENUMS) {
            assertEquals(day.toString(), appContext.getString(Strings.DAYS.getValue(day)))
        }
    }
}
