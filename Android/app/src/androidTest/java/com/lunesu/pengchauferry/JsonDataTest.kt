package com.lunesu.pengchauferry

import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertNotNull
import org.junit.Test

class JsonDataTest {
    private val appContext = InstrumentationRegistry.getInstrumentation().targetContext

    @Test
    fun testLoad() {
        assertNotNull(JsonData.load(appContext))
    }
}
