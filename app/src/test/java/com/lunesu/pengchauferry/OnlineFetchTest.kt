package com.lunesu.pengchauferry

import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertNotEquals
import org.junit.BeforeClass
import org.junit.Test
import java.security.SecureRandom
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext

class OnlineFetchTest {
    companion object {
        @BeforeClass
        @JvmStatic
        fun setup() {
            // SSL verification fails: ignore the certs.
            val sc = SSLContext.getInstance("SSL")
            sc.init(null, arrayOf(TrustAllCertificates()), SecureRandom())
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.socketFactory)
        }
    }

    @Test
    fun testPengChauToCentral() {
        runBlocking {
            assertNotEquals(0, PengChauToCentralFetcher.fetch().size)
        }
    }

    @Test
    fun testKaito() {
        runBlocking {
            assertNotEquals(0, KaitoFetcher.fetch().size)
        }
    }

    @Test
    fun testInterIslands() {
        runBlocking {
            assertNotEquals(0, InterIslandsFetcher.fetch().size)
        }
    }

    @Test
    fun testHongKongHolidays() {
        runBlocking {
            assertNotEquals(0, HongKongHolidaysFetcher.fetch().size)
        }
    }
}
