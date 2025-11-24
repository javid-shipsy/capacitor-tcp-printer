package com.stockone.plugins.tcpprinter

import android.util.Log
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

        Log.e("TcpPrint", "Received request: IP=$ip Port=$port DataLength=${data?.length}")

        if (ip == null || port == null || data == null) {
            Log.e("TcpPrint", "Missing parameter")
            call.reject("Missing parameters")
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                Log.e("TcpPrint", "Attempting connection to $ip:$port")

                val success = tcpPrint.send(ip, port, data)

                if (success) {
                    Log.e("TcpPrint", "Print sent successfully!")
                    call.resolve()
                } else {
                    Log.e("TcpPrint", "Failed to send print data")
                    call.reject("send_failed")
                }
            } catch (e: Exception) {
                Log.e("TcpPrint", "Exception: ${e.message}")
                call.reject("exception: ${e.message}")
            }
        }
    }
}
