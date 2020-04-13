package com.lunesu.pengchauferry

import android.os.Build
import android.util.Log
import kotlinx.coroutines.delay
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

object Utils {
    private const val TAG = "Utils"

    val isEmulator = Build.FINGERPRINT?.contains("generic") != false

    @Throws(RuntimeException::class)
    fun <T> atLeast(list: List<T>, size: Int): List<T> {
        if (list.size < size) throw RuntimeException("Expected $size entries but got ${list.size}")
        return list
    }

    suspend fun <T> retry(max: Int, millis: Long, f: suspend () -> T): T {
        var retries = 1
        while (true) {
            try {
                return f()
            } catch (e: Exception) {
                Log.e(TAG, "retry $retries failed", e)
                if (++retries > max) throw e
            }
            delay(millis * retries)
        }
    }

    suspend fun retryJsoupGet(url: String): Document {
        return retry(2, 1000L) { Jsoup.connect(url).timeout(5000).get() }
    }
}
