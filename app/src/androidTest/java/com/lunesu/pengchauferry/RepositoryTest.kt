package com.lunesu.pengchauferry

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RepositoryTest {
    private val appContext = InstrumentationRegistry.getInstrumentation().targetContext

    @Test
    fun testRefresh() {
        val repo = FerryRepository(DbOpenHelper(appContext))
        runBlocking {
            repo.refresh()
        }
    }
}