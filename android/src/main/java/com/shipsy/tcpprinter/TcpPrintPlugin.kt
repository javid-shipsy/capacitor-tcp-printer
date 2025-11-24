package com.shipsy.tcpprinter

import com.getcapacitor.Plugin
import com.getcapacitor.annotation.CapacitorPlugin
import com.getcapacitor.PluginCall
import kotlinx.coroutines.*

@CapacitorPlugin(name = "TcpPrint")
class TcpPrintPlugin : Plugin() {

    private val tcpPrint = TcpPrint()

    fun send(call: PluginCall) {
        val ip = call.getString("ip")
        val port = call.getInt("port")
        val data = call.getString("data")

        if (ip == null || port == null || data == null) {
            call.reject("Missing parameters")
            return
        }

        // run in background
        CoroutineScope(Dispatchers.IO).launch {
            val success = tcpPrint.send(ip, port, data)
            if (success) {
                call.resolve()
            } else {
                call.reject("TCP print failed")
            }
        }
    }
}
