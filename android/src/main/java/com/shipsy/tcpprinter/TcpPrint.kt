package com.shipsy.tcpprinter

import java.io.OutputStream
import java.net.InetSocketAddress
import java.net.Socket

class TcpPrint {

    fun send(ip: String, port: Int, data: String): Boolean {
        return try {
            val socket = Socket()
            socket.connect(InetSocketAddress(ip, port), 3000)
            val out: OutputStream = socket.getOutputStream()
            out.write(data.toByteArray())
            out.flush()
            out.close()
            socket.close()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
