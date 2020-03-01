package com.lunesu.pengchauferry

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.lunesu.pengchauferry.ui.ferry.FerryViewModel
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    private val appContext = InstrumentationRegistry.getInstrumentation().targetContext

    @Test
    fun useAppContext() {
        // Context of the app under test.
        assertEquals("com.lunesu.pengchauferry", appContext.packageName)
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
