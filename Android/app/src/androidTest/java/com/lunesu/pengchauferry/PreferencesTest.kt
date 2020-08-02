package com.lunesu.pengchauferry

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.joda.time.LocalDateTime
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class PreferencesTest {
    private val appContext = InstrumentationRegistry.getInstrumentation().targetContext

    @Test
    fun useAppContext() {
        // Context of the app under test.
        assertEquals("com.lunesu.pengchauferry", appContext.packageName)
    }

    @Test
    fun testLanguage() {
        val lang = "en"
        Preferences(appContext).language = lang
        assertEquals(lang, Preferences(appContext).language)

        Preferences(appContext).language = null
        assertNull(Preferences(appContext).language)
    }

    @Test
    fun testLastRefresh() {
        val now = LocalDateTime(42)
        Preferences(appContext).lastRefresh = now
        assertEquals(now, Preferences(appContext).lastRefresh)

        Preferences(appContext).lastRefresh = null
        assertNull(Preferences(appContext).lastRefresh)
    }

    /*@Test
    fun testClear() {
        val db = DbOpenHelper(null)
        val vm = FerryViewModel(Application(), db)
        val viewModelStore = ViewModelStore()
        val viewModelProvider = ViewModelProvider(viewModelStore, object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel?> create(modelClass: Class<T>): T = vm as T
        })
        viewModelProvider.get(vm::class.java)

        //Run 2
        viewModelStore.clear()//To call clear() in ViewModel

        Assert.assertFalse(db.readableDatabase.isOpen)
        Assert.assertFalse(db.writableDatabase.isOpen)
    }*/
}
