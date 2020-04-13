package com.lunesu.pengchauferry

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

@ExperimentalCoroutinesApi
class UtilsTest {
    @Test
    fun testIsEmulator() {
        assertTrue(Utils.isEmulator)
    }

    @Test
    fun testAtLeast() {
        Utils.atLeast(listOf(""), 1)
    }

    @Test(expected = RuntimeException::class)
    fun testAtLeastFail() {
        Utils.atLeast(listOf(""), 2)
    }

    @Test
    fun testRetry() = runBlockingTest {
        var i = 0
        Utils.retry(2, 100) { if (i++ == 0) throw RuntimeException() }
        assertEquals(2, i)
    }

    @Test(expected = RuntimeException::class)
    fun testRetryFail() = runBlockingTest {
        Utils.retry(2, 100) { throw RuntimeException() }
    }

    @Test
    fun testRetryJsoupGet() = runBlockingTest {
        Utils.retryJsoupGet("https://www.google.com/")
    }

    @Test(expected = IllegalArgumentException::class)
    fun testRetryJsoupGetFail() = runBlockingTest {
        Utils.retryJsoupGet("asf")
    }
}
