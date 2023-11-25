package com.lunesu.pengchauferry

import android.content.Context
import android.os.Build
import android.util.Log
import kotlinx.coroutines.delay
import org.joda.time.LocalTime
import org.joda.time.format.DateTimeFormatterBuilder
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

    fun jsoupLoad(context: Context, asset: String): Document {
        val inputStream = context.assets.open(asset)
        return Jsoup.parse(inputStream, "utf-8", "https://example.com")
    }

    suspend fun retryJsoupGet(url: String): Document {
        return retry(2, 1000L) {
            Jsoup.connect(url).timeout(5000).get()
        }
    }

    private val formatter = DateTimeFormatterBuilder().appendPattern("h.mm a").toFormatter()
    private val timeRegex = Regex("""^\s*([^0-9]*)\s*(\d{1,2}\.\d{2} [ap]\.?m)\.?\s*(.*)\s*$""")

    fun parseTime(str: String): Pair<LocalTime,String>? =
        timeRegex.find(str)?.run {
            LocalTime.parse(
                groupValues[2].replace(".m", "m"),
                formatter
            ) to groupValues[1] + groupValues[3]
        }
}

