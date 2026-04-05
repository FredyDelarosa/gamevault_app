package com.fredy.gamevault.core.network

import android.util.Log
import okhttp3.Call
import okhttp3.EventListener
import okhttp3.HttpUrl
import okhttp3.Protocol
import java.io.IOException
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.Proxy
import java.util.concurrent.atomic.AtomicLong

class NetworkEventLogger : EventListener() {

    private var requestId: Long = 0L

    override fun callStart(call: Call) {
        requestId = ID_GENERATOR.incrementAndGet()
        val url: HttpUrl = call.request().url
        Log.d(TAG, "[$requestId] callStart ${call.request().method} $url")
    }

    override fun dnsStart(call: Call, domainName: String) {
        Log.d(TAG, "[$requestId] dnsStart $domainName")
    }

    override fun dnsEnd(call: Call, domainName: String, inetAddressList: List<InetAddress>) {
        Log.d(TAG, "[$requestId] dnsEnd $domainName -> $inetAddressList")
    }

    override fun connectStart(call: Call, inetSocketAddress: InetSocketAddress, proxy: Proxy) {
        Log.d(TAG, "[$requestId] connectStart $inetSocketAddress via $proxy")
    }

    override fun secureConnectStart(call: Call) {
        Log.d(TAG, "[$requestId] secureConnectStart")
    }

    override fun secureConnectEnd(call: Call, handshake: okhttp3.Handshake?) {
        Log.d(TAG, "[$requestId] secureConnectEnd protocol=${handshake?.tlsVersion}")
    }

    override fun connectEnd(
        call: Call,
        inetSocketAddress: InetSocketAddress,
        proxy: Proxy,
        protocol: Protocol?
    ) {
        Log.d(TAG, "[$requestId] connectEnd $inetSocketAddress protocol=$protocol")
    }

    override fun connectFailed(
        call: Call,
        inetSocketAddress: InetSocketAddress,
        proxy: Proxy,
        protocol: Protocol?,
        ioe: IOException
    ) {
        Log.e(TAG, "[$requestId] connectFailed $inetSocketAddress: ${ioe.javaClass.simpleName} ${ioe.message}")
    }

    override fun responseHeadersStart(call: Call) {
        Log.d(TAG, "[$requestId] responseHeadersStart")
    }

    override fun callEnd(call: Call) {
        Log.d(TAG, "[$requestId] callEnd")
    }

    override fun callFailed(call: Call, ioe: IOException) {
        Log.e(TAG, "[$requestId] callFailed: ${ioe.javaClass.simpleName} ${ioe.message}")
    }

    companion object {
        private const val TAG = "NetworkEventLogger"
        private val ID_GENERATOR = AtomicLong(0)
    }
}

