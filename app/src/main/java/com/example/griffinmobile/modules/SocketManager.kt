package com.example.griffinmobile.modules

import java.net.DatagramSocket

object SocketManager {
    private var socket: DatagramSocket? = null
    @Volatile
    private var isClosed = false

    fun getOrCreateSocket(port: Int): DatagramSocket {
        if (socket == null || socket!!.isClosed) {
            println("socket created")
            socket = DatagramSocket(port)
            isClosed = false
        }
        return socket!!
    }

    fun closeSocket() {
        socket?.let {
            it.close()
            socket = null
            isClosed = true
            println("Socket closed")
        }
    }

    fun isClosed(): Boolean {
        return isClosed
    }
}