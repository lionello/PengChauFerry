package io.enuma.nounly

import java.io.InputStream
import java.net.URL
import java.net.URLConnection
import java.net.URLStreamHandler
import java.net.URLStreamHandlerFactory
import java.security.cert.Certificate
import javax.net.ssl.HttpsURLConnection

object MockURLStreamHandlerFactory : URLStreamHandlerFactory {

    private class MockURLStreamHandler : URLStreamHandler() {

        private class MockConnection(url: URL?) : HttpsURLConnection(url) {
            override fun usingProxy(): Boolean = false

            override fun connect() {}

            override fun getServerCertificates(): Array<Certificate> {
                return emptyArray() // TODO
            }

            override fun disconnect() {}

            override fun getCipherSuite(): String = "mock"

            override fun getLocalCertificates(): Array<Certificate> {
                return emptyArray()
            }

//            override fun getInputStream(): InputStream {
//                return super.getInputStream()
//            }
        }

        override fun openConnection(u: URL?): URLConnection = MockConnection(u)
    }

    override fun createURLStreamHandler(protocol: String?): URLStreamHandler = MockURLStreamHandler()
}
