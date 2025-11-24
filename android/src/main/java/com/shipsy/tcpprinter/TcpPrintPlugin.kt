package com.shipsy.tcpprinter

import com.getcapacitor.JSObject
import com.getcapacitor.Plugin
import com.getcapacitor.PluginCall
import com.getcapacitor.annotation.CapacitorPlugin
import com.getcapacitor.annotation.PluginMethod
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.OutputStream
import java.net.InetSocketAddress
import java.net.Socket
import java.util.concurrent.atomic.AtomicBoolean

@CapacitorPlugin(name = "TcpPrint")
class TcpPrintPlugin : Plugin() {

    private var socket: Socket? = null
    private var outStream: OutputStream? = null
    private val isConnected = AtomicBoolean(false)
    private val ioScope = CoroutineScope(Dispatchers.IO)

    /**
     * connect({ ip: string, port: number, timeout?: number })
     */
    @PluginMethod
    fun connect(call: PluginCall) {
        val ip = call.getString("ip")
        val port = call.getInt("port") ?: 0
        val timeout = call.getInt("timeout") ?: 5000

        if (ip.isNullOrBlank() || port == 0) {
            call.reject("Missing or invalid 'ip' or 'port' parameter")
            return
        }

        ioScope.launch {
            try {
                val s = Socket()
                s.connect(InetSocketAddress(ip, port), timeout)
                outStream = s.getOutputStream()
                socket = s
                isConnected.set(true)
                val res = JSObject()
                res.put("connected", true)
                call.resolve(res)
            } catch (e: Exception) {
                call.reject("Failed to connect: ${e.message}", e)
            }
        }
    }

    /**
     * send({ data: string, isBase64?: boolean })
     * data can be a plain string or base64 if isBase64 true
     */
    @PluginMethod
    fun send(call: PluginCall) {
        val data = call.getString("data")
        val isBase64 = call.getBoolean("isBase64") ?: false

        if (!isConnected.get() || socket == null || outStream == null) {
            call.reject("Not connected")
            return
        }

        if (data == null) {
            call.reject("Missing 'data' parameter")
            return
        }

        ioScope.launch {
            try {
                val bytes = if (isBase64) {
                    android.util.Base64.decode(data, android.util.Base64.DEFAULT)
                } else {
                    data.toByteArray(Charsets.UTF_8)
                }
                outStream?.write(bytes)
                outStream?.flush()
                val res = JSObject()
                res.put("sent", bytes.size)
                call.resolve(res)
            } catch (e: Exception) {
                call.reject("Failed to send: ${e.message}", e)
            }
        }
    }

    /**
     * disconnect()
     */
    @PluginMethod
    fun disconnect(call: PluginCall) {
        ioScope.launch {
            try {
                outStream?.flush()
                outStream?.close()
                socket?.close()
            } catch (_: Exception) {
            } finally {
                outStream = null
                socket = null
                isConnected.set(false)
                val res = JSObject()
                res.put("connected", false)
                call.resolve(res)
            }
        }
    }

    override fun handleOnDestroy() {
        super.handleOnDestroy()
        try {
            outStream?.close()
            socket?.close()
        } catch (_: Exception) {
        }
    }
}
