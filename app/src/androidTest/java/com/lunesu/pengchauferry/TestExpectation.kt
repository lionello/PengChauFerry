package io.enuma.nounly

import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class TestExpectation(val expectationDescription: String) {
    private val latch = CountDownLatch(1)

    fun fulfill() {
        latch.countDown()
    }

    fun wait(timeout: Double) {
        latch.await((timeout * 1000).toLong(), TimeUnit.MILLISECONDS)
    }

//    companion object {
//        fun wait(for: Array<TestExpectation>, timeout: Double) {
//            latch.await((timeout * 1000).toLong(), TimeUnit.MILLISECONDS)
//        }
//    }
}
