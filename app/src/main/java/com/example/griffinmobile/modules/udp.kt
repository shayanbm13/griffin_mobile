package com.example.griffinmobile.mudels

import android.annotation.SuppressLint


import android.content.Context
import android.net.wifi.WifiManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import com.example.griffinmobile.database.*
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.net.*



class UdpViewModel : ViewModel() {
    private val _received_udp = MutableLiveData<String?>()

    val received_udp: LiveData<String?>
    get() = _received_udp

//    fun getUdpMessage(): LiveData<String> {
//        return udpMessage
//    }

    fun setUdpMessagePost(newText: String?) {
    _received_udp.postValue(newText)
        }
//    fun setUdpMessagePost(message: String) {
//        udpMessage.postValue(message)
//    }
}



//class UdpReceiverThread(private val port: Int, private val viewModel: UdpViewModel) : Thread() {
//
//    override fun run() {
//        val bufferSize = 1024
//        val receiveData = ByteArray(bufferSize)
//
//        val socket = DatagramSocket(port)
//
//        while (!isInterrupted) {
//            val receivePacket = DatagramPacket(receiveData, receiveData.size)
//            socket.receive(receivePacket)
//
//            val receivedMessage = String(receivePacket.data, 0, receivePacket.length)
//            val senderAddress = receivePacket.address
//            val senderPort = receivePacket.port
//            println(receivedMessage)
//            viewModel.setUdpMessagePost(receivedMessage)
//        }
//
//        socket.close()
//    }
//}

fun receiveUdpMessage(callback: (String) -> Unit, port: Int, timeoutMillis: Long) {
//    //UdpListener8089.pause()
    DatagramSocket(port).use { socket ->
        socket.soTimeout = timeoutMillis.toInt()

        var receivedMessage: String? = null
        var success = false

        try {
            val bufferSize = 1024
            val receiveData = ByteArray(bufferSize)
            val receivePacket = DatagramPacket(receiveData, receiveData.size)

            socket.receive(receivePacket)
            receivedMessage = String(receivePacket.data, 0, receivePacket.length)
            println("received $receivedMessage")
            success = true
            socket.close()
        } catch (e: SocketTimeoutException) {
            println("Timeout occurred")
            socket.close()
        } catch (e: Exception) {
            println("Error occurred: ${e.message}")
            socket.close()
//            //    UdpListener8089.resume()
        }

        if (success) {
            if (receivedMessage != null) {
                callback(receivedMessage)
            }
            socket.close()
        } else {
            callback("failed")
            socket.close()
        }
    }
}

fun receiveUdpMessage_answer_analyze(callback: (String) -> Unit, port: Int, timeoutMillis: Long,mac:String) {
    //UdpListener8089.pause()
    DatagramSocket(port).use { socket ->
        socket.soTimeout = timeoutMillis.toInt()
        var endTime = System.currentTimeMillis() + timeoutMillis
        var success = false

        while (System.currentTimeMillis() < endTime) {
            try {
                val bufferSize = 1024
                val receiveData = ByteArray(bufferSize)
                val receivePacket = DatagramPacket(receiveData, receiveData.size)

                socket.receive(receivePacket)
                val receivedMessage = String(receivePacket.data, 0, receivePacket.length)
                println("Received: $receivedMessage")

                // چک کنید که آیا پیام با "rfsh" شروع می‌شود
                if (receivedMessage.startsWith("rfsh") || receivedMessage.startsWith("cmnd")|| receivedMessage.startsWith("sixc") || !receivedMessage.contains(mac)) {
                    println("Received incorrect message, continuing to listen...")
                    continue // به گوش دادن ادامه دهید
                }

                success = true
                callback(receivedMessage)
                break // پیام صحیح دریافت شد، خارج شوید

            } catch (e: SocketTimeoutException) {
                println("Timeout occurred while listening for messages")

                break // اگر تایم‌اوت رخ داد، حلقه را بشکنید
            } catch (e: Exception) {
                println("Error occurred: ${e.message}")
                //    UdpListener8089.resume()
                break // برای هر خطای دیگر، حلقه را بشکنید
            }
        }

        if (!success) {
            callback("failed") // اگر پیام صحیح دریافت نشد
        }
    }
//    //    UdpListener8089.resume()
}

fun receiveUdpMessage_answer_analyze_old(callback: (String) -> Unit, port: Int, timeoutMillis: Long,mac:String) {
    //UdpListener8089.pause()
    DatagramSocket(port).use { socket ->
        socket.soTimeout = timeoutMillis.toInt()
        var endTime = System.currentTimeMillis() + timeoutMillis
        var success = false

        while (System.currentTimeMillis() < endTime) {
            try {
                val bufferSize = 1024
                val receiveData = ByteArray(bufferSize)
                val receivePacket = DatagramPacket(receiveData, receiveData.size)

                socket.receive(receivePacket)
                val receivedMessage = String(receivePacket.data, 0, receivePacket.length)
                println("Received: $receivedMessage")

                // چک کنید که آیا پیام با "rfsh" شروع می‌شود
                if (receivedMessage.startsWith("rfsh") || receivedMessage.startsWith("cmnd")|| receivedMessage.startsWith("sixc") || !receivedMessage.contains(mac)) {

                    println(mac)
                    println(receivedMessage)
                    println("Received incorrect message, continuing to listen...")
                    continue // به گوش دادن ادامه دهید
                }

                success = true
                callback(receivedMessage)
                break // پیام صحیح دریافت شد، خارج شوید

            } catch (e: SocketTimeoutException) {
                println("Timeout occurred while listening for messages")

                break // اگر تایم‌اوت رخ داد، حلقه را بشکنید
            } catch (e: Exception) {
                println("Error occurred: ${e.message}")
                //    UdpListener8089.resume()
                break // برای هر خطای دیگر، حلقه را بشکنید
            }
        }

        if (!success) {
            callback("failed") // اگر پیام صحیح دریافت نشد
        }
    }
//    //    UdpListener8089.resume()
}



fun receiveUdpMessage_send_inside_thermostat_cmnd(
    callback: (String) -> Unit,
    port: Int,
    timeoutMillis: Long,
    mac:String,mood:String?,temperature:String?,fan_status:String?,on_off:String?,myip: String?,target_ip:String?
) {
    //UdpListener8089.pause()

    val t  ="25"
    val status= "$on_off$t$temperature$fan_status$mood---------"



    val messageToSend ="cmnd~>$mac~>Tmpr~>0000~>$status~>0000~>$myip"
    val address = InetAddress.getByName(target_ip) // Target IP address
    val sendPort = port // Target port for sending message

    DatagramSocket(port).use { socket ->
        socket.soTimeout = timeoutMillis.toInt()
        val endTime = System.currentTimeMillis() + timeoutMillis

        try {
            // Send the message
            val sendData = messageToSend.toByteArray()
            val sendPacket = DatagramPacket(sendData, sendData.size, address, sendPort)
            socket.send(sendPacket)
//            println("direct IP sent")
            println("Message sent: $messageToSend")

            // Continue listening until timeout or a correct message is received
            while (System.currentTimeMillis() < endTime) {
                try {
                    // Receive the response
                    val bufferSize = 1024
                    val receiveData = ByteArray(bufferSize)
                    val receivePacket = DatagramPacket(receiveData, receiveData.size)
                    socket.receive(receivePacket)
                    val receivedMessage = String(receivePacket.data, 0, receivePacket.length)
                    println("Received: $receivedMessage")

                    // Check if the received message is correct (not starting with "rfsh")
                    if (!receivedMessage.startsWith("rfsh") && !receivedMessage.startsWith("cmnd") && (receivedMessage.contains(mac))) {
                        // Callback with the received correct message
                        callback(receivedMessage)
                        return // Exit after receiving correct message
                    } else {
                        println("Incorrect message received, continuing to listen...")
                    }
                } catch (e: SocketTimeoutException) {
                    println("Timeout occurred while listening for messages")
                    break // Exit if timeout occurs
                }
            }
            callback("failed") // If correct message not received within timeout
        } catch (e: Exception) {
            println("Error occurred: ${e.message}")
            //    UdpListener8089.resume()
            callback("failed")
        } finally {
//            //    UdpListener8089.resume()
        }
    }
}

fun receiveUdpMessage_send_inside_plug_cmnd(
    callback: (String) -> Unit,
    port: Int,
    timeoutMillis: Long,
    mac:String?,subtype:String?,status:String?,myip:String,ip:String?
) {
    //UdpListener8089.pause()






    val messageToSend ="cmnd~>$mac~>Plug~>$subtype~>$status~>0000~>$myip"
    val address = InetAddress.getByName(ip) // Target IP address
    val sendPort = port // Target port for sending message

    DatagramSocket(port).use { socket ->
        socket.soTimeout = timeoutMillis.toInt()
        val endTime = System.currentTimeMillis() + timeoutMillis

        try {
            // Send the message
            val sendData = messageToSend.toByteArray()
            val sendPacket = DatagramPacket(sendData, sendData.size, address, sendPort)
            socket.send(sendPacket)
//            println("direct IP sent")
            println("Message sent: $messageToSend")

            // Continue listening until timeout or a correct message is received
            while (System.currentTimeMillis() < endTime) {
                try {
                    // Receive the response
                    val bufferSize = 1024
                    val receiveData = ByteArray(bufferSize)
                    val receivePacket = DatagramPacket(receiveData, receiveData.size)
                    socket.receive(receivePacket)
                    val receivedMessage = String(receivePacket.data, 0, receivePacket.length)
                    println("Received: $receivedMessage")

                    // Check if the received message is correct (not starting with "rfsh")
                    if (!receivedMessage.startsWith("rfsh") && !receivedMessage.startsWith("cmnd") && (mac?.let {
                            receivedMessage.contains(
                                it
                            )
                        } == true)) {
                        // Callback with the received correct message
                        callback(receivedMessage)
                        return // Exit after receiving correct message
                    } else {
                        println("Incorrect message received, continuing to listen...")
                    }
                } catch (e: SocketTimeoutException) {
                    println("Timeout occurred while listening for messages")
                    break // Exit if timeout occurs
                }
            }
            callback("failed") // If correct message not received within timeout
        } catch (e: Exception) {
            println("Error occurred: ${e.message}")
            //    UdpListener8089.resume()
            callback("failed")
        } finally {
//            //    UdpListener8089.resume()
        }
    }
}
fun receiveUdpMessage_send_inside_fan_cmnd(
    callback: (String) -> Unit,
    port: Int,
    timeoutMillis: Long,
    mac:String?,subtype:String?,status:String?,myip:String,ip:String?
) {
    //UdpListener8089.pause()






    val messageToSend = "cmnd~>$mac~>Fano~>$subtype~>$status~>0000~>$myip"
    val address = InetAddress.getByName(ip) // Target IP address
    val sendPort = port // Target port for sending message

    DatagramSocket(port).use { socket ->
        socket.soTimeout = timeoutMillis.toInt()
        val endTime = System.currentTimeMillis() + timeoutMillis

        try {
            // Send the message
            val sendData = messageToSend.toByteArray()
            val sendPacket = DatagramPacket(sendData, sendData.size, address, sendPort)
            socket.send(sendPacket)
//            println("direct IP sent")
            println("Message sent: $messageToSend")

            // Continue listening until timeout or a correct message is received
            while (System.currentTimeMillis() < endTime) {
                try {
                    // Receive the response
                    val bufferSize = 1024
                    val receiveData = ByteArray(bufferSize)
                    val receivePacket = DatagramPacket(receiveData, receiveData.size)
                    socket.receive(receivePacket)
                    val receivedMessage = String(receivePacket.data, 0, receivePacket.length)
                    println("Received: $receivedMessage")

                    // Check if the received message is correct (not starting with "rfsh")
                    if (!receivedMessage.startsWith("rfsh") && !receivedMessage.startsWith("cmnd") && (mac?.let {
                            receivedMessage.contains(
                                it
                            )
                        } == true)) {
                        // Callback with the received correct message
                        callback(receivedMessage)
                        return // Exit after receiving correct message
                    } else {
                        println("Incorrect message received, continuing to listen...")
                    }
                } catch (e: SocketTimeoutException) {
                    println("Timeout occurred while listening for messages")
                    break // Exit if timeout occurs
                }
            }
            callback("failed") // If correct message not received within timeout
        } catch (e: Exception) {
            println("Error occurred: ${e.message}")
            //    UdpListener8089.resume()
            callback("failed")
        } finally {
//            //    UdpListener8089.resume()
        }
    }
}

fun receiveUdpMessage_send_inside_fan_cmnd_broadcast(
    callback: (String) -> Unit,
    port: Int,
    timeoutMillis: Long,
    mac:String?,subtype:String?,status:String?,myip:String,ip:String?
) {
    //UdpListener8089.pause()






    val messageToSend = "cmnd~>$mac~>Fano~>$subtype~>$status~>0000~>$myip"
    val address = InetAddress.getByName(ip) // Target IP address
    val sendPort = port // Target port for sending message

    DatagramSocket(port).use { socket ->
        socket.soTimeout = timeoutMillis.toInt()
        val endTime = System.currentTimeMillis() + timeoutMillis

        try {
            // Send the message
            val sendData = messageToSend.toByteArray()
            val sendPacket = DatagramPacket(sendData, sendData.size, address, sendPort)
            socket.send(sendPacket)
            socket.send(sendPacket)
            socket.send(sendPacket)
            socket.send(sendPacket)
            socket.send(sendPacket)
//            println("direct IP sent")
            println("Message sent: $messageToSend")

            // Continue listening until timeout or a correct message is received
            while (System.currentTimeMillis() < endTime) {
                try {
                    // Receive the response
                    val bufferSize = 1024
                    val receiveData = ByteArray(bufferSize)
                    val receivePacket = DatagramPacket(receiveData, receiveData.size)
                    socket.receive(receivePacket)
                    val receivedMessage = String(receivePacket.data, 0, receivePacket.length)
                    println("Received: $receivedMessage")

                    // Check if the received message is correct (not starting with "rfsh")
                    if (!receivedMessage.startsWith("rfsh") && !receivedMessage.startsWith("cmnd") && (mac?.let {
                            receivedMessage.contains(
                                it
                            )
                        } == true)) {
                        // Callback with the received correct message
                        callback(receivedMessage)
                        return // Exit after receiving correct message
                    } else {
                        println("Incorrect message received, continuing to listen...")
                    }
                } catch (e: SocketTimeoutException) {
                    println("Timeout occurred while listening for messages")
                    break // Exit if timeout occurs
                }
            }
            callback("failed") // If correct message not received within timeout
        } catch (e: Exception) {
            println("Error occurred: ${e.message}")
            //    UdpListener8089.resume()
            callback("failed")
        } finally {
//            //    UdpListener8089.resume()
        }
    }
}

fun receiveUdpMessage_send_inside_curtain_cmnd(
    callback: (String) -> Unit,
    port: Int,
    timeoutMillis: Long,
    mac:String?,status: String?,myip: String?,ip:String?
) {
    //UdpListener8089.pause()






    val messageToSend ="cmnd~>$mac~>Crtn~>0000~>${status}00000000000000~>0000~>$myip"
    val address = InetAddress.getByName(ip) // Target IP address
    val sendPort = port // Target port for sending message

    DatagramSocket(port).use { socket ->
        socket.soTimeout = timeoutMillis.toInt()
        val endTime = System.currentTimeMillis() + timeoutMillis

        try {
            // Send the message
            val sendData = messageToSend.toByteArray()
            val sendPacket = DatagramPacket(sendData, sendData.size, address, sendPort)
            socket.send(sendPacket)
//            println("direct IP sent")
            println("Message sent: $messageToSend")

            // Continue listening until timeout or a correct message is received
            while (System.currentTimeMillis() < endTime) {
                try {
                    // Receive the response
                    val bufferSize = 1024
                    val receiveData = ByteArray(bufferSize)
                    val receivePacket = DatagramPacket(receiveData, receiveData.size)
                    socket.receive(receivePacket)
                    val receivedMessage = String(receivePacket.data, 0, receivePacket.length)
                    println("Received: $receivedMessage")

                    // Check if the received message is correct (not starting with "rfsh")
                    if (!receivedMessage.startsWith("rfsh") && !receivedMessage.startsWith("cmnd") && (mac?.let {
                            receivedMessage.contains(
                                it
                            )
                        } == true)) {
                        // Callback with the received correct message
                        callback(receivedMessage)
                        return // Exit after receiving correct message
                    } else {
                        println("Incorrect message received, continuing to listen...")
                    }
                } catch (e: SocketTimeoutException) {
                    println("Timeout occurred while listening for messages")
                    break // Exit if timeout occurs
                }
            }
            callback("failed") // If correct message not received within timeout
        } catch (e: Exception) {
            println("Error occurred: ${e.message}")
            //    UdpListener8089.resume()
            callback("failed")
        } finally {
//            //    UdpListener8089.resume()
        }
    }
}

fun receiveUdpMessage_send_inside_curtain_cmnd_broadcast(
    callback: (String) -> Unit,
    port: Int,
    timeoutMillis: Long,
    mac:String?,status: String?,myip: String?,ip:String?
) {
    //UdpListener8089.pause()






    val messageToSend ="cmnd~>$mac~>Crtn~>0000~>${status}00000000000000~>0000~>$myip"
    val address = InetAddress.getByName(ip) // Target IP address
    val sendPort = port // Target port for sending message

    DatagramSocket(port).use { socket ->
        socket.soTimeout = timeoutMillis.toInt()
        val endTime = System.currentTimeMillis() + timeoutMillis

        try {
            // Send the message
            val sendData = messageToSend.toByteArray()
            val sendPacket = DatagramPacket(sendData, sendData.size, address, sendPort)
            socket.send(sendPacket)
            socket.send(sendPacket)
            socket.send(sendPacket)
            socket.send(sendPacket)
            socket.send(sendPacket)
//            println("direct IP sent")
            println("Message sent: $messageToSend")

            // Continue listening until timeout or a correct message is received
            while (System.currentTimeMillis() < endTime) {
                try {
                    // Receive the response
                    val bufferSize = 1024
                    val receiveData = ByteArray(bufferSize)
                    val receivePacket = DatagramPacket(receiveData, receiveData.size)
                    socket.receive(receivePacket)
                    val receivedMessage = String(receivePacket.data, 0, receivePacket.length)
                    println("Received: $receivedMessage")

                    // Check if the received message is correct (not starting with "rfsh")
                    if (!receivedMessage.startsWith("rfsh") && !receivedMessage.startsWith("cmnd") && (mac?.let {
                            receivedMessage.contains(
                                it
                            )
                        } == true)) {
                        // Callback with the received correct message
                        callback(receivedMessage)
                        return // Exit after receiving correct message
                    } else {
                        println("Incorrect message received, continuing to listen...")
                    }
                } catch (e: SocketTimeoutException) {
                    println("Timeout occurred while listening for messages")
                    break // Exit if timeout occurs
                }
            }
            callback("failed") // If correct message not received within timeout
        } catch (e: Exception) {
            println("Error occurred: ${e.message}")
            //    UdpListener8089.resume()
            callback("failed")
        } finally {
//            //    UdpListener8089.resume()
        }
    }
}

fun receiveUdpMessage_send_inside_valve_cmnd(
    callback: (String) -> Unit,
    port: Int,
    timeoutMillis: Long,
    mac:String?,subtype:String?,status:String?,myip:String,ip:String?
) {
    //UdpListener8089.pause()






    val messageToSend ="cmnd~>$mac~>Valv~>$subtype~>$status~>0000~>$myip"
    val address = InetAddress.getByName(ip) // Target IP address
    val sendPort = port // Target port for sending message

    DatagramSocket(port).use { socket ->
        socket.soTimeout = timeoutMillis.toInt()
        val endTime = System.currentTimeMillis() + timeoutMillis

        try {
            // Send the message
            val sendData = messageToSend.toByteArray()
            val sendPacket = DatagramPacket(sendData, sendData.size, address, sendPort)
            socket.send(sendPacket)
//            println("direct IP sent")
            println("Message sent: $messageToSend")

            // Continue listening until timeout or a correct message is received
            while (System.currentTimeMillis() < endTime) {
                try {
                    // Receive the response
                    val bufferSize = 1024
                    val receiveData = ByteArray(bufferSize)
                    val receivePacket = DatagramPacket(receiveData, receiveData.size)
                    socket.receive(receivePacket)
                    val receivedMessage = String(receivePacket.data, 0, receivePacket.length)
                    println("Received: $receivedMessage")

                    // Check if the received message is correct (not starting with "rfsh")
                    if (!receivedMessage.startsWith("rfsh") && !receivedMessage.startsWith("cmnd") && (mac?.let {
                            receivedMessage.contains(
                                it
                            )
                        } == true)) {
                        // Callback with the received correct message
                        callback(receivedMessage)
                        return // Exit after receiving correct message
                    } else {
                        println("Incorrect message received, continuing to listen...")
                    }
                } catch (e: SocketTimeoutException) {
                    println("Timeout occurred while listening for messages")
                    break // Exit if timeout occurs
                }
            }
            callback("failed") // If correct message not received within timeout
        } catch (e: Exception) {
            println("Error occurred: ${e.message}")
            //    UdpListener8089.resume()
            callback("failed")
        } finally {
//            //    UdpListener8089.resume()
        }
    }
}

fun receiveUdpMessage_send_inside_valve_cmnd_broadcast(
    callback: (String) -> Unit,
    port: Int,
    timeoutMillis: Long,
    mac:String?,subtype:String?,status:String?,myip:String,ip:String?
) {
    //UdpListener8089.pause()






    val messageToSend ="cmnd~>$mac~>Valv~>$subtype~>$status~>0000~>$myip"
    val address = InetAddress.getByName(ip) // Target IP address
    val sendPort = port // Target port for sending message

    DatagramSocket(port).use { socket ->
        socket.soTimeout = timeoutMillis.toInt()
        val endTime = System.currentTimeMillis() + timeoutMillis

        try {
            // Send the message
            val sendData = messageToSend.toByteArray()
            val sendPacket = DatagramPacket(sendData, sendData.size, address, sendPort)
            socket.send(sendPacket)
            socket.send(sendPacket)
            socket.send(sendPacket)
            socket.send(sendPacket)
            socket.send(sendPacket)
//            println("direct IP sent")
            println("Message sent: $messageToSend")

            // Continue listening until timeout or a correct message is received
            while (System.currentTimeMillis() < endTime) {
                try {
                    // Receive the response
                    val bufferSize = 1024
                    val receiveData = ByteArray(bufferSize)
                    val receivePacket = DatagramPacket(receiveData, receiveData.size)
                    socket.receive(receivePacket)
                    val receivedMessage = String(receivePacket.data, 0, receivePacket.length)
                    println("Received: $receivedMessage")

                    // Check if the received message is correct (not starting with "rfsh")
                    if (!receivedMessage.startsWith("rfsh") && !receivedMessage.startsWith("cmnd") && (mac?.let {
                            receivedMessage.contains(
                                it
                            )
                        } == true)) {
                        // Callback with the received correct message
                        callback(receivedMessage)
                        return // Exit after receiving correct message
                    } else {
                        println("Incorrect message received, continuing to listen...")
                    }
                } catch (e: SocketTimeoutException) {
                    println("Timeout occurred while listening for messages")
                    break // Exit if timeout occurs
                }
            }
            callback("failed") // If correct message not received within timeout
        } catch (e: Exception) {
            println("Error occurred: ${e.message}")
            //    UdpListener8089.resume()
            callback("failed")
        } finally {
//            //    UdpListener8089.resume()
        }
    }
}


fun receiveUdpMessage_send_inside_plug_cmnd_broadcast(
    callback: (String) -> Unit,
    port: Int,
    timeoutMillis: Long,
    mac:String?,subtype:String?,status:String?,myip:String,ip:String?
) {
    //UdpListener8089.pause()






    val messageToSend ="cmnd~>$mac~>Plug~>$subtype~>$status~>0000~>$myip"
    val address = InetAddress.getByName(ip) // Target IP address
    val sendPort = port // Target port for sending message

    DatagramSocket(port).use { socket ->
        socket.soTimeout = timeoutMillis.toInt()
        val endTime = System.currentTimeMillis() + timeoutMillis

        try {
            // Send the message
            val sendData = messageToSend.toByteArray()
            val sendPacket = DatagramPacket(sendData, sendData.size, address, sendPort)
            socket.send(sendPacket)
            socket.send(sendPacket)
            socket.send(sendPacket)
            socket.send(sendPacket)
            socket.send(sendPacket)
//            println("direct IP sent")
            println("Message sent: $messageToSend")

            // Continue listening until timeout or a correct message is received
            while (System.currentTimeMillis() < endTime) {
                try {
                    // Receive the response
                    val bufferSize = 1024
                    val receiveData = ByteArray(bufferSize)
                    val receivePacket = DatagramPacket(receiveData, receiveData.size)
                    socket.receive(receivePacket)
                    val receivedMessage = String(receivePacket.data, 0, receivePacket.length)
                    println("Received: $receivedMessage")

                    // Check if the received message is correct (not starting with "rfsh")
                    if (!receivedMessage.startsWith("rfsh") && !receivedMessage.startsWith("cmnd") && (mac?.let {
                            receivedMessage.contains(
                                it
                            )
                        } == true)) {
                        // Callback with the received correct message
                        callback(receivedMessage)
                        return // Exit after receiving correct message
                    } else {
                        println("Incorrect message received, continuing to listen...")
                    }
                } catch (e: SocketTimeoutException) {
                    println("Timeout occurred while listening for messages")
                    break // Exit if timeout occurs
                }
            }
            callback("failed") // If correct message not received within timeout
        } catch (e: Exception) {
            println("Error occurred: ${e.message}")
            //    UdpListener8089.resume()
            callback("failed")
        } finally {
//            //    UdpListener8089.resume()
        }
    }
}

fun receiveUdpMessage_send_inside_light_cmnd_broadcast(
    callback: (String) -> Unit,
    port: Int,
    timeoutMillis: Long,
    mac:String?,subtype:String?,status:String?,myip:String,ip:String?
) {
    //UdpListener8089.pause()





    val messageToSend ="cmnd~>$mac~>Lght~>$subtype~>$status~>0000~>$myip"
    val address = InetAddress.getByName(ip) // Target IP address
    val sendPort = port // Target port for sending message

    DatagramSocket(port).use { socket ->
        socket.soTimeout = timeoutMillis.toInt()
        val endTime = System.currentTimeMillis() + timeoutMillis

        try {
            // Send the message
            val sendData = messageToSend.toByteArray()
            val sendPacket = DatagramPacket(sendData, sendData.size, address, sendPort)
            socket.send(sendPacket)
            socket.send(sendPacket)
            socket.send(sendPacket)
            socket.send(sendPacket)
            socket.send(sendPacket)
//            println("direct IP sent")
            println("Message sent: $messageToSend")

            // Continue listening until timeout or a correct message is received
            while (System.currentTimeMillis() < endTime) {
                try {
                    // Receive the response
                    val bufferSize = 1024
                    val receiveData = ByteArray(bufferSize)
                    val receivePacket = DatagramPacket(receiveData, receiveData.size)
                    socket.receive(receivePacket)
                    val receivedMessage = String(receivePacket.data, 0, receivePacket.length)
                    println("Received: $receivedMessage")

                    // Check if the received message is correct (not starting with "rfsh")
                    if (!receivedMessage.startsWith("rfsh") && !receivedMessage.startsWith("cmnd") && (mac?.let {
                            receivedMessage.contains(
                                it
                            )
                        } == true)) {
                        // Callback with the received correct message
                        callback(receivedMessage)
                        return // Exit after receiving correct message
                    } else {
                        println("Incorrect message received, continuing to listen...")
                    }
                } catch (e: SocketTimeoutException) {
                    println("Timeout occurred while listening for messages")
                    break // Exit if timeout occurs
                }
            }
            callback("failed") // If correct message not received within timeout
        } catch (e: Exception) {
            println("Error occurred: ${e.message}")
            //    UdpListener8089.resume()
            callback("failed")
        } finally {
//            //    UdpListener8089.resume()
        }
    }
}

fun receiveUdpMessage_send_inside_light_cmnd(
    callback: (String) -> Unit,
    port: Int,
    timeoutMillis: Long,
    mac:String?,subtype:String?,status:String?,myip:String,ip:String?
) {
    //UdpListener8089.pause()





    val messageToSend ="cmnd~>$mac~>Lght~>$subtype~>$status~>0000~>$myip"
    val address = InetAddress.getByName(ip) // Target IP address
    val sendPort = port // Target port for sending message

    DatagramSocket(port).use { socket ->
        socket.soTimeout = timeoutMillis.toInt()
        val endTime = System.currentTimeMillis() + timeoutMillis

        try {
            // Send the message
            val sendData = messageToSend.toByteArray()
            val sendPacket = DatagramPacket(sendData, sendData.size, address, sendPort)
            socket.send(sendPacket)
//            println("direct IP sent")
            println("Message sent: $messageToSend")

            // Continue listening until timeout or a correct message is received
            while (System.currentTimeMillis() < endTime) {
                try {
                    // Receive the response
                    val bufferSize = 1024
                    val receiveData = ByteArray(bufferSize)
                    val receivePacket = DatagramPacket(receiveData, receiveData.size)
                    socket.receive(receivePacket)
                    val receivedMessage = String(receivePacket.data, 0, receivePacket.length)
                    println("Received: $receivedMessage")

                    // Check if the received message is correct (not starting with "rfsh")
                    if (!receivedMessage.startsWith("rfsh") && !receivedMessage.startsWith("cmnd") && (mac?.let {
                            receivedMessage.contains(
                                it
                            )
                        } == true)) {
                        // Callback with the received correct message
                        callback(receivedMessage)
                        return // Exit after receiving correct message
                    } else {
                        println("Incorrect message received, continuing to listen...")
                    }
                } catch (e: SocketTimeoutException) {
                    println("Timeout occurred while listening for messages")
                    break // Exit if timeout occurs
                }
            }
            callback("failed") // If correct message not received within timeout
        } catch (e: Exception) {
            println("Error occurred: ${e.message}")
            //    UdpListener8089.resume()
            callback("failed")
        } finally {
//            //    UdpListener8089.resume()
        }
    }
}

fun receiveUdpMessage_send_inside_thermostat_cmnd_broadcast(
    callback: (String) -> Unit,
    port: Int,
    timeoutMillis: Long,
    mac:String,mood:String?,temperature:String?,fan_status:String?,on_off:String?,myip: String?,target_ip:String?
) {
    //UdpListener8089.pause()

    val t  ="25"
    val status= "$on_off$t$temperature$fan_status$mood---------"



    val messageToSend ="cmnd~>$mac~>Tmpr~>0000~>$status~>0000~>$myip"
    val address = InetAddress.getByName(target_ip) // Target IP address
    val sendPort = port // Target port for sending message

    DatagramSocket(port).use { socket ->
        socket.soTimeout = timeoutMillis.toInt()
        val endTime = System.currentTimeMillis() + timeoutMillis

        try {
            // Send the message
            val sendData = messageToSend.toByteArray()
            val sendPacket = DatagramPacket(sendData, sendData.size, address, sendPort)
            socket.send(sendPacket)
            socket.send(sendPacket)
            socket.send(sendPacket)
            socket.send(sendPacket)
            socket.send(sendPacket)
//            println("direct IP sent")
            println("Message sent: $messageToSend")

            // Continue listening until timeout or a correct message is received
            while (System.currentTimeMillis() < endTime) {
                try {
                    // Receive the response
                    val bufferSize = 1024
                    val receiveData = ByteArray(bufferSize)
                    val receivePacket = DatagramPacket(receiveData, receiveData.size)
                    socket.receive(receivePacket)
                    val receivedMessage = String(receivePacket.data, 0, receivePacket.length)
                    println("Received: $receivedMessage")

                    // Check if the received message is correct (not starting with "rfsh")
                    if (!receivedMessage.startsWith("rfsh") && !receivedMessage.startsWith("cmnd") && (receivedMessage.contains(mac))) {
                        // Callback with the received correct message
                        callback(receivedMessage)
                        return // Exit after receiving correct message
                    } else {
                        println("Incorrect message received, continuing to listen...")
                    }
                } catch (e: SocketTimeoutException) {
                    println("Timeout occurred while listening for messages")
                    break // Exit if timeout occurs
                }
            }
            callback("failed") // If correct message not received within timeout
        } catch (e: Exception) {
            println("Error occurred: ${e.message}")
            //    UdpListener8089.resume()
            callback("failed")
        } finally {
//            //    UdpListener8089.resume()
        }
    }
}


fun receiveUdpMessage_send_inside(
    callback: (String) -> Unit,
    port: Int,
    timeoutMillis: Long,
    target_ip: String,
    mac: String,
    myip: String
) {
    //UdpListener8089.pause()

    val messageToSend = "rfsh~>$mac~>0000~>$myip"
    val address = InetAddress.getByName(target_ip) // Target IP address
    val sendPort = port // Target port for sending message

    DatagramSocket(port).use { socket ->
        socket.soTimeout = timeoutMillis.toInt()
        val endTime = System.currentTimeMillis() + timeoutMillis

        try {
            // Send the message
            val sendData = messageToSend.toByteArray()
            val sendPacket = DatagramPacket(sendData, sendData.size, address, sendPort)
            socket.send(sendPacket)
            println("direct IP sent")
            println("Message sent: $messageToSend")

            // Continue listening until timeout or a correct message is received
            while (System.currentTimeMillis() < endTime) {
                try {
                    // Receive the response
                    val bufferSize = 1024
                    val receiveData = ByteArray(bufferSize)
                    val receivePacket = DatagramPacket(receiveData, receiveData.size)
                    socket.receive(receivePacket)
                    val receivedMessage = String(receivePacket.data, 0, receivePacket.length)
                    println("Received: $receivedMessage")

                    // Check if the received message is correct (not starting with "rfsh")
                    if (!receivedMessage.startsWith("rfsh") && (receivedMessage.contains(mac))) {
                        // Callback with the received correct message
                        callback(receivedMessage)
                        return // Exit after receiving correct message
                    } else {
                        println("Incorrect message received, continuing to listen...")
                    }
                } catch (e: SocketTimeoutException) {
                    println("Timeout occurred while listening for messages")
                    break // Exit if timeout occurs
                }
            }
            callback("failed") // If correct message not received within timeout
        } catch (e: Exception) {
            println("Error occurred: ${e.message}")
            //    UdpListener8089.resume()
            callback("failed")
        } finally {
//            //    UdpListener8089.resume()
        }
    }
}


fun receiveUdpMessage_send_inside_brodcast(
    callback: (String) -> Unit,
    port: Int,
    timeoutMillis: Long,
    target_ip: String,
    mac: String,
    myip: String
) {
    //UdpListener8089.pause()

    val messageToSend = "rfsh~>$mac~>0000~>$myip"
    val address = InetAddress.getByName(target_ip) // Target IP address
    val sendPort = port // Target port for sending message

    DatagramSocket(port).use { socket ->
        socket.soTimeout = timeoutMillis.toInt()
        val endTime = System.currentTimeMillis() + timeoutMillis

        try {
            // Send the message
            val sendData = messageToSend.toByteArray()
            val sendPacket = DatagramPacket(sendData, sendData.size, address, sendPort)
            socket.send(sendPacket)
            socket.send(sendPacket)
            socket.send(sendPacket)
            socket.send(sendPacket)
            socket.send(sendPacket)
            println("Message sent: $messageToSend")

            // Continue listening until timeout or a correct message is received
            while (System.currentTimeMillis() < endTime) {
                try {
                    // Receive the response
                    val bufferSize = 1024
                    val receiveData = ByteArray(bufferSize)
                    val receivePacket = DatagramPacket(receiveData, receiveData.size)
                    socket.receive(receivePacket)
                    val receivedMessage = String(receivePacket.data, 0, receivePacket.length)
                    println("Received: $receivedMessage")

                    // Check if the received message is correct (not starting with "rfsh")
                    if (!receivedMessage.startsWith("rfsh") && (receivedMessage.contains(mac))) {
                        // Callback with the received correct message
                        callback(receivedMessage)
                        return // Exit after receiving correct message
                    } else {
                        println("Incorrect message received, continuing to listen...")
                    }
                } catch (e: SocketTimeoutException) {
                    println("Timeout occurred while listening for messages")
                    break // Exit if timeout occurs
                }
            }
            callback("failed") // If correct message not received within timeout
        } catch (e: Exception) {
            println("Error occurred: ${e.message}")
            //    UdpListener8089.resume()
            callback("failed")
        } finally {
//            //    UdpListener8089.resume()
        }
    }
}



fun receiveUdpMessage2(callback: (String) -> Unit, port: Int, timeoutMillis: Long) {
    DatagramSocket(port).use { socket ->
        socket.soTimeout = timeoutMillis.toInt()

        var receivedMessage: String? = null
        var success = false

        try {
            while (true){

                val bufferSize = 1024
                val receiveData = ByteArray(bufferSize)
                val receivePacket = DatagramPacket(receiveData, receiveData.size)

                socket.receive(receivePacket)
                receivedMessage = String(receivePacket.data, 0, receivePacket.length)
                println("received $receivedMessage")
                success = true

            }

        } catch (e: SocketTimeoutException) {
            println("Timeout occurred")
        } catch (e: Exception) {
            println("Error occurred: ${e.message}")
        }

        if (success) {
            if (receivedMessage != null) {
                callback(receivedMessage)
            }
        } else {
            callback("failed")
        }
    }
}

//fun receiveUdpMessage(callback: (String) -> Unit, port: Int, timeoutMillis: Long) {
//
//        try {
//            val bufferSize = 1024
//            val receiveData = ByteArray(bufferSize)
//
//            val socket = DatagramSocket(port)
//            socket.soTimeout = timeoutMillis.toInt() // تنظیم زمان انتظار برای سوکت
//
//            var receivedMessage: String? = null
//            var success = false // نشان دهنده دریافت موفقیت‌آمیز یا ناموفقیت‌آمیز پیام
//
//            try {
//                val receivePacket = DatagramPacket(receiveData, receiveData.size)
//                socket.receive(receivePacket)
//
//                receivedMessage = String(receivePacket.data, 0, receivePacket.length)
//                println("received $receivedMessage")
//                success = true
//            } catch (e: SocketTimeoutException) {
//                println("Timeout occurred")
//            } catch (e: Exception) {
//                println("Error occurred: ${e.message}")
//            } finally {
//                socket.close()
//
//                if (success) {
//                    if (receivedMessage != null) {
//                        callback(receivedMessage)
//                    }
//                } else {
//                    callback("failed")
//                }
//            }
//
//        }catch (e:Exception){
//
//            println(e)
//        }
//
//
//}


fun startListening() {
    GlobalScope.launch(Dispatchers.IO) {
        val bufferSize = 1024
        val receiveData = ByteArray(bufferSize)
        var socket: DatagramSocket? = null

        while (true) {
            try {
                if (socket == null || socket.isClosed) {
                    socket = DatagramSocket(8090)
                    println("Listenning to 8090..")
                }

                val receivePacket = DatagramPacket(receiveData, receiveData.size)
                socket.receive(receivePacket)

                val receivedMessage = String(receivePacket.data, 0, receivePacket.length)
                println("received messagefrom 8090 : $receivedMessage")

                // پیام دریافتی را بررسی و پردازش کنید

            } catch (e: SocketException) {
                // سوکت بسته شده است، اتصال مجدد را انجام دهید
                e.printStackTrace()
                socket?.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}




fun listenOnPort( callback: (String) -> Unit) {
    val bufferSize = 1024
    val receiveData = ByteArray(bufferSize)
    var running = true

    DatagramSocket(8089).use { socket ->
        while (running) {
            try {
                val receivePacket = DatagramPacket(receiveData, receiveData.size)
                socket.receive(receivePacket)

                val receivedMessage = String(receivePacket.data, 0, receivePacket.length)
                callback(receivedMessage)
            } catch (e: SocketException) {
                // نخ قطع شده است، متوقف شود
                running = false
            }
        }
    }
}

fun sync_fidback(context:Context ,sy:String,akid:String, ip:String){
    var targetIP=ip
    val my_ip= checkIP(context)
    var message = "sysi~>$my_ip"
    if (akid =="#"){
        message = "sysi~>$my_ip"


    }else{
         message = "$sy~>$akid"

    }

    val serverAddress = InetAddress.getByName(targetIP)
    val serverPort = 8089

    val socket = DatagramSocket()
    val sendData = message.toByteArray()
    val sendPacket = DatagramPacket(sendData, sendData.size, serverAddress, serverPort)
    socket.send(sendPacket)
    socket.close()

}





//################################################  Light ################################################
@SuppressLint("RestrictedApi")
fun udp_light (context: Context, light: Light) : Boolean{
    //UdpListener8089.pause()
    val startTime = System.currentTimeMillis()
    var is_succes=false

    var isResponseReceivedFirstTime = false


    var timeoutMillis = 700
    var loopCount = 0

    do {


        if (loopCount == 0) {
            if (!isResponseReceivedFirstTime){

                val light_database=light_db.getInstance(context)

                if(light_database.getStatusById(light.id)=="on"){

                    println("injas")
                    light_database.updateStatusById(light.id, "off")

                }else{
                    light_database.updateStatusById(light.id, "on")

                }
            }
            println("stage1")

            val light_database = light_db.getInstance(context)


//            println(light_database.getStatusById(light.id))
            val light_2 = light_database.get_from_db_light(light.id)

            var key_status = "----------------"
            val same_macs = light.mac?.let { light_database.getLightsByMacAddress(it) }
            var pole = light?.sub_type?.toInt() ?: 0

            if (same_macs!!.sortedBy { it!!.sub_type?.toInt() }[0]!!.sub_type!!.toInt() == 0) {
                pole += 1
            }

            if (light_2?.status == "on") {
                println("on")
                if (key_status.length >= pole) {
                    val startIndex = pole - 1
                    val endIndex = startIndex + 1
                    val newChar = "1"
                    key_status = key_status.substring(0, startIndex) + newChar + key_status.substring(endIndex)
                }
            } else if (light_2?.status == "off") {
                println("off")
                if (key_status.length >= pole) {
                    val startIndex = pole - 1
                    val endIndex = startIndex + 1
                    val newChar = "0"
                    key_status = key_status.substring(0, startIndex) + newChar + key_status.substring(endIndex)
                }
            }

// جایگزینی محدوده‌های مورد نظر


            print(key_status)

//            println(light.mac)
//            println(light.sub_type)
//            println("sssssssssssssssssssssssssssssssssss"+light.id)
//            println(light_database.get_from_db_light(light.id))
//            send_to_light(light.mac,light.sub_type,key_status, checkIP(context),light_database.get_from_db_light(light.id)!!.ip)
//            send_to_light(light.mac,light.sub_type,key_status, checkIP(context),light_database.get_from_db_light(light.id)!!.ip)
//            send_to_light(light.mac,light.sub_type,key_status, checkIP(context),light_database.get_from_db_light(light.id)!!.ip)
//            send_to_light(light.mac,light.sub_type,key_status, checkIP(context.requireContext()),light_database.get_from_db_light(light.id)!!.ip)

            println("direct IP sent")




            receiveUdpMessage_send_inside_light_cmnd({ receivedMessage ->
                if (receivedMessage=="failed"){
                    println("direct IP failed..")
                    is_succes = false

                }else{
                    if ((!receivedMessage.startsWith("cmnd"))&&(!receivedMessage.startsWith("rfsh"))&&(receivedMessage.split("~>")[1] ==  light.mac) ){
                        println("direct IP success..")
                        timeoutMillis=0
                        isResponseReceivedFirstTime = true
                        val light_database2=light_db.getInstance(context)
                        var receivedmessage_decoded=extract_response(receivedMessage)
                        var  macip=receivedmessage_decoded[0]
                        var  pole_num=receivedmessage_decoded[1]
                        var  key_status=receivedmessage_decoded[2]
                        var ip =receivedmessage_decoded[3]

                        var statusList=light_sub_type_decoder(key_status,pole_num)

                        val light_database = light_db.getInstance(context)
                        val same_macs = light_database.getLightsByMacAddress(light.mac).sortedBy { it!!.sub_type!!.toInt() }
                        var index=0
//                    for (light_same_mac in same_macs){
//                        light_database2.updateStatusById(light_same_mac!!.id,statusList[index])
//                        index+=1
//
//                    }
                        is_succes = true

                    }



                }



            }, 8089, 1000,light.mac,light.sub_type,key_status, checkIP(context),light_database.get_from_db_light(light.id)!!.ip)






        }else if (loopCount == 1 && !isResponseReceivedFirstTime) {
            println("broadcasting..")
            val light_database=light_db.getInstance(context)
            val broadcastIP=convertIpToBroadcast(context)


            var key_status = "----------------"
            val same_macs = light.mac?.let { light_database.getLightsByMacAddress(it) }
            var pole = light?.sub_type?.toInt() ?: 0

            if (same_macs!!.sortedBy { it!!.sub_type?.toInt() }[0]!!.sub_type!!.toInt() == 0) {
                pole += 1
            }

            if (light?.status == "on") {
                println("on")
                if (key_status.length >= pole) {
                    val startIndex = pole - 1
                    val endIndex = startIndex + 1
                    val newChar = "1"
                    key_status = key_status.substring(0, startIndex) + newChar + key_status.substring(endIndex)
                }
            } else if (light?.status == "off") {
                println("off")
                if (key_status.length >= pole) {
                    val startIndex = pole - 1
                    val endIndex = startIndex + 1
                    val newChar = "0"
                    key_status = key_status.substring(0, startIndex) + newChar + key_status.substring(endIndex)
                }
            }

// جایگزینی محدوده‌های مورد نظر


            print(key_status)

//            send_to_light(light.mac,light.sub_type,key_status, checkIP(context),broadcastIP)
//            send_to_light(light.mac,light.sub_type,key_status, checkIP(context),broadcastIP)
            println("broadcast sent")
            var isrecived=false
            receiveUdpMessage_send_inside_light_cmnd_broadcast({ receivedMessage ->
                if (receivedMessage=="failed"){
                    println("broadcast failed..")
                    if(light_database.getStatusById(light.id)=="on"){

                        light_database.updateStatusById(light.id, "off")

                    }else{
                        light_database.updateStatusById(light.id, "on")

                    }
                    println("Broadcast failed..")
                    is_succes = false

                }else{

                     if ((!receivedMessage.startsWith("cmnd"))&&(!receivedMessage.startsWith("rfsh"))&&(receivedMessage.split("~>")[1] ==  light.mac) ){
                        println("success..")
                        var receivedmessage_decoded=extract_response(receivedMessage)
                        var  macip=receivedmessage_decoded[0]
                        var  pole_num=receivedmessage_decoded[1]
                        var  key_status=receivedmessage_decoded[2]
                        var ip =receivedmessage_decoded[3]
                        val light_database3=light_db.getInstance(context)
                        val same_macs = light_database3.getLightsByMacAddress(light.mac).sortedBy { it!!.sub_type!!.toInt() }

//                    var statusList=light_sub_type_decoder(key_status,pole_num)
//                    var index=0
                        for (light_same_mac3 in same_macs){
                            light_database3.updateIpById(light_same_mac3!!.id , ip)
//                        light_database3.updateStatusById(light_same_mac3!!.id,statusList[index])
//                        index+=1
                        }
                         is_succes = true

                    }else{
                        println("Broadcast failed..")
                         is_succes = false
                         //    UdpListener8089.resume()
                    }

                }


            }, 8089, 1000,light.mac,light.sub_type,key_status, checkIP(context),broadcastIP)


        }

        loopCount++

        // تاخیر یک ثانیه قبل از ورود به دور جدید از حلقه
//        Thread.sleep(700)
    } while (loopCount < 2) // تکرار حلقه تا دو بار اجرا

    //    UdpListener8089.resume()
    val endTime = System.currentTimeMillis()
    val duration = endTime - startTime

    println("Time taken: $duration ms")
    return is_succes
}

fun createStatusString(subType: Int, status: Int): String {
    // ابتدا یک آرایه‌ی کاراکتری با طول 16 و پر شده با `-` ایجاد می‌کنیم
    val result = CharArray(16) { '-' }

    // اطمینان حاصل می‌کنیم که subType در محدوده‌ی مجاز باشد (0 تا 15)
    if (subType in 0..15) {
        // مقدار status را در ایندکس مشخص شده قرار می‌دهیم
        result[subType] = status.toString().first()
    }

    // آرایه‌ی کاراکتری را به یک رشته تبدیل و برمی‌گردانیم
    return String(result)
}
fun udp_light_sms (context: Context, light: Light , status:String) : Boolean{
    //UdpListener8089.pause()
    var is_succes=false

    var isResponseReceivedFirstTime = false


    var timeoutMillis = 700
    var loopCount = 0

    do {


        if (loopCount == 0) {
            if (!isResponseReceivedFirstTime){


            }
            println("stage1")

            val light_database = light_db.getInstance(context)


//            send_to_light(light.mac,light.sub_type,status, checkIP(context),light_database.get_from_db_light(light.id)!!.ip)
//            send_to_light(light.mac,light.sub_type,key_status, checkIP(context.requireContext()),light_database.get_from_db_light(light.id)!!.ip)

//            println("direct IP sent")




            receiveUdpMessage_send_inside_light_cmnd({ receivedMessage ->
                if (receivedMessage=="failed"){
                    println("direct IP failed..")
                    is_succes = false

                }else{
                    if ((!receivedMessage.startsWith("cmnd"))&&(!receivedMessage.startsWith("rfsh"))&&(receivedMessage.split("~>")[1] ==  light.mac) ){
                        println("direct IP success..")
                        timeoutMillis=0
                        isResponseReceivedFirstTime = true
                        val light_database2=light_db.getInstance(context)
                        var receivedmessage_decoded=extract_response(receivedMessage)
                        var  macip=receivedmessage_decoded[0]
                        var  pole_num=receivedmessage_decoded[1]
                        var  key_status=receivedmessage_decoded[2]
                        var ip =receivedmessage_decoded[3]

                        var statusList=light_sub_type_decoder(key_status,pole_num)

                        val light_database = light_db.getInstance(context)
                        val same_macs = light_database.getLightsByMacAddress(light.mac).sortedBy { it!!.sub_type!!.toInt() }
                        var index=0
                        //                    for (light_same_mac in same_macs){
                        //                        light_database2.updateStatusById(light_same_mac!!.id,statusList[index])
                        //                        index+=1
                        //
                        //                    }
                        is_succes = true

                    }


                }


            }, 8089, 700, light.mac,light.sub_type,status, checkIP(context),light_database.get_from_db_light(light.id)!!.ip)






        }else if (loopCount == 1 && !isResponseReceivedFirstTime) {
            println("broadcasting..")
            val light_database=light_db.getInstance(context)
            val broadcastIP=convertIpToBroadcast(context)



//            send_to_light(light.mac,light.sub_type,status, checkIP(context),broadcastIP)
//            send_to_light(light.mac,light.sub_type,status, checkIP(context),broadcastIP)
//            println("broadcast sent")
            receiveUdpMessage_send_inside_light_cmnd_broadcast({ receivedMessage ->
                if (receivedMessage=="failed"){
                    println("broadcast failed..")

                    println("Broadcast failed..")
                    is_succes = false

                }else{

                    if ((!receivedMessage.startsWith("cmnd"))&&(!receivedMessage.startsWith("rfsh"))&&(receivedMessage.split("~>")[1] ==  light.mac) ){
                        println("success..")
                        var receivedmessage_decoded=extract_response(receivedMessage)
                        var  macip=receivedmessage_decoded[0]
                        var  pole_num=receivedmessage_decoded[1]
                        var  key_status=receivedmessage_decoded[2]
                        var ip =receivedmessage_decoded[3]
                        val light_database3=light_db.getInstance(context)
                        val same_macs = light_database3.getLightsByMacAddress(light.mac).sortedBy { it!!.sub_type!!.toInt() }

                        //                    var statusList=light_sub_type_decoder(key_status,pole_num)
                        //                    var index=0

                        is_succes = true

                    }else{
                        println("Broadcast failed..")
                        is_succes = false
                        //    UdpListener8089.resume()
                    }

                }


            }, 8089, 700, light.mac,light.sub_type,status, checkIP(context),broadcastIP)


        }

        loopCount++

        // تاخیر یک ثانیه قبل از ورود به دور جدید از حلقه
        Thread.sleep(700)
    } while (loopCount < 2) // تکرار حلقه تا دو بار اجرا

    //    UdpListener8089.resume()
    return is_succes
}

fun udp_light_scenario (context: Context, lights: List<Light>) : Boolean{
    //UdpListener8089.pause()
    var is_succes=false

    var isResponseReceivedFirstTime = false
    var pass = false


    val timeoutMillis = 1000
    var loopCount = 0

    do {


        if (loopCount == 0) {
            if (!isResponseReceivedFirstTime){

                val light_database=light_db.getInstance(context)
//                println(light.status)
                for (light in lights){
                    if(light.status=="on"){

                        light_database.updateStatusById(light.id, "on")

                    }else{

                        light_database.updateStatusById(light.id, "off")

                    }

                }

            }

            val light_database=light_db.getInstance(context)

//            send_to_light(lights[0].mac,lights[0].sub_type,light_sub_type_coder(context,lights[0].mac), checkIP(context),light_database.get_from_db_light(lights[0].id)!!.ip)
            println("direct IP sent")




            var a= receiveUdpMessage_send_inside_light_cmnd({ receivedMessage ->
                if (receivedMessage=="failed"){
                    println("direct IP failed..")
                    pass=true
                    is_succes = false

                }else{
                    pass=true
                    if ((!receivedMessage.startsWith("cmnd"))&&(!receivedMessage.startsWith("rfsh")) ){
                        is_succes = true

                        pass=true
                        println("direct IP success..")
                        isResponseReceivedFirstTime = true
                        val light_database2=light_db.getInstance(context)
                        var receivedmessage_decoded=extract_response(receivedMessage)
                        var  macip=receivedmessage_decoded[0]
                        var  pole_num=receivedmessage_decoded[1]
                        var  key_status=receivedmessage_decoded[2]
                        var ip =receivedmessage_decoded[3]

                        var statusList=light_sub_type_decoder(key_status,pole_num)

                        val light_database = light_db.getInstance(context)
                        val same_macs = light_database.getLightsByMacAddress(lights[0].mac).sortedBy { it!!.sub_type!!.toInt() }
                        var index=0
//                    for (light_same_mac in same_macs){
//                        light_database2.updateStatusById(light_same_mac!!.id,statusList[index])
//                        index+=1
//
//                    }

                    }



                }



            }, 8089, 700,lights[0].mac,lights[0].sub_type,light_sub_type_coder(context,lights[0].mac), checkIP(context),light_database.get_from_db_light(lights[0].id)!!.ip)






        }else if (loopCount == 1 && !isResponseReceivedFirstTime) {
            println("broadcasting..")
            val light_database=light_db.getInstance(context)
            val broadcastIP=convertIpToBroadcast(context)
//            send_to_light(lights[0].mac,lights[0].sub_type,light_sub_type_coder(context,lights[0].mac), checkIP(context),broadcastIP)
//            send_to_light(lights[0].mac,lights[0].sub_type,light_sub_type_coder(context,lights[0].mac), checkIP(context),broadcastIP)
//            send_to_light(lights[0].mac,lights[0].sub_type,light_sub_type_coder(context,lights[0].mac), checkIP(context),broadcastIP)
//            send_to_light(lights[0].mac,lights[0].sub_type,light_sub_type_coder(context,lights[0].mac), checkIP(context),broadcastIP)
            println("broadcast sent")
            var isrecived=false
            receiveUdpMessage_send_inside_light_cmnd_broadcast({ receivedMessage ->
                if (receivedMessage=="failed"){
                    println("broadcast failed..")
                    pass=true
//                    for (light in lights){
//                        if(light_database.getStatusById(light.id)=="on"){
//
//                            light_database.updateStatusById(light.id, "off")
//
//                        }else{
//                            light_database.updateStatusById(light.id, "on")
//
//                        }
//
//                    }
                    println("Broadcast failed..")
                    is_succes = false

                }else{
                    pass=true

                    if ((!receivedMessage.startsWith("cmnd"))&&(!receivedMessage.startsWith("rfsh")) ){
                        is_succes = true
                        println("success..")
                        var receivedmessage_decoded=extract_response(receivedMessage)
                        var  macip=receivedmessage_decoded[0]
                        var  pole_num=receivedmessage_decoded[1]
                        var  key_status=receivedmessage_decoded[2]
                        var ip =receivedmessage_decoded[3]
                        val light_database3=light_db.getInstance(context)
                        val same_macs = light_database3.getLightsByMacAddress(lights[0].mac).sortedBy { it!!.sub_type!!.toInt() }

//                    var statusList=light_sub_type_decoder(key_status,pole_num)
//                    var index=0
                        for (light_same_mac3 in same_macs){
                            light_database3.updateIpById(light_same_mac3!!.id , ip)
//                        light_database3.updateStatusById(light_same_mac3!!.id,statusList[index])
//                        index+=1
                        }

                    }else{
                        println("Broadcast failed..")
                        is_succes = false

                    }

                }


            }, 8089, 700,lights[0].mac,lights[0].sub_type,light_sub_type_coder(context,lights[0].mac), checkIP(context),broadcastIP)


        }

        if (!is_succes){

            loopCount++
        }else{
            break
        }

        // تاخیر یک ثانیه قبل از ورود به دور جدید از حلقه

        if (!pass){
          Thread.sleep(700)
          print("waiting..")
        }
    } while (loopCount < 2) // تکرار حلقه تا دو بار اجرا


    //    UdpListener8089.resume()
    return is_succes
}





//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++




fun refresh_light(context: Context, rooms: rooms?): Boolean {
    println("Pausing UDP listener")
//    //UdpListener8089.pause()
    var is_ok = false
    val startTime = System.currentTimeMillis()
    val light_database1 = light_db.getInstance(context)
    val light_list = light_database1.getLightsWithSameMacByRoomName(rooms!!.room_name)
    val mutex = Mutex()

    runBlocking {
        for (same_mac_list in light_list) {
            var isResponseReceived = false

            // ارسال مستقیم IP
//            sendDirectIP(context, same_mac_list)
            withTimeoutOrNull(700) {
                mutex.withLock {
                    println("direct IP sent")
                    val my_ip = checkIP(context)
                    var target_ip = light_database1.get_from_db_light(same_mac_list[0].id)!!.ip
                    if (target_ip!!.contains("~>")){
                        val splitedd = target_ip.split("~>")
                        target_ip=splitedd[splitedd.lastIndex]
                    }
                    try {
                        println("innnnnnnnnnnnn")

//                        send_refresh(same_mac_list[0].mac,checkIP(context),target_ip)
                    }catch (e:Exception){

                        println(e)
                    }
//                    send_refresh(same_mac_list[0].mac,checkIP(context),light_database1.get_from_db_light(same_mac_list[0].id)!!.ip)
//                    send_refresh(same_mac_list[0].mac,checkIP(context),light_database1.get_from_db_light(same_mac_list[0].id)!!.ip)
                    if (target_ip != null) {
                        same_mac_list[0].mac?.let {
                            receiveUdpMessage_send_inside({ receivedMessage ->
                                // بررسی و پردازش پیام دریافت شده
                                if (processReceivedMessage(receivedMessage, same_mac_list, light_database1) == true) {
                                    isResponseReceived = true
                                    is_ok = true
                                }
                            }, 8089, 700,target_ip, it,my_ip)
                        }
                    }
                }
            } ?: println("Timeout: No response from direct IP")

            // اگر پاسخی از IP مستقیم دریافت نشد، برودکست را آغاز کن
            if (!isResponseReceived) {
                println("No response from direct IP, starting broadcast")
                var a =0
                repeat(3) { loopCount ->
                    if (isResponseReceived) return@repeat
                    a++

                    withTimeoutOrNull(700) {
                        mutex.withLock {
                            val broadcastIP = convertIpToBroadcast(context)
                            println("Broadcast message sent")
//                            send_refresh(same_mac_list[0].mac, checkIP(context), broadcastIP)
//                            send_refresh(same_mac_list[0].mac, checkIP(context), broadcastIP)
//                            send_refresh(same_mac_list[0].mac, checkIP(context), broadcastIP)
//                            send_refresh(same_mac_list[0].mac, checkIP(context), broadcastIP)
//                            send_refresh(same_mac_list[0].mac, checkIP(context), broadcastIP)
                            same_mac_list[0].mac?.let {
                                receiveUdpMessage_send_inside_brodcast({ receivedMessage ->
                        //                                println("%" + same_mac_list[0].mac)
                        //                                println(receivedMessage)
                                    // بررسی و پردازش پیام دریافت شده از برودکست
                                    if (processReceivedMessage(receivedMessage, same_mac_list, light_database1) == true) {
                                        isResponseReceived = true
                                        is_ok = true
                                    }
                                }, 8089, (700).toLong(),broadcastIP, it,checkIP(context))
                            }
                        }
                    } ?: println("Timeout on broadcast attempt $loopCount")

                    if (isResponseReceived) {
                        println("Response received from broadcast, breaking loop")
                        return@repeat
                    }
                }
            }

            if (isResponseReceived) {
                println("Response received, moving to next light")
            } else {
                println("No response received after all attempts for current light")
            }
        }
    }

    val endTime = System.currentTimeMillis()
    val executionTime = endTime - startTime
    println("Execution time: $executionTime milliseconds")
    println("Resuming UDP listener")
//    //    UdpListener8089.resume()
    return is_ok
}

private fun sendDirectIP(context: Context, same_mac_list: List<Light>) {
    try {
        // ارسال مستقیم به IP
        println("Sending direct IP message")
        // پیاده‌سازی ارسال مستقیم IP
    } catch (e: Exception) {
        println("Error in sending direct IP: $e")
    }
}

private fun sendBroadcast(context: Context, same_mac_list: List<Light>) {
    // ارسال به صورت broadcast

}

private fun processReceivedMessage(
    receivedMessage: String, same_mac_list: List<Light>, light_database: light_db
): Boolean? {
    return if (receivedMessage == "failed") {
        println("Message indicates failure")
        false
    } else if (!receivedMessage.startsWith("cmnd") && !receivedMessage.startsWith("rfsh")) {
        println("Processing successful message")
        val receivedmessage_decoded = extract_response(receivedMessage)
        val macip = receivedmessage_decoded[0]
        val pole_num = receivedmessage_decoded[1]
        val key_status = receivedmessage_decoded[2]
        val ip = receivedmessage_decoded[3]
        println(ip)

        val pole_num_f = light_database.getLightsByMacAddress(macip)
            .sortedBy { it!!.sub_type?.toInt() }
            .let { lights ->
                lights[lights.count() - 1]!!.sub_type.toString().let {
                    if (lights[0]!!.sub_type!!.toInt() == 0) (it.toInt() + 1).toString() else it
                }
            }

        // دیکد کردن وضعیت‌ها و به‌روزرسانی دیتابیس
        val statusList = light_sub_type_decoder(key_status, pole_num_f)
        updateLightStatus(light_database, same_mac_list, statusList, ip)
        true
    } else {
        println("Invalid message format received")
        false
    }
}

private fun updateLightStatus(
    light_database: light_db, same_macs: List<Light>, statusList: List<String>, ip: String
) {

//    println(statusList.getOrElse(index))
//    println(statusList.getOrElse(index))
    // به‌روزرسانی وضعیت لایت‌ها در دیتابیس
    val sortedMacs = light_database.getLightsByMacAddress(same_macs[0].mac)
        .sortedBy { it!!.sub_type!!.toInt() }
    sortedMacs.forEachIndexed { index, lightt ->
        light_database.updateStatusAndIpById(lightt!!.id, statusList.getOrElse(index) { "unknown" }, ip)
//        println(statusList.getOrElse(index))
    }
    println("Light status updated successfully")
}



//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++



//fun refreshLight(context: Context, rooms: rooms?): Boolean {
//
////    val viewModelProvider = ViewModelProvider(context as ViewModelStoreOwner)
////
////    // بدست آوردن یک instance از SharedViewModel از ViewModelProvider
////    val SharedViewModel = viewModelProvider.get(SharedViewModel::class.java)
////    val handler = Handler(Looper.getMainLooper())
//
//
//    val learn_light_db= light_db.getInstance(context)
//    var isOk = false
//    val timeoutMillis = 1000
//    val light_database = light_db.getInstance(context)
//    val lightList = light_database.getLightsWithSameMacByRoomName(rooms?.room_name)
//
//    for (same_mac_list in lightList) {
//        val startTime = System.currentTimeMillis()
//        var isResponseReceivedFirstTime = false
//
//        repeat(2) { loopCount ->
//            var pass = false
//
//            if (loopCount == 0) {
//                try {
//                    send_refresh(same_mac_list[0].mac,checkIP(context),light_database.get_from_db_light(same_mac_list[0].id)!!.ip)
//                    println("Direct IP sent")
//                } catch (e: Exception) {
//                    println(e)
//                }
//
//                receiveUdpMessage({ receivedMessage ->
//                    if (receivedMessage == "failed") {
//                        println("Direct IP failed..")
//                        pass = true
//                    } else {
//                        if ((!receivedMessage.startsWith("cmnd")) && (!receivedMessage.startsWith("rfsh"))) {
//                            isOk = true
//                            println("Direct IP success..")
//                            isResponseReceivedFirstTime = true
//                            val receivedMessageDecoded = extract_response(receivedMessage)
//                            val macIp = receivedMessageDecoded[0]
//                            val poleNum = receivedMessageDecoded[1]
//                            val keyStatus = receivedMessageDecoded[2]
//                            val ip = receivedMessageDecoded[3]
//                            val statusList = light_sub_type_decoder(keyStatus, light_database.getLightsByMacAddress(macIp).last()?.sub_type.toString())
//
//                            val sameMacs = light_database.getLightsByMacAddress(same_mac_list[0].mac).sortedBy { it?.sub_type?.toInt() }
//                            var index = 0
//                            for (light in sameMacs) {
//                                light_database.updateStatusById(light?.id, statusList[index])
//                                index++
//                            }
//                        }
//                    }
//                }, 8089, 700)
//            } else if (loopCount == 1 && !isResponseReceivedFirstTime) {
//                println("Broadcasting..")
//                val broadcastIP = convertIpToBroadcast(context)
//                send_refresh(same_mac_list[0].mac,checkIP(context),broadcastIP)
//                println("Broadcast sent")
//                var isReceived = false
//
//                receiveUdpMessage({ receivedMessage ->
//                    if (receivedMessage == "failed") {
//                        println("Broadcast failed..")
//                        pass = true
//                    } else {
//                        pass = true
//                        if ((!receivedMessage.startsWith("cmnd")) && (!receivedMessage.startsWith("rfsh"))) {
//                            isOk = true
//                            println("Success..")
//                            val receivedMessageDecoded = extract_response(receivedMessage)
//                            val macIp = receivedMessageDecoded[0]
//                            val poleNum = receivedMessageDecoded[1]
//                            val keyStatus = receivedMessageDecoded[2]
//                            val ip = receivedMessageDecoded[3]
//
//                            val sameMacs = light_database.getLightsByMacAddress(same_mac_list[0].mac).sortedBy { it?.sub_type?.toInt() }
//                            val statusList = light_sub_type_decoder(keyStatus, light_database.getLightsByMacAddress(macIp).last()?.sub_type.toString())
//                            var index = 0
//                            for (light in sameMacs) {
//                                light_database.updateStatusAndIpById(light?.id, statusList[index], ip)
//                                index++
//                            }
//                        } else {
//                            println("Failed..")
//                        }
//                    }
//                }, 8089, 700)
//            }
//
//            // Delay 700 milliseconds before entering the next iteration of the loop
//            if (!pass) {
//                Thread.sleep(700)
//            }
//        }
////        handler.post {
////            SharedViewModel.update_light_to_learn_list( learn_light_db.getAllLightsByRoomName(rooms!!.room_name))
////
////        }
//        val endTime = System.currentTimeMillis()
//        val executionTime = endTime - startTime
//        println("زمان اجرا: $executionTime میلی‌ثانیه")
//    }
//
//    return isOk
//}



fun refresh_light_2(context:Context,lights:List<Light>):Boolean{
    //UdpListener8089.pause()
    var is_ok=false

    val timeoutMillis = 1000


    val light_database1=light_db.getInstance(context)
    val light_list=lights

    for (same_mac_list in light_list){
        var isResponseReceivedFirstTime = false
        var loopCount = 0
//        println( light_list)


        do {
            var pass = false

            if (loopCount == 0) {

                val light_database=light_db.getInstance(context)
                try {
                    send_refresh(same_mac_list.mac,checkIP(context),light_database.get_from_db_light(same_mac_list.id)!!.ip)
                    println("direct IP sent")

                }catch (e:Exception){
                    println(e)
                }





                same_mac_list.mac?.let {
                    receiveUdpMessage_answer_analyze({ receivedMessage ->
                        if (receivedMessage=="failed"){
                            println("direct IP failed..")
                            pass = true

                        }else{
                            if ((!receivedMessage.startsWith("cmnd"))&&(!receivedMessage.startsWith("rfsh")) ){
                                is_ok=true
                                println("direct IP success..")
                                isResponseReceivedFirstTime = true
                                val light_database2=light_db.getInstance(context)
                                var receivedmessage_decoded=extract_response(receivedMessage)
                                println(receivedmessage_decoded)
                                var  macip=receivedmessage_decoded[0]
                                var  pole_num=receivedmessage_decoded[1]
                                var  key_status=receivedmessage_decoded[2]
                                var ip =receivedmessage_decoded[3]
                                var pole_num_f = light_database.getLightsByMacAddress(macip).sortedBy { it!!.sub_type?.toInt() }[light_database.getLightsByMacAddress(macip).count()-1]!!.sub_type.toString()

                                var statusList:MutableList<String>
                                if (light_database.getLightsByMacAddress(macip).sortedBy { it!!.sub_type?.toInt() }[0]!!.sub_type!!.toInt() == 0){
                                    println(pole_num_f)

                                    pole_num_f=(pole_num_f.toInt()+1).toString()
                                    println(pole_num_f)
                                    statusList=light_sub_type_decoder(key_status,pole_num_f)

                                }else{
                                    statusList=light_sub_type_decoder(key_status,pole_num_f)

                                }
                                val light_database = light_db.getInstance(context)
                                val same_macs = light_database.getLightsByMacAddress(same_mac_list.mac).sortedBy { it!!.sub_type!!.toInt() }
                                var index=0
                                println(same_macs.count())
                                for (lightt in same_macs){
                                    println("uuuuuuuuuuuuuuuuupppppppppppppppppppppppppp1p")

                                    light_database.updateStatusById(lightt!!.id,statusList[0+index])
                                    index+=1

                                }
                            }


                        }


                    }, 8089, 700, it)
                }






            }else if (loopCount == 1 && !isResponseReceivedFirstTime) {
                println("broadcasting..")
                val light_database=light_db.getInstance(context)
                val broadcastIP=convertIpToBroadcast(context)
                send_refresh(same_mac_list.mac,checkIP(context),broadcastIP)
                println("broadcast sent")
                var isrecived=false
                receiveUdpMessage({ receivedMessage ->
                    if (receivedMessage=="failed"){
                        println("broadcast failed..")
                        pass = true



                    }else{
                        pass = true

                        if ((!receivedMessage.startsWith("cmnd"))&&(!receivedMessage.startsWith("rfsh")) ){
                            is_ok=true
                            println("success..")
                            var receivedmessage_decoded=extract_response(receivedMessage)
                            var  macip=receivedmessage_decoded[0]
                            var  pole_num=receivedmessage_decoded[1]
                            var  key_status=receivedmessage_decoded[2]
                            var ip =receivedmessage_decoded[3]
                            val light_database3=light_db.getInstance(context)
                            val same_macs = light_database3.getLightsByMacAddress(same_mac_list.mac).sortedBy { it!!.sub_type!!.toInt() }
                            var pole_num_f = light_database.getLightsByMacAddress(macip).sortedBy { it!!.sub_type?.toInt() }[light_database.getLightsByMacAddress(macip).count()-1]!!.sub_type.toString()

                            var statusList:MutableList<String>
                            if (light_database.getLightsByMacAddress(macip).sortedBy { it!!.sub_type?.toInt() }[0]!!.sub_type!!.toInt() == 0){
                                println(pole_num_f)

                                pole_num_f=(pole_num_f.toInt()+1).toString()
                                println(pole_num_f)
                                statusList=light_sub_type_decoder(key_status,pole_num_f)

                            }else{
                                statusList=light_sub_type_decoder(key_status,pole_num_f)

                            }
                            var index=0
                            for (lightt in same_macs){
                                light_database.updateStatusAndIpById(lightt!!.id,statusList[0+index],ip)
                                index+=1

                            }



                        }else{
                            println("failed..")
                        }


                    }


                }, 8089, 700)


            }

            if (!is_ok){

                loopCount++
            }else{
                break
            }

            // تاخیر یک ثانیه قبل از ورود به دور جدید از حلقه
            if (!pass ){
                Thread.sleep(700)

            }
        } while (loopCount < 2) // تکرار حلقه تا دو بار اجرا


    }

    //    UdpListener8089.resume()
    return is_ok









}


fun refresh_light_for_scenario(context:Context,light_list: List<Light>) :ArrayList<String>{
    //UdpListener8089.pause()
    val ok_to_send= arrayListOf<String>()

    val timeoutMillis = 1000



    val light_list=light_list

    for (same_mac_list in light_list){
        var pass = false
        var isResponseReceivedFirstTime = false
        var loopCount = 0
//        println( light_list)


        do {


            if (loopCount == 0) {

                val light_database=light_db.getInstance(context)

                send_refresh(same_mac_list.mac,checkIP(context),light_database.get_from_db_light(same_mac_list.id)!!.ip)
                println("direct IP sent")




                receiveUdpMessage({ receivedMessage ->
                    if (receivedMessage=="failed"){
                        println("direct IP failed..")
                        pass = true

                    }else{
                        pass = true
                        if ((!receivedMessage.startsWith("cmnd"))&&(!receivedMessage.startsWith("rfsh")) ){
                            same_mac_list.mac?.let { ok_to_send.add(it) }
                            println("direct IP success..")
                            isResponseReceivedFirstTime = true
                            val light_database2=light_db.getInstance(context)
                            var receivedmessage_decoded=extract_response(receivedMessage)
                            var  macip=receivedmessage_decoded[0]
                            var  pole_num=receivedmessage_decoded[1]
                            var  key_status=receivedmessage_decoded[2]
                            var ip =receivedmessage_decoded[3]

                            var pole_num_f = light_database.getLightsByMacAddress(macip).sortedBy { it!!.sub_type?.toInt() }[light_database.getLightsByMacAddress(macip).count()-1]!!.sub_type.toString()

                            var statusList:MutableList<String>
                            if (light_database.getLightsByMacAddress(macip).sortedBy { it!!.sub_type?.toInt() }[0]!!.sub_type!!.toInt() == 0){
                                println(pole_num_f)

                                pole_num_f=(pole_num_f.toInt()+1).toString()
                                println(pole_num_f)
                                statusList=light_sub_type_decoder(key_status,pole_num_f)

                            }else{
                                statusList=light_sub_type_decoder(key_status,pole_num_f)

                            }
                            val light_database = light_db.getInstance(context)
                            val same_macs = light_database.getLightsByMacAddress(same_mac_list.mac).sortedBy { it!!.sub_type!!.toInt() }
                            var index=0
                            for (lightt in same_macs){
                                light_database.updateStatusById(lightt!!.id,statusList[0+index])
                                index+=1

                            }
                        }




                    }



                }, 8089, 700)






            }else if (loopCount == 1 && !isResponseReceivedFirstTime) {
                println("broadcasting..")
                val light_database=light_db.getInstance(context)
                val broadcastIP=convertIpToBroadcast(context)
                send_refresh(same_mac_list.mac,checkIP(context),broadcastIP)
                println("broadcast sent")
                var isrecived=false
                receiveUdpMessage({ receivedMessage ->
                    if (receivedMessage=="failed"){
                        pass = true
                        println("broadcast failed..")



                    }else{
                        pass = true

                        if ((!receivedMessage.startsWith("cmnd"))&&(!receivedMessage.startsWith("rfsh")) ){
                            same_mac_list.mac?.let { ok_to_send.add(it) }
                            println("success..")
                            var receivedmessage_decoded=extract_response(receivedMessage)
                            var  macip=receivedmessage_decoded[0]
                            var  pole_num=receivedmessage_decoded[1]
                            var  key_status=receivedmessage_decoded[2]
                            var ip =receivedmessage_decoded[3]
                            val light_database3=light_db.getInstance(context)
                            val same_macs = light_database3.getLightsByMacAddress(same_mac_list.mac).sortedBy { it!!.sub_type!!.toInt() }
                            var pole_num_f = light_database.getLightsByMacAddress(macip).sortedBy { it!!.sub_type?.toInt() }[light_database.getLightsByMacAddress(macip).count()-1]!!.sub_type.toString()

                            var statusList:MutableList<String>
                            if (light_database.getLightsByMacAddress(macip).sortedBy { it!!.sub_type?.toInt() }[0]!!.sub_type!!.toInt() == 0){
                                println(pole_num_f)

                                pole_num_f=(pole_num_f.toInt()+1).toString()
                                println(pole_num_f)
                                statusList=light_sub_type_decoder(key_status,pole_num_f)

                            }else{
                                statusList=light_sub_type_decoder(key_status,pole_num_f)

                            }
                            var index=0
                            for (lightt in same_macs){
                                light_database.updateStatusAndIpById(lightt!!.id,statusList[0+index],ip)
                                index+=1

                            }



                        }else{
                            println("failed..")
                        }


                    }


                }, 8089, 700)


            }

            loopCount++

            // تاخیر یک ثانیه قبل از ورود به دور جدید از حلقه
            if (!pass){
                Thread.sleep(700)

            }
        } while (loopCount < 2) // تکرار حلقه تا دو بار اجرا


    }



    //    UdpListener8089.resume()
    return ok_to_send


}


fun all_same_mac_on_on_test (context: Fragment, light: Light){
    //UdpListener8089.pause()
    var isResponseReceivedFirstTime = false


    val timeoutMillis = 1000
    var loopCount = 0

    do {


        if (loopCount == 0) {
            if (!isResponseReceivedFirstTime){
                val light_database=light_db.getInstance(context.requireContext())

                val same_macs = light_database.getLightsByMacAddress(light.mac).sortedBy { light.sub_type!!.toInt() }
                for (lightt in same_macs){

                    if(light_database.getStatusById(lightt!!.id)=="on"){

                        light_database.updateStatusById(lightt.id, "off")

                    }else{
                        light_database.updateStatusById(lightt.id, "on")

                    }
                }
            }

            val light_database=light_db.getInstance(context.requireContext())
            send_to_light(light.mac,light.sub_type,light_sub_type_coder(context.requireContext(),light.mac), checkIP(context.requireContext()),light_database.get_from_db_light(light.id)!!.ip)
            println("direct IP sent")




            receiveUdpMessage({ receivedMessage ->
                if (receivedMessage=="failed"){
                    println("direct IP failed..")

                }else{
                     if ((!receivedMessage.startsWith("cmnd"))&&(!receivedMessage.startsWith("rfsh")) ){

                        println("direct IP success..")

                        val light_database2=light_db.getInstance(context.requireContext())
                        var receivedmessage_decoded=extract_response(receivedMessage)
                        var  macip=receivedmessage_decoded[0]
                        var  pole_num=receivedmessage_decoded[1]
                        var  key_status=receivedmessage_decoded[2]
                        var ip =receivedmessage_decoded[3]

                        var statusList=light_sub_type_decoder(key_status,pole_num)

//                    val light_database = light_db.getInstance(context.requireContext())
//                    val same_macs = light_database.getLightsByMacAddress(light.mac).sortedBy { it!!.sub_type!!.toInt() }
//                    var index=0
//                    println(same_macs[0])
//                    for (light_same_mac in same_macs){
//                        light_database2.updateStatusById(light_same_mac!!.id,statusList[index])
//                        println(light_same_mac.Lname)
//                        index+=1
//
//                    }
                        isResponseReceivedFirstTime = true

                    }


                }


            }, 8089, 700)






        }else if (loopCount == 1 && !isResponseReceivedFirstTime) {
            println("broadcasting..")
            val light_database=light_db.getInstance(context.requireContext())
            val broadcastIP=convertIpToBroadcast(context.requireContext())
            send_to_light(light.mac,light.sub_type,light_sub_type_coder(context.requireContext(),light.mac), checkIP(context.requireContext()),broadcastIP)
            println("broadcast sent")
            var isrecived=false
            receiveUdpMessage({ receivedMessage ->
                if (receivedMessage=="failed"){
                    println("broadcast failed..")
                    val same_macs = light_database.getLightsByMacAddress(light.mac).sortedBy { light.sub_type!!.toInt() }
                    for (lightt in same_macs){

                        if(light_database.getStatusById(lightt!!.id)=="on"){

                            light_database.updateStatusById(lightt.id, "off")

                        }else{
                            light_database.updateStatusById(lightt.id, "on")

                        }
                    }
                    println("Broadcast failed..")

                }else{
                     if ((!receivedMessage.startsWith("cmnd"))&&(!receivedMessage.startsWith("rfsh")) ){
                        println("success..")
                        var receivedmessage_decoded=extract_response(receivedMessage)
                        var  macip=receivedmessage_decoded[0]
                        var  pole_num=receivedmessage_decoded[1]
                        var  key_status=receivedmessage_decoded[2]
                        var ip =receivedmessage_decoded[3]
                        val light_database3=light_db.getInstance(context.requireContext())

                        val same_macs = light_database3.getLightsByMacAddress(light.mac).sortedBy { it!!.sub_type!!.toInt() }

                        var statusList=light_sub_type_decoder(key_status,pole_num)
//                    var index=0
                        for (light_same_mac3 in same_macs){
                            light_database3.updateIpById(light_same_mac3!!.id , ip)
//                        light_database3.updateStatusById(light_same_mac3!!.id,statusList[index])
//                        index+=1
                        }

                    }else{
                        println("Broadcast failed..")
                    }

                }


            }, 8089, 700)


        }

        loopCount++

        // تاخیر یک ثانیه قبل از ورود به دور جدید از حلقه
        Thread.sleep(700)
    } while (loopCount < 2) // تکرار حلقه تا دو بار اجرا
    //    UdpListener8089.resume()

}



fun light_sub_type_decoder(status: String?, pone_num: String): MutableList<String> {

    var cuted_subtype = status!!.substring(0, pone_num.toInt())
    var status_list = mutableListOf<String>()
//    println(cuted_subtype)
    for (statusChar in cuted_subtype) {
        val status = statusChar.toString()
//        println(statusChar)

        if (status == "0") {
            status_list.add("off")
        } else if (status == "1") {
            status_list.add("on")
        }
    }
    return status_list
}

fun light_sub_type_coder(context: Context, mac: String?): String {
    val light_database = light_db.getInstance(context)
    val same_macs = mac?.let { light_database.getLightsByMacAddress(it) }
    var key_status = "0000000000000000"

    if (same_macs != null) {

        var pole_count=0
        for (light in same_macs) {
            var pole = light?.sub_type?.toInt() ?: 0


            if (same_macs.sortedBy { it!!.sub_type?.toInt() }[0]!!.sub_type!!.toInt() == 0 ){
                pole+=1
            }

            if (light?.status == "on") {
                println("on")
                if (key_status.length >= pole) {
                    val startIndex = pole-1
                    val endIndex = startIndex + 1
                    val newChar = "1"
                    key_status = key_status.substring(0, startIndex) + newChar + key_status.substring(endIndex)
                }
            }else if (light?.status == "off") {
                println("off")
                if (key_status.length >= pole) {
                    val startIndex = pole-1
                    val endIndex = startIndex + 1
                    val newChar = "0"
                    key_status = key_status.substring(0, startIndex) + newChar + key_status.substring(endIndex)
                }
            }
            print("stage3")
            if (pole>pole_count ){
                if (light != null) {
                    pole_count=light.sub_type!!.toInt()
                }
                if (same_macs.sortedBy { it!!.sub_type?.toInt() }[0]!!.sub_type!!.toInt() == 0 ){
                    pole_count+=1
                }

            }

        }
        println(pole_count)
        println(key_status)
        key_status="${key_status.substring(0,pole_count)}${key_status.substring(pole_count).replace("0","-")}"

    }
    println(key_status)
    return key_status
}
fun light_sub_type_coder2(context: Context, mac: String?,): String {
    val light_database = light_db.getInstance(context)
    val same_macs = mac?.let { light_database.getLightsByMacAddress(it) }
    var key_status = "0000000000000000"

    if (same_macs != null) {
        var pole_count=0
        for (light in same_macs) {
            val pole = light?.sub_type?.toInt() ?: 0
            if (light?.status == "on") {
                println("on")
                if (key_status.length >= pole) {
                    val startIndex = pole-1
                    val endIndex = startIndex + 1
                    val newChar = "1"
                    key_status = key_status.substring(0, startIndex) + newChar + key_status.substring(endIndex)
                }
            }else if (light?.status == "off") {
                println("off")
                if (key_status.length >= pole) {
                    val startIndex = pole-1
                    val endIndex = startIndex + 1
                    val newChar = "0"
                    key_status = key_status.substring(0, startIndex) + newChar + key_status.substring(endIndex)
                }
            }
            if (light!!.sub_type!!.toInt()>pole_count ){
                pole_count=light.sub_type!!.toInt()

            }

        }
        println(pole_count)
        println(key_status)
        key_status="${key_status.substring(0,pole_count)}${key_status.substring(pole_count).replace("0","-")}"

    }
    println(key_status)
    return key_status
}

//################################################  Thermostat ################################################
fun thermostat_subtype_decoder(subtype: String?):MutableList<String> {
    val subtypelist= mutableListOf<String>()
    val on_off=subtype!!.substring(0,1)
    val local_temp=subtype!!.substring(1,3)
    val seted_temp=subtype!!.substring(3,5)
    val fan_status=subtype!!.substring(5,6)
    val mood=subtype!!.substring(6,7)
    subtypelist.add(on_off)
    subtypelist.add(local_temp)
    subtypelist.add(seted_temp)
    subtypelist.add(fan_status)
    subtypelist.add(mood)
//    val local_temp=subtype!!.substring(1,)
//    val on_off=subtype!!.substring(0,2)
    return subtypelist

}


fun send_modem_setting(ssid:String?,pass:String?,myip:String?){
//    //UdpListener8089.pause()
    var defalt_ip="192.168.4.1"
//    var defalt_ip="192.168.103.255"
//    var defalt_ip2="192.168.0.15"
//    var second_ip="192.168.0.7"

    val message = "ssid:$ssid~>pswd:$pass~>$myip"
    val serverAddress = InetAddress.getByName(defalt_ip)
    val serverPort = 8089

    val socket = DatagramSocket()
    val sendData = message.toByteArray()
    val sendPacket = DatagramPacket(sendData, sendData.size, serverAddress, serverPort)
    socket.send(sendPacket)
    socket.close()
//    //    UdpListener8089.resume()

}

fun send_ir_cmd(protocol:String?,data:String?,mac:String?,nbit:String?,repeat:String?,ip:String?){
//    //UdpListener8089.pause()


    val message = "cmnd~>$mac~>Irce~>0000~>$protocol~>$data~>$nbit~>$repeat"
    val serverAddress = InetAddress.getByName(ip)
    val serverPort = 8089

    val socket = DatagramSocket()
    val sendData = message.toByteArray()
    val sendPacket = DatagramPacket(sendData, sendData.size, serverAddress, serverPort)
    socket.send(sendPacket)
    socket.close()
//    //    UdpListener8089.resume()

}

fun send_modem_setting_old1(ssid:String?){
//    //UdpListener8089.pause()
    var defalt_ip="192.168.4.1"
//    var defalt_ip2="192.168.0.15"
//    var second_ip="192.168.0.7"

    val message = "ssid~>$ssid"
    println(message)
    val serverAddress = InetAddress.getByName(defalt_ip)
    val serverPort = 8089

    val socket = DatagramSocket()
    val sendData = message.toByteArray()
    val sendPacket = DatagramPacket(sendData, sendData.size, serverAddress, serverPort)
    socket.send(sendPacket)
    socket.close()
//    //    UdpListener8089.resume()











}
fun send_modem_setting_old2(pass:String?){
//    //UdpListener8089.pause()
    var defalt_ip="192.168.4.1"
//    var defalt_ip2="192.168.0.15"
//    var second_ip="192.168.0.7"

    val message = "pswd~>$pass"
    println(message)
    val serverAddress = InetAddress.getByName(defalt_ip)
    val serverPort = 8089

    val socket = DatagramSocket()
    val sendData = message.toByteArray()
    val sendPacket = DatagramPacket(sendData, sendData.size, serverAddress, serverPort)
    socket.send(sendPacket)

    socket.close()
//    //    UdpListener8089.resume()




}


fun send_to_light(mac:String?,subtype:String?,status:String?,myip:String,ip:String?){
//    //UdpListener8089.pause()
    var targetIP=ip

    val message = "cmnd~>$mac~>Lght~>$subtype~>$status~>0000~>$myip"
    val serverAddress = InetAddress.getByName(targetIP)
    val serverPort = 8089

    val socket = DatagramSocket()
    val sendData = message.toByteArray()
    val sendPacket = DatagramPacket(sendData, sendData.size, serverAddress, serverPort)
    socket.send(sendPacket)
    socket.close()
//    //    UdpListener8089.resume()
}
fun send_to_six_worker(mac:String?,pole:String?,subtype:String?,type:String?,status:String?,device_ip:String?,myip:String,ip:String?,target_mac:String?){
//    //UdpListener8089.pause()
    var targetIP=ip

    println(targetIP)
    val message = "duty~>$target_mac~>$pole~>sixcomnd~>$mac~>$type~>$subtype~>$status~>$device_ip~>$myip"
//    val message = "duty~>$pole~>sixcomnd~>$mac~>$type~>$subtype~>$status~>$device_ip~>$myip"
    val serverAddress = InetAddress.getByName(targetIP)
    val serverPort = 8089

    val socket = DatagramSocket()
    val sendData = message.toByteArray()
    val sendPacket = DatagramPacket(sendData, sendData.size, serverAddress, serverPort)
    socket.send(sendPacket)
    socket.close()
//    //    UdpListener8089.resume()
}
fun send_to_six_worker_scenario(mac:String ,pole:String?,myip:String,ip:String?){
//    //UdpListener8089.pause()
    var targetIP=ip

    println(targetIP)
    val message = "duty~>$mac~>0$pole~>scenario~>$myip"
    val serverAddress = InetAddress.getByName(targetIP)
    val serverPort = 8089

    val socket = DatagramSocket()
    val sendData = message.toByteArray()
    val sendPacket = DatagramPacket(sendData, sendData.size, serverAddress, serverPort)
    socket.send(sendPacket)
    socket.close()
//    //    UdpListener8089.resume()
}
fun send_to_thermostat(mac:String?,mood:String?,temperature:String?,fan_status:String?,on_off:String?,myip: String?,ip:String?){
    var targetIP=ip



    var t="25"


    val status= "$on_off$t$temperature$fan_status$mood---------"

    val message = "cmnd~>$mac~>Tmpr~>0000~>$status~>0000~>$myip"
    println("sent message : $message")
    val serverAddress = InetAddress.getByName(targetIP)
    val serverPort = 8089

    val socket = DatagramSocket()
    val sendData = message.toByteArray()
    val sendPacket = DatagramPacket(sendData, sendData.size, serverAddress, serverPort)
    socket.send(sendPacket)
    socket.close()


}
fun send_to_thermostat_sms(mac:String?,status: String?,myip: String?,ip:String?){
    var targetIP=ip



    var t="25"




    val message = "cmnd~>$mac~>Tmpr~>0000~>$status~>0000~>$myip"
    println("sent message : $message")
    val serverAddress = InetAddress.getByName(targetIP)
    val serverPort = 8089

    val socket = DatagramSocket()
    val sendData = message.toByteArray()
    val sendPacket = DatagramPacket(sendData, sendData.size, serverAddress, serverPort)
    socket.send(sendPacket)
    socket.close()


}


//fun send_refresh(mac:String?,myip: String?,ip:String?){
////    val status= "$on_off$t$temperature$fan_status$mood---------"
//    val message = "rfsh~>$mac~>$myip"
//    val serverAddress = InetAddress.getByName(ip)
//    val serverPort = 8089
//
//    val socket = DatagramSocket()
//    val sendData = message.toByteArray()
//    val sendPacket = DatagramPacket(sendData, sendData.size, serverAddress, serverPort)
//    socket.send(sendPacket)
//    socket.close()
//}

fun send_refresh22(mac: String?, myip: String?, ip: String?,num:String) {

//    val message = "rfsh~>$mac~>0000~>$myip"
    val message = "rfsh~>$mac~>$num~>$myip"

    DatagramSocket().use { socket ->
        val serverAddress = InetAddress.getByName(ip)
        val serverPort = 8089

        val sendData = message.toByteArray()
        val sendPacket = DatagramPacket(sendData, sendData.size, serverAddress, serverPort)

        socket.send(sendPacket)
        socket.close()
    }

}

fun send_refresh(mac: String?, myip: String?, ip: String?) {

    val message = "rfsh~>$mac~>0000~>$myip"


    DatagramSocket().use { socket ->
        try {
            val serverAddress = InetAddress.getByName(ip)
            val serverPort = 8089

            val sendData = message.toByteArray()
            val sendPacket = DatagramPacket(sendData, sendData.size, serverAddress, serverPort)

            socket.send(sendPacket)
            socket.close()
        }catch (e:Exception){

            socket.close()
        }

    }

}

fun get_mac_light( context:Context,my_ip:String) {
    //UdpListener8089.pause()
    val broadcastIP=convertIpToBroadcast(context)
    val message = "gtmc~>Lght~>0000~>$my_ip"

    DatagramSocket().use { socket ->
        val serverAddress = InetAddress.getByName(broadcastIP)
        val serverPort = 8089

        val sendData = message.toByteArray()
        val sendPacket = DatagramPacket(sendData, sendData.size, serverAddress, serverPort)

        socket.send(sendPacket)
        socket.close()
    }
    //    UdpListener8089.resume()
}

fun get_mac_plug( context:Context,my_ip:String) {
    //UdpListener8089.pause()
    val broadcastIP=convertIpToBroadcast(context)
    val message = "gtmc~>Plug~>0000~>$my_ip"

    DatagramSocket().use { socket ->
        val serverAddress = InetAddress.getByName(broadcastIP)
        val serverPort = 8089

        val sendData = message.toByteArray()
        val sendPacket = DatagramPacket(sendData, sendData.size, serverAddress, serverPort)

        socket.send(sendPacket)
        socket.close()
    }
    //    UdpListener8089.resume()
}
fun get_mac_curtain( context:Context,my_ip:String) {
    //UdpListener8089.pause()
    val broadcastIP=convertIpToBroadcast(context)
    val message = "gtmc~>Crtn~>0000~>$my_ip"

    DatagramSocket().use { socket ->
        val serverAddress = InetAddress.getByName(broadcastIP)
        val serverPort = 8089

        val sendData = message.toByteArray()
        val sendPacket = DatagramPacket(sendData, sendData.size, serverAddress, serverPort)

        socket.send(sendPacket)
        socket.close()
    }
    //    UdpListener8089.resume()
}

fun get_mac_sixc( context:Context,my_ip:String) {
    //UdpListener8089.pause()
    val broadcastIP=convertIpToBroadcast(context)
    val message = "gtmc~>SixC~>0000~>$my_ip"

    DatagramSocket().use { socket ->
        val serverAddress = InetAddress.getByName(broadcastIP)
        val serverPort = 8089

        val sendData = message.toByteArray()
        val sendPacket = DatagramPacket(sendData, sendData.size, serverAddress, serverPort)

        socket.send(sendPacket)
        socket.close()
    }
    //    UdpListener8089.resume()
}

fun get_mac_valv( context:Context,my_ip:String) {
    //UdpListener8089.pause()
    val broadcastIP=convertIpToBroadcast(context)
    val message = "gtmc~>ElVa~>0000~>$my_ip"

    DatagramSocket().use { socket ->
        val serverAddress = InetAddress.getByName(broadcastIP)
        val serverPort = 8089

        val sendData = message.toByteArray()
        val sendPacket = DatagramPacket(sendData, sendData.size, serverAddress, serverPort)

        socket.send(sendPacket)
        socket.close()
    }
    //    UdpListener8089.resume()
}
fun get_mac_temp( context:Context,my_ip:String) {
    //UdpListener8089.pause()
    val broadcastIP=convertIpToBroadcast(context)
    val message = "gtmc~>Tmpr~>0000~>$my_ip"

    DatagramSocket().use { socket ->
        val serverAddress = InetAddress.getByName(broadcastIP)
        val serverPort = 8089

        val sendData = message.toByteArray()
        val sendPacket = DatagramPacket(sendData, sendData.size, serverAddress, serverPort)

        socket.send(sendPacket)
        socket.close()
    }
    //    UdpListener8089.resume()
}



fun refresh_thermostat(context: Fragment, TThermostst: Thermostst?):Boolean{
    //UdpListener8089.pause()
    var is_ok=false
    var isResponseReceivedFirstTime = false
    var pass = false

    val timeoutMillis = 1000
    var loopCount = 0

    do {


        if (loopCount == 0) {



//            send_refresh(TThermostst!!.mac, checkIP(context.requireContext()),TThermostst!!.ip)
//            send_refresh(TThermostst!!.mac, checkIP(context.requireContext()),TThermostst!!.ip)


//            println("direct IP sent")




            if (TThermostst != null) {
                TThermostst.mac?.let {
                    TThermostst.ip?.let { it1 ->
                        receiveUdpMessage_send_inside({ receivedMessage ->
                            if (receivedMessage=="failed"){
                                println("direct IP failed..")
                                pass = true
                                is_ok=false

                            }else{
                                pass = true
                                if ((!receivedMessage.startsWith("cmnd"))&&(!receivedMessage.startsWith("rfsh")) ){
                                    is_ok=true
                                    println("direct IP success..")
                                    isResponseReceivedFirstTime = true
                                    val Temperature_db=Temperature_db.getInstance(context.requireContext())
                                    var receivedmessage_decoded=extract_response(receivedMessage)
                                    var  macip=receivedmessage_decoded[0]
                                    var  pole_num=receivedmessage_decoded[1]
                                    var  key_status=receivedmessage_decoded[2]
                                    var ip =receivedmessage_decoded[3]
                                    println(ip)

                                    var statusList= thermostat_subtype_decoder(key_status)

                                    Temperature_db.updatehermostatById(TThermostst!!.id,statusList[2],statusList[1],statusList[4],statusList[3],statusList[0])

                                }else{
                                    println("direct IP failed self broadcast received..")
                                }


                            }


                        }, 8089, 950, it1, it,checkIP(context.requireContext()))
                    }
                }
            }






        }else if (loopCount == 1 && !isResponseReceivedFirstTime) {
            println("broadcasting..")


            val broadcastIP=convertIpToBroadcast(context.requireContext())
//            send_refresh(TThermostst!!.mac, checkIP(context.requireContext()),broadcastIP)
//            send_refresh(TThermostst!!.mac, checkIP(context.requireContext()),broadcastIP)
//            send_refresh(TThermostst!!.mac, checkIP(context.requireContext()),broadcastIP)
//            send_refresh(TThermostst!!.mac, checkIP(context.requireContext()),broadcastIP)

            println("broadcast sent")


            if (TThermostst != null) {
                TThermostst.mac?.let {
                    receiveUdpMessage_send_inside_brodcast({ receivedMessage ->
                        if (receivedMessage=="failed"){
                            println("broadcast failed..")
                            pass = true

                            is_ok=false


                        }else{
                            pass = true
                            if ((!receivedMessage.startsWith("cmnd"))&&(!receivedMessage.startsWith("rfsh")) ){
                                is_ok=true
                                println("success..")
                                val Temperature_db=Temperature_db.getInstance(context.requireContext())
                                var receivedmessage_decoded=extract_response(receivedMessage)
                                var  macip=receivedmessage_decoded[0]
                                var  pole_num=receivedmessage_decoded[1]
                                var  key_status=receivedmessage_decoded[2]
                                var new_ip =receivedmessage_decoded[3]
                                println(new_ip)
                                var statusList= thermostat_subtype_decoder(key_status)

                                Temperature_db.updatehermostatAndIpById(TThermostst!!.id,statusList[2],statusList[1],statusList[4],statusList[3],statusList[0],new_ip)


                            }else{
                                println("broadcast failed..")
                                is_ok=false
                            }

                        }


                    }, 8089, 950,broadcastIP, it,checkIP(context.requireContext()))
                }
            }


        }

        if (!is_ok){

            loopCount++
        }else{
            break
        }

        // تاخیر یک ثانیه قبل از ورود به دور جدید از حلقه
        if (!pass){
            Thread.sleep(950)

        }
    } while (loopCount < 2) // تکرار حلقه تا دو بار اجرا

    //    UdpListener8089.resume()
    return is_ok
}
fun refresh_thermostat_for_scenario(context: Fragment, thermostat_list: List<Thermostst>?):ArrayList<String>{
    //UdpListener8089.pause()
    val ok_to_send= arrayListOf<String>()
    var is_ok=false
    var isResponseReceivedFirstTime = false


    val timeoutMillis = 1000
    var loopCount = 0
    if (thermostat_list != null) {
        for (TThermostst in thermostat_list){
            do {


                if (loopCount == 0) {


                    send_refresh(TThermostst!!.mac, checkIP(context.requireContext()),TThermostst!!.ip)


                    println("direct IP sent")




                    receiveUdpMessage({ receivedMessage ->
                        if (receivedMessage=="failed"){
                            println("direct IP failed..")
                            is_ok=false

                        }else{
                            if ((!receivedMessage.startsWith("cmnd"))&&(!receivedMessage.startsWith("rfsh")) ){
                                is_ok=true
                                println("direct IP success..")
                                TThermostst.mac?.let { ok_to_send.add(it) }
                                isResponseReceivedFirstTime = true
                                val Temperature_db=Temperature_db.getInstance(context.requireContext())
                                var receivedmessage_decoded=extract_response(receivedMessage)
                                var  macip=receivedmessage_decoded[0]
                                var  pole_num=receivedmessage_decoded[1]
                                var  key_status=receivedmessage_decoded[2]
                                var ip =receivedmessage_decoded[3]

                                var statusList= thermostat_subtype_decoder(key_status)

                                Temperature_db.updatehermostatById(TThermostst!!.id,statusList[2],statusList[1],statusList[4],statusList[3],statusList[0])

                            }else{
                                println("direct IP failed self broadcast received..")
                            }


                        }


                    }, 8089, 700)


                }else if (loopCount == 1 && !isResponseReceivedFirstTime) {
                    println("broadcasting..")


                    val broadcastIP=convertIpToBroadcast(context.requireContext())
                    send_refresh(TThermostst!!.mac, checkIP(context.requireContext()),broadcastIP)

                    println("broadcast sent")


                    receiveUdpMessage({ receivedMessage ->
                        if (receivedMessage=="failed"){
                            println("broadcast failed..")

                            is_ok=false


                        }else{
                            if ((!receivedMessage.startsWith("cmnd"))&&(!receivedMessage.startsWith("rfsh")) ){
                                is_ok=true
                                println("success..")
                                TThermostst.mac?.let { ok_to_send.add(it) }
                                val Temperature_db=Temperature_db.getInstance(context.requireContext())
                                var receivedmessage_decoded=extract_response(receivedMessage)
                                var  macip=receivedmessage_decoded[0]
                                var  pole_num=receivedmessage_decoded[1]
                                var  key_status=receivedmessage_decoded[2]
                                var new_ip =receivedmessage_decoded[3]

                                var statusList= thermostat_subtype_decoder(key_status)

                                Temperature_db.updatehermostatAndIpById(TThermostst!!.id,statusList[2],statusList[1],statusList[4],statusList[3],statusList[0],new_ip)


                            }else{
                                println("broadcast failed..")
                                is_ok=false
                            }

                        }


                    }, 8089, 700)


                }

                loopCount++

                // تاخیر یک ثانیه قبل از ورود به دور جدید از حلقه
                Thread.sleep(700)
            } while (loopCount < 2)


        }
    }

 // تکرار حلقه تا دو بار اجرا
    //    UdpListener8089.resume()

    return ok_to_send
}




fun checkIP(context: Context) :String{
    val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
    var ipString:String=""
    if (wifiManager.isWifiEnabled) {
        val wifiInfo = wifiManager.connectionInfo
        val ipAddress = wifiInfo.ipAddress
        ipString = String.format(
            "%d.%d.%d.%d",
            ipAddress and 0xff,
            ipAddress shr 8 and 0xff,
            ipAddress shr 16 and 0xff,
            ipAddress shr 24 and 0xff
        )

    }
    return ipString

}
fun udp_thermostat (context: Fragment, TThermostst: Thermostst?, mac: String?, mood: String?, temperature: String?, fan_status: String?, on_off: String?, ip: String?,){

    //UdpListener8089.pause()
    var isResponseReceivedFirstTime = false


    val timeoutMillis = 1000
    var loopCount = 0

    do {


        if (loopCount == 0) {


//            send_to_thermostat(mac,mood,temperature,fan_status,on_off,checkIP(context.requireContext()),ip)
//            send_to_thermostat(mac,mood,temperature,fan_status,on_off,checkIP(context.requireContext()),ip)


            println("direct IP sent")




            if (mac != null) {
                receiveUdpMessage_send_inside_thermostat_cmnd({ receivedMessage ->
                    if (receivedMessage=="failed"){
                        println("direct IP failed..")

                    }else{
                        if ((!receivedMessage.startsWith("cmnd"))&&(!receivedMessage.startsWith("rfsh")) ){

                            println("direct IP success..")
                            isResponseReceivedFirstTime = true
                            val Temperature_db=Temperature_db.getInstance(context.requireContext())
                            var receivedmessage_decoded=extract_response(receivedMessage)
                            var  macip=receivedmessage_decoded[0]
                            var  pole_num=receivedmessage_decoded[1]
                            var  key_status=receivedmessage_decoded[2]
                            var ip =receivedmessage_decoded[3]

                            var statusList= thermostat_subtype_decoder(key_status)

                            Temperature_db.updatehermostatById(TThermostst!!.id,temperature,statusList[1],statusList[4],statusList[3],statusList[0],)
                            //    UdpListener8089.resume()
                        }


                    }


                }, 8089, 700,mac,mood,temperature,fan_status,on_off,checkIP(context.requireContext()),ip)
            }






        }else if (loopCount == 1 && !isResponseReceivedFirstTime) {
            println("broadcasting..")


            val broadcastIP=convertIpToBroadcast(context.requireContext())
//            send_to_thermostat(mac,mood,temperature,fan_status,on_off, checkIP(context.requireContext()),broadcastIP)

            println("broadcast sent")


            if (mac != null) {
                receiveUdpMessage_send_inside_thermostat_cmnd_broadcast({ receivedMessage ->
                    if (receivedMessage=="failed"){
                        println("broadcast failed..")


                    }else{
                        if ((!receivedMessage.startsWith("cmnd"))&&(!receivedMessage.startsWith("rfsh")) ){

                            println("success..")
                            val Temperature_db=Temperature_db.getInstance(context.requireContext())
                            var receivedmessage_decoded=extract_response(receivedMessage)
                            var  macip=receivedmessage_decoded[0]
                            var  pole_num=receivedmessage_decoded[1]
                            var  key_status=receivedmessage_decoded[2]
                            var new_ip =receivedmessage_decoded[3]
                            println(new_ip)

                            var statusList= thermostat_subtype_decoder(key_status)

                            Temperature_db.updatehermostatAndIpById(TThermostst!!.id,temperature,statusList[1],statusList[4],statusList[3],statusList[0],new_ip)

                            //    UdpListener8089.resume()
                        }else{

                            println("broadcast failed..")
                            //    UdpListener8089.resume()
                        }


                    }


                }, 8089, 700,mac,mood,temperature,fan_status,on_off,checkIP(context.requireContext()),broadcastIP)
            }


        }

        loopCount++

        // تاخیر یک ثانیه قبل از ورود به دور جدید از حلقه
        Thread.sleep(700)
    } while (loopCount < 2) // تکرار حلقه تا دو بار اجرا




}


fun udp_thermostat_for_scenario (context: Context, TThermostst: Thermostst?, mac: String?, mood: String?, temperature: String?, fan_status: String?, on_off: String?, ip: String?,):Boolean{

    //UdpListener8089.pause()
    var is_ok=false
    var isResponseReceivedFirstTime = false
    var pass=false


    val timeoutMillis = 1000
    var loopCount = 0

    do {


        if (loopCount == 0) {


//            send_to_thermostat(mac,mood,temperature,fan_status,on_off,checkIP(context),ip)


            println("direct IP sent")




            if (mac != null) {
                receiveUdpMessage_send_inside_thermostat_cmnd({ receivedMessage ->
                    if (receivedMessage=="failed"){
                        println("direct IP failed..")
                        pass=true

                    }else{
                        pass=true
                        if ((!receivedMessage.startsWith("cmnd"))&&(!receivedMessage.startsWith("rfsh")) ){

                            println("direct IP success..")
                            is_ok=true
                            isResponseReceivedFirstTime = true
                            val Temperature_db=Temperature_db.getInstance(context)
                            var receivedmessage_decoded=extract_response(receivedMessage)
                            var  macip=receivedmessage_decoded[0]
                            var  pole_num=receivedmessage_decoded[1]
                            var  key_status=receivedmessage_decoded[2]
                            var ip =receivedmessage_decoded[3]

                            var statusList= thermostat_subtype_decoder(key_status)

        //                        Temperature_db.updatehermostatById(TThermostst!!.id,temperature,statusList[1],statusList[4],statusList[3],statusList[0],)

                        }


                    }


                }, 8089, 700,mac,mood,temperature,fan_status,on_off,checkIP(context),ip)
            }






        }else if (loopCount == 1 && !isResponseReceivedFirstTime) {
            println("broadcasting..")


            val broadcastIP=convertIpToBroadcast(context)
//            send_to_thermostat(mac,mood,temperature,fan_status,on_off, checkIP(context),broadcastIP)
//            send_to_thermostat(mac,mood,temperature,fan_status,on_off, checkIP(context),broadcastIP)
//            send_to_thermostat(mac,mood,temperature,fan_status,on_off, checkIP(context),broadcastIP)

            println("broadcast sent")


            if (mac != null) {
                receiveUdpMessage_send_inside_thermostat_cmnd_broadcast({ receivedMessage ->
                    if (receivedMessage=="failed"){
                        println("broadcast failed..")
                        pass=true


                    }else{
                        pass=true
                        if ((!receivedMessage.startsWith("cmnd"))&&(!receivedMessage.startsWith("rfsh")) ){
                            is_ok=true
                            println("success..")
                            val Temperature_db=Temperature_db.getInstance(context)
                            var receivedmessage_decoded=extract_response(receivedMessage)
                            var  macip=receivedmessage_decoded[0]
                            var  pole_num=receivedmessage_decoded[1]
                            var  key_status=receivedmessage_decoded[2]
                            var new_ip =receivedmessage_decoded[3]

                            var statusList= thermostat_subtype_decoder(key_status)

        //                        Temperature_db.updatehermostatAndIpById(TThermostst!!.id,temperature,statusList[1],statusList[4],statusList[3],statusList[0],new_ip)
                        }else{

                            println("broadcast failed..")

                        }


                    }


                }, 8089, 700,mac,mood,temperature,fan_status,on_off, checkIP(context),broadcastIP)
            }


        }

        if (!is_ok){

            loopCount++
        }else{
            break
        }

        // تاخیر یک ثانیه قبل از ورود به دور جدید از حلقه
        if (!pass){
            Thread.sleep(700)
        }

    } while (loopCount < 2) // تکرار حلقه تا دو بار اجرا

    //    UdpListener8089.resume()

    return is_ok
}



fun send_to_curtain(mac:String?,status: String?,myip: String?,ip:String?){
    var targetIP=ip
    //UdpListener8089.pause()






    val message = "cmnd~>$mac~>Crtn~>0000~>${status}00000000000000~>0000~>$myip"
    val serverAddress = InetAddress.getByName(targetIP)
    val serverPort = 8089

    val socket = DatagramSocket()
    val sendData = message.toByteArray()
    val sendPacket = DatagramPacket(sendData, sendData.size, serverAddress, serverPort)
    socket.send(sendPacket)
    socket.close()
    //    UdpListener8089.resume()

}

fun udp_curtain (context: Context, curtain: curtain,status:String?):Boolean{
    //UdpListener8089.pause()
    var pass= false
    var is_ok=false
    var isResponseReceivedFirstTime = false


    val timeoutMillis = 1000
    var loopCount = 0

    do {


        if (loopCount == 0) {


//            send_to_curtain(curtain.mac,status,checkIP(context),curtain.ip)
//
//
//            println("direct IP sent")




            receiveUdpMessage_send_inside_curtain_cmnd({ receivedMessage ->
                if (receivedMessage=="failed"){
                    println("direct IP failed..")
                    pass= true

                }else{
                    pass= true
                     if ((!receivedMessage.startsWith("cmnd"))&&(!receivedMessage.startsWith("rfsh")) ){
                         is_ok=true
                        println("direct IP success..")
                        isResponseReceivedFirstTime = true
                        val curtain_db=curtain_db.getInstance(context)
                        var receivedmessage_decoded=extract_response(receivedMessage)
                        var  macip=receivedmessage_decoded[0]
                        var  pole_num=receivedmessage_decoded[1]
                        var  key_status=receivedmessage_decoded[2]
                        var ip =receivedmessage_decoded[3]

                        var statusList= thermostat_subtype_decoder(key_status)

                        curtain_db.updateStatusById(curtain.id,status)

                    }else{
                        println("direct IP failed..")
                    }



                }



            }, 8089, 700,curtain.mac,status,checkIP(context),curtain.ip)






        }else if (loopCount == 1 && !isResponseReceivedFirstTime) {
            println("broadcasting..")


            val broadcastIP=convertIpToBroadcast(context)
//            send_to_curtain(curtain.mac,status,checkIP(context),broadcastIP)
//
//            println("broadcast sent")


            receiveUdpMessage_send_inside_curtain_cmnd_broadcast({ receivedMessage ->
                if (receivedMessage=="failed"){
                    println("broadcast failed..")
                    pass= true




                }else{
                    pass= true
                     if ((!receivedMessage.startsWith("cmnd"))&&(!receivedMessage.startsWith("rfsh")) ){
                         is_ok=true
                        println("success..")
                        val curtain_db=curtain_db.getInstance(context)
                        var receivedmessage_decoded=extract_response(receivedMessage)
                        var  macip=receivedmessage_decoded[0]
                        var  pole_num=receivedmessage_decoded[1]
                        var  key_status=receivedmessage_decoded[2]
                        var new_ip =receivedmessage_decoded[3]

                        var statusList= thermostat_subtype_decoder(key_status)

                        curtain_db.updateStatusAndIpById(curtain.id,status,new_ip)
                    }else{

                        println("broadcast failed..")

                    }


                }


            }, 8089, 700,curtain.mac,status,checkIP(context),broadcastIP)


        }

        if (!is_ok){

            loopCount++
        }else{
            break
        }

        // تاخیر یک ثانیه قبل از ورود به دور جدید از حلقه
        if (!pass){

            Thread.sleep(700)

        }
    } while (loopCount < 2) // تکرار حلقه تا دو بار اجرا
    //    UdpListener8089.resume()
    return is_ok


}

fun udp_curtain_for_scenario (context: Context, curtain: curtain,status:String?):Boolean{
    //UdpListener8089.pause()
    var is_succes=false
    var pass = false
    var isResponseReceivedFirstTime = false


    val timeoutMillis = 1000
    var loopCount = 0

    do {


        if (loopCount == 0) {


//            send_to_curtain(curtain.mac,status,checkIP(context),curtain.ip)


            println("direct IP sent")




            receiveUdpMessage_send_inside_curtain_cmnd({ receivedMessage ->
                if (receivedMessage=="failed"){
                    println("direct IP failed..")
                    pass = true


                }else{
                    pass = true
                    if ((!receivedMessage.startsWith("cmnd"))&&(!receivedMessage.startsWith("rfsh")) ){
                        is_succes=true
                        println("direct IP success..")
                        isResponseReceivedFirstTime = true
                        val curtain_db=curtain_db.getInstance(context)
                        var receivedmessage_decoded=extract_response(receivedMessage)
                        var  macip=receivedmessage_decoded[0]
                        var  pole_num=receivedmessage_decoded[1]
                        var  key_status=receivedmessage_decoded[2]
                        var ip =receivedmessage_decoded[3]

                        var statusList= thermostat_subtype_decoder(key_status)

                        curtain_db.updateStatusById(curtain.id,status)

                    }else{
                        is_succes=false
                        println("direct IP failed..")
                    }



                }



            }, 8089, 700,curtain.mac,status,checkIP(context),curtain.ip)






        }else if (loopCount == 1 && !isResponseReceivedFirstTime) {
            println("broadcasting..")


            val broadcastIP=convertIpToBroadcast(context)
//            send_to_curtain(curtain.mac,status,checkIP(context),broadcastIP)
//            send_to_curtain(curtain.mac,status,checkIP(context),broadcastIP)
//            send_to_curtain(curtain.mac,status,checkIP(context),broadcastIP)
//            send_to_curtain(curtain.mac,status,checkIP(context),broadcastIP)

            println("broadcast sent")


            receiveUdpMessage_send_inside_curtain_cmnd_broadcast({ receivedMessage ->
                if (receivedMessage=="failed"){
                    println("broadcast failed..")
                    pass = true
                    is_succes=false



                }else{
                    pass = true
                    if ((!receivedMessage.startsWith("cmnd"))&&(!receivedMessage.startsWith("rfsh")) ){
                        is_succes=true
                        println("success..")
                        val curtain_db=curtain_db.getInstance(context)
                        var receivedmessage_decoded=extract_response(receivedMessage)
                        var  macip=receivedmessage_decoded[0]
                        var  pole_num=receivedmessage_decoded[1]
                        var  key_status=receivedmessage_decoded[2]
                        var new_ip =receivedmessage_decoded[3]

                        var statusList= thermostat_subtype_decoder(key_status)

                        curtain_db.updateStatusAndIpById(curtain.id,status,new_ip)
                    }else{

                        println("broadcast failed..")
                        var is_succes=false

                    }


                }


            }, 8089, 700,curtain.mac,status,checkIP(context),broadcastIP)


        }

        if (!is_succes){

            loopCount++
        }else{
            break
        }

        // تاخیر یک ثانیه قبل از ورود به دور جدید از حلقه
        if (!pass ){
            Thread.sleep(700)

        }
    } while (loopCount < 2) // تکرار حلقه تا دو بار اجرا

    //    UdpListener8089.resume()

    return  is_succes
}


fun curtain_subtype_decoder(subtype: String?):MutableList<String> {
    val subtypelist= mutableListOf<String>()
    val status=subtype!!.substring(0,2)

    subtypelist.add(status)

//    val local_temp=subtype!!.substring(1,)
//    val on_off=subtype!!.substring(0,2)
    return subtypelist

}

fun plug_subtype_decoder(subtype: String?):MutableList<String> {
    val subtypelist= mutableListOf<String>()
    val status=subtype!!.substring(0,2)

    subtypelist.add(status)

//    val local_temp=subtype!!.substring(1,)
//    val on_off=subtype!!.substring(0,2)
    return subtypelist

}

fun plug_sub_type_decoder(status: String?, pone_num: String): MutableList<String> {
    println(status)
    println(pone_num)
    var cuted_subtype = status!!.substring(0, pone_num.toInt())
    var status_list = mutableListOf<String>()
    println(cuted_subtype)
    for (statusChar in cuted_subtype) {
        val status = statusChar.toString()
        println(statusChar)

        if (status == "0") {
            status_list.add("0")
        } else if (status == "1") {
            status_list.add("1")
        }
    }
    return status_list
}


fun refresh_curtain(context: Fragment, curtain: curtain):Boolean{
    //UdpListener8089.pause()
    var is_ok=false
    var pass = false
    var isResponseReceivedFirstTime = false


    val timeoutMillis = 1000
    var loopCount = 0

    do {


        if (loopCount == 0) {



            send_refresh(curtain.mac,checkIP(context.requireContext()),curtain.ip)




            println("direct IP sent")




            receiveUdpMessage({ receivedMessage ->
                if (receivedMessage=="failed"){
                    println("direct IP failed..")
                    pass = true
                }else{
                     if ((!receivedMessage.startsWith("cmnd"))&&(!receivedMessage.startsWith("rfsh")) ){
                        println("direct IP success..")
                         pass = true
                         is_ok=true
                        isResponseReceivedFirstTime = true
                        val curtain_db=curtain_db.getInstance(context.requireContext())
                        var receivedmessage_decoded=extract_response(receivedMessage)
                        var  macip=receivedmessage_decoded[0]
                        var  pole_num=receivedmessage_decoded[1]
                        var  key_status=receivedmessage_decoded[2]
                        var ip =receivedmessage_decoded[3]

                        var statusList= curtain_subtype_decoder(key_status)

                        curtain_db.updateStatusById(curtain.id,statusList[0])

                    }else{
                        println("direct IP failed..")
                    }



                }



            }, 8089, 700)






        }else if (loopCount == 1 && !isResponseReceivedFirstTime) {
            println("broadcasting..")


            val broadcastIP=convertIpToBroadcast(context.requireContext())
            send_refresh(curtain.mac,checkIP(context.requireContext()),broadcastIP)


            println("broadcast sent")


            receiveUdpMessage({ receivedMessage ->
                if (receivedMessage=="failed"){
                    println("broadcast failed..")
                    pass = true



                }else{
                    pass = true
                     if ((!receivedMessage.startsWith("cmnd"))&&(!receivedMessage.startsWith("rfsh")) ){
                         is_ok=true
                        println("success..")
                        val curtain_db=curtain_db.getInstance(context.requireContext())
                        var receivedmessage_decoded=extract_response(receivedMessage)
                        var  macip=receivedmessage_decoded[0]
                        var  pole_num=receivedmessage_decoded[1]
                        var  key_status=receivedmessage_decoded[2]
                        var new_ip =receivedmessage_decoded[3]


                        var statusList= curtain_subtype_decoder(key_status)

                        curtain_db.updateStatusAndIpById(curtain.id,statusList[0],new_ip)
                    }else{

                        println("broadcast failed..")

                    }


                }


            }, 8089, 900)


        }

        if (!is_ok){

            loopCount++
        }else{
            break
        }

        // تاخیر یک ثانیه قبل از ورود به دور جدید از حلقه
        if (!pass ){
            Thread.sleep(700)

        }
    } while (loopCount < 2) // تکرار حلقه تا دو بار اجرا
    //    UdpListener8089.resume()
    return  is_ok


}
fun refresh_curtain_for_scenario(context: Fragment, curtainlist: List<curtain>):ArrayList<String> {
    //UdpListener8089.pause()
    val ok_to_send= arrayListOf<String>()
    var isResponseReceivedFirstTime = false


    val timeoutMillis = 1000
    var loopCount = 0
    for (curtain in curtainlist){

        do {


            if (loopCount == 0) {


                send_refresh(curtain.mac,checkIP(context.requireContext()),curtain.ip)


                println("direct IP sent")




                receiveUdpMessage({ receivedMessage ->
                    if (receivedMessage=="failed"){
                        println("direct IP failed..")

                    }else{
                        if ((!receivedMessage.startsWith("cmnd"))&&(!receivedMessage.startsWith("rfsh")) ){
                            println("direct IP success..")
                            curtain.mac?.let { ok_to_send.add(it) }
                            isResponseReceivedFirstTime = true
                            val curtain_db=curtain_db.getInstance(context.requireContext())
                            var receivedmessage_decoded=extract_response(receivedMessage)
                            var  macip=receivedmessage_decoded[0]
                            var  pole_num=receivedmessage_decoded[1]
                            var  key_status=receivedmessage_decoded[2]
                            var ip =receivedmessage_decoded[3]

                            var statusList= curtain_subtype_decoder(key_status)

                            curtain_db.updateStatusById(curtain.id,statusList[0])

                        }else{
                            println("direct IP failed..")
                        }



                    }



                }, 8089, 700)






            }else if (loopCount == 1 && !isResponseReceivedFirstTime) {
                println("broadcasting..")


                val broadcastIP=convertIpToBroadcast(context.requireContext())
                send_refresh(curtain.mac,checkIP(context.requireContext()),broadcastIP)
                send_refresh(curtain.mac,checkIP(context.requireContext()),broadcastIP)
                send_refresh(curtain.mac,checkIP(context.requireContext()),broadcastIP)
                send_refresh(curtain.mac,checkIP(context.requireContext()),broadcastIP)


                println("broadcast sent")


                receiveUdpMessage({ receivedMessage ->
                    if (receivedMessage=="failed"){
                        println("broadcast failed..")




                    }else{
                        if ((!receivedMessage.startsWith("cmnd"))&&(!receivedMessage.startsWith("rfsh")) ){
                            curtain.mac?.let { ok_to_send.add(it) }
                            println("success..")
                            val curtain_db=curtain_db.getInstance(context.requireContext())
                            var receivedmessage_decoded=extract_response(receivedMessage)
                            var  macip=receivedmessage_decoded[0]
                            var  pole_num=receivedmessage_decoded[1]
                            var  key_status=receivedmessage_decoded[2]
                            var new_ip =receivedmessage_decoded[3]

                            var statusList= curtain_subtype_decoder(key_status)

                            curtain_db.updateStatusAndIpById(curtain.id,statusList[0],new_ip)
                        }else{

                            println("broadcast failed..")

                        }


                    }


                }, 8089, 700)


            }

            loopCount++

            // تاخیر یک ثانیه قبل از ورود به دور جدید از حلقه
            Thread.sleep(700)
        } while (loopCount < 2) // تکرار حلقه تا دو بار اجرا





    }

    //    UdpListener8089.resume()
    return ok_to_send

}



fun convertIpToBroadcast(context: Context):String {

    var indexes= mutableListOf<Int>()
    var myIP=checkIP(context)
    var currentIndex=myIP.indexOf(".")
    while (currentIndex >=0){
        indexes.add(currentIndex)
        currentIndex = myIP.indexOf("." , currentIndex + 1)
    }
    var broadcastIP = "${myIP.substring(0,indexes[2]+1)}255"
    return broadcastIP

}


fun plug_sub_type_coder(context: Context, mac: String?): String {
    val plug_database = plug_db.getInstance(context)
    val same_macs = mac?.let { plug_database.getPlugsByMacAddress(it) }
    var key_status = "0000000000000000"

    if (same_macs != null) {
        var pole_count=0
        val first_plug=same_macs.sortedBy { it?.subtype }
        for (plug in same_macs) {
            val pole = plug?.subtype?.toInt() ?: 0
            if (plug?.status == "1") {
                println("on")
                if (key_status.length >= pole) {
                    val startIndex :Int
                    if (first_plug[0!!]!!.subtype == "0000"){
                        startIndex = pole

                    }else{
                        startIndex = pole-1

                    }
                    val endIndex = startIndex + 1
                    val newChar = "1"
                    key_status = key_status.substring(0, startIndex) + newChar + key_status.substring(endIndex)
                }
            }else if (plug?.status == "0") {
                println("off")
                if (key_status.length >= pole) {
                    val startIndex :Int
                    if (first_plug[0!!]!!.subtype == "0000"){
                        startIndex = pole

                    }else{
                        startIndex = pole-1

                    }
                    val endIndex = startIndex + 1
                    val newChar = "0"
                    key_status = key_status.substring(0, startIndex) + newChar + key_status.substring(endIndex)
                }
            }
            if (plug!!.subtype!!.toInt()>pole_count ){
                pole_count=plug.subtype!!.toInt()

            }

        }
        println(pole_count)
        println(key_status)
        key_status="${key_status.substring(0,pole_count)}${key_status.substring(pole_count).replace("0","-")}"

    }
    println(key_status)
    return key_status
}





fun send_to_plug(mac:String?,subtype:String?,status:String?,myip:String,ip:String?){
//    //UdpListener8089.pause()
    var targetIP=ip

    val message = "cmnd~>$mac~>Plug~>$subtype~>$status~>0000~>$myip"
    val serverAddress = InetAddress.getByName(targetIP)
    val serverPort = 8089

    val socket = DatagramSocket()
    val sendData = message.toByteArray()
    val sendPacket = DatagramPacket(sendData, sendData.size, serverAddress, serverPort)
    socket.send(sendPacket)
    socket.close()
//    //    UdpListener8089.resume()

}





fun send_to_elevator(mac:String?,myip:String,ip:String?){
//    //UdpListener8089.pause()
    var targetIP=ip

    val message = "cmnd~>$mac~>Elev~>0000~>1000000000000000~>0000~>$myip"
    val serverAddress = InetAddress.getByName(targetIP)
    val serverPort = 8089

    val socket = DatagramSocket()
    val sendData = message.toByteArray()
    val sendPacket = DatagramPacket(sendData, sendData.size, serverAddress, serverPort)
    socket.send(sendPacket)
    socket.close()
//    //    UdpListener8089.resume()

}




fun udp_elevator (context: Context, elevator: Elevator?):Boolean{
    //UdpListener8089.pause()
    var pass=false
    var is_ok=false
    var isResponseReceivedFirstTime = false


    val timeoutMillis = 1000
    var loopCount = 0

    do {


        if (loopCount == 0) {


            val elevator_database=Elevator_db.getInstance(context)
            send_to_elevator(elevator!!.mac , checkIP(context),elevator_database.get_from_db_Elevator(elevator.id)!!.ip)
            println("direct IP sent")




            receiveUdpMessage({ receivedMessage ->
                if (receivedMessage=="failed"){
                    println("direct IP failed..")
                    pass=true

                }else{
                    pass=true
                    if ((!receivedMessage.startsWith("cmnd"))&&(!receivedMessage.startsWith("rfsh")) ){
                        is_ok=true
                        println("direct IP success..")
                        isResponseReceivedFirstTime = true
                        val plug_database2=plug_db.getInstance(context)
                        var receivedmessage_decoded=extract_response(receivedMessage)
                        var  macip=receivedmessage_decoded[0]
                        var  pole_num=receivedmessage_decoded[1]
                        var  key_status=receivedmessage_decoded[2]
                        var ip =receivedmessage_decoded[3]

//                    var statusList=light_sub_type_decoder(key_status,pole_num)

//                        val plug_database = plug_db.getInstance(context)
//                        val same_macs = plug_database.getPlugsByMacAddress(plug.mac).sortedBy { it!!.subtype!!.toInt() }
//                        var index=0
//                    for (plug_same_mac in same_macs){
//                        plug_database2.updateStatusbyId(plug_same_mac!!.id,statusList[index])
//                        index+=1
//
//                    }

                    }


                }



            }, 8089, 700)






        }else if (loopCount == 1 && !isResponseReceivedFirstTime) {
            println("broadcasting..")
            val elevator_database=Elevator_db.getInstance(context)
            val broadcastIP=convertIpToBroadcast(context)
            send_to_elevator(elevator!!.mac , checkIP(context),broadcastIP)
            println("broadcast sent")
            var isrecived=false
            receiveUdpMessage({ receivedMessage ->
                if (receivedMessage=="failed"){
                    println("broadcast failed..")
                    pass=true

                    println("Broadcast failed..")

                }else{
                    pass=true

                    if ((!receivedMessage.startsWith("cmnd"))&&(!receivedMessage.startsWith("rfsh")) ){
                        println("success..")
                        is_ok=true
                        var receivedmessage_decoded=extract_response(receivedMessage)
                        var  macip=receivedmessage_decoded[0]
                        var  pole_num=receivedmessage_decoded[1]
                        var  key_status=receivedmessage_decoded[2]
                        var ip =receivedmessage_decoded[3]



                        elevator.ip=ip
                        elevator_database.updateElevatorById(elevator.id,elevator)


                    }else{
                        println("Broadcast failed..")

                    }

                }


            }, 8089, 700)


        }

        if (!is_ok){

            loopCount++
        }else{
            break
        }

        // تاخیر یک ثانیه قبل از ورود به دور جدید از حلقه
        if (!pass){
            Thread.sleep(700)

        }
    } while (loopCount < 2) // تکرار حلقه تا دو بار اجرا


    //    UdpListener8089.resume()
    return is_ok

}











fun udp_plug (context: Context, plug: Plug?):Boolean{
    //UdpListener8089.pause()
    var pass=false
    var is_ok=false
    var isResponseReceivedFirstTime = false



    val timeoutMillis = 1000
    var loopCount = 0

    do {


        if (loopCount == 0) {
            if (!isResponseReceivedFirstTime){

                val plug_database=plug_db.getInstance(context)
                println(plug!!.status)
                if(plug_database.getStatusById(plug!!.id)=="1"){

                    plug_database.updateStatusbyId(plug.id, "0")

                }else{
                    plug_database.updateStatusbyId(plug.id, "1")

                }
            }

//            val light_database = light_db.getInstance(context)


//            println(light_database.getStatusById(light.id))


            val plug_database = plug_db.getInstance(context)
            val light_2 = plug_database.get_from_db_Plug(plug!!.id)

            var key_status = "----------------"
            val same_macs = plug!!.mac?.let { plug_database.getPlugsByMacAddress(it) }
            var pole = plug?.subtype?.toInt() ?: 0

            if (same_macs!!.sortedBy { it!!.subtype?.toInt() }[0]!!.subtype!!.toInt() == 0) {
                pole += 1
            }

            if (light_2?.status == "1") {
                println("on")
                if (key_status.length >= pole) {
                    val startIndex = pole - 1
                    val endIndex = startIndex + 1
                    val newChar = "1"
                    key_status = key_status.substring(0, startIndex) + newChar + key_status.substring(endIndex)
                }
            } else if (light_2?.status == "0") {
                println("off")
                if (key_status.length >= pole) {
                    val startIndex = pole - 1
                    val endIndex = startIndex + 1
                    val newChar = "0"
                    key_status = key_status.substring(0, startIndex) + newChar + key_status.substring(endIndex)
                }
            }

//
//            send_to_plug(plug!!.mac,plug!!.subtype,key_status, checkIP(context),plug_database.get_from_db_Plug(plug.id)!!.ip)
//            println("direct IP sent")




            receiveUdpMessage_send_inside_plug_cmnd({ receivedMessage ->
                if (receivedMessage=="failed"){
                    println("direct IP failed..")
                    pass=true

                }else{
                    pass=true
                    if ((!receivedMessage.startsWith("cmnd"))&&(!receivedMessage.startsWith("rfsh")) ){
                        is_ok=true
                        println("direct IP success..")
                        isResponseReceivedFirstTime = true
                        val plug_database2=plug_db.getInstance(context)
                        var receivedmessage_decoded=extract_response(receivedMessage)
                        var  macip=receivedmessage_decoded[0]
                        var  pole_num=receivedmessage_decoded[1]
                        var  key_status=receivedmessage_decoded[2]
                        var ip =receivedmessage_decoded[3]

                        //                    var statusList=light_sub_type_decoder(key_status,pole_num)

                        val plug_database = plug_db.getInstance(context)
                        val same_macs = plug_database.getPlugsByMacAddress(plug.mac).sortedBy { it!!.subtype!!.toInt() }
                        var index=0
                        //                    for (plug_same_mac in same_macs){
                        //                        plug_database2.updateStatusbyId(plug_same_mac!!.id,statusList[index])
                        //                        index+=1
                        //
                        //                    }

                    }


                }


            }, 8089, 700, plug!!.mac,plug!!.subtype,key_status, checkIP(context),plug_database.get_from_db_Plug(plug.id)!!.ip)






        }else if (loopCount == 1 && !isResponseReceivedFirstTime) {

            val plug_database = plug_db.getInstance(context)
            val light_2 = plug_database.get_from_db_Plug(plug!!.id)

            var key_status = "----------------"
            val same_macs = plug!!.mac?.let { plug_database.getPlugsByMacAddress(it) }
            var pole = plug?.subtype?.toInt() ?: 0

            if (same_macs!!.sortedBy { it!!.subtype?.toInt() }[0]!!.subtype!!.toInt() == 0) {
                pole += 1
            }

            if (light_2?.status == "1") {
                println("on")
                if (key_status.length >= pole) {
                    val startIndex = pole - 1
                    val endIndex = startIndex + 1
                    val newChar = "1"
                    key_status = key_status.substring(0, startIndex) + newChar + key_status.substring(endIndex)
                }
            } else if (light_2?.status == "0") {
                println("off")
                if (key_status.length >= pole) {
                    val startIndex = pole - 1
                    val endIndex = startIndex + 1
                    val newChar = "0"
                    key_status = key_status.substring(0, startIndex) + newChar + key_status.substring(endIndex)
                }
            }
            println("broadcasting..")

            val broadcastIP=convertIpToBroadcast(context)
//            send_to_plug(plug!!.mac,plug.subtype,key_status, checkIP(context),broadcastIP)
//            println("broadcast sent")
            var isrecived=false
            receiveUdpMessage_send_inside_plug_cmnd_broadcast({ receivedMessage ->
                if (receivedMessage=="failed"){
                    println("broadcast failed..")
                    pass=true
                    if(plug_database.getStatusById(plug.id)=="1"){

                        plug_database.updateStatusbyId(plug.id, "0")

                    }else{
                        plug_database.updateStatusbyId(plug.id, "1")

                    }
                    println("Broadcast failed..")

                }else{
                    pass=true

                    if ((!receivedMessage.startsWith("cmnd"))&&(!receivedMessage.startsWith("rfsh")) ){
                        println("success..")
                        is_ok=true
                        var receivedmessage_decoded=extract_response(receivedMessage)
                        var  macip=receivedmessage_decoded[0]
                        var  pole_num=receivedmessage_decoded[1]
                        var  key_status=receivedmessage_decoded[2]
                        var ip =receivedmessage_decoded[3]
                        val light_database3=plug_db.getInstance(context)
                        val same_macs = light_database3.getPlugsByMacAddress(plug.mac).sortedBy { it!!.subtype!!.toInt() }

                        //                    var statusList=light_sub_type_decoder(key_status,pole_num)
                        //                    var index=0
                        for (light_same_mac3 in same_macs){
                            light_database3.updatePlugbyId(light_same_mac3!!.id , ip)
                            //                        light_database3.updateStatusById(light_same_mac3!!.id,statusList[index])
                            //                        index+=1
                        }

                    }else{
                        println("Broadcast failed..")

                    }

                }


            }, 8089, 700, plug!!.mac,plug.subtype,key_status, checkIP(context),broadcastIP)


        }

        if (!is_ok){

            loopCount++
        }else{
            break
        }

        // تاخیر یک ثانیه قبل از ورود به دور جدید از حلقه
        if (!pass){
            Thread.sleep(700)

        }
    } while (loopCount < 2) // تکرار حلقه تا دو بار اجرا


    //    UdpListener8089.resume()
    return is_ok

}

fun udp_plug_sms (context: Context, plug: Plug?,status: String?):Boolean{
    //UdpListener8089.pause()
    var pass=false
    var is_ok=false
    var isResponseReceivedFirstTime = false


    val plug_database=plug_db.getInstance(context)
    val timeoutMillis = 1000
    var loopCount = 0
    val same_macs = plug!!.mac?.let { plug_database.getPlugsByMacAddress(it) }
    var pole = plug?.subtype?.toInt() ?: 0

    if (same_macs!!.sortedBy { it!!.subtype?.toInt() }[0]!!.subtype!!.toInt() == 1) {
        pole -= 1
    }

    do {


        if (loopCount == 0) {
            if (!isResponseReceivedFirstTime){


            }


            if(status!!.length <=2){


//                send_to_plug(plug!!.mac,plug!!.subtype,createStatusString(pole , status!!.toInt()), checkIP(context),plug_database.get_from_db_Plug(plug.id)!!.ip)
                receiveUdpMessage_send_inside_plug_cmnd({ receivedMessage ->
                    if (receivedMessage=="failed"){
                        println("direct IP failed..")
                        pass=true

                    }else{
                        pass=true
                        if ((!receivedMessage.startsWith("cmnd"))&&(!receivedMessage.startsWith("rfsh")) ){
                            is_ok=true
                            println("direct IP success..")
                            isResponseReceivedFirstTime = true
                            val plug_database2=plug_db.getInstance(context)
                            var receivedmessage_decoded=extract_response(receivedMessage)
                            var  macip=receivedmessage_decoded[0]
                            var  pole_num=receivedmessage_decoded[1]
                            var  key_status=receivedmessage_decoded[2]
                            var ip =receivedmessage_decoded[3]

                            //                    var statusList=light_sub_type_decoder(key_status,pole_num)

                            val plug_database = plug_db.getInstance(context)
                            val same_macs = plug_database.getPlugsByMacAddress(plug.mac).sortedBy { it!!.subtype!!.toInt() }
                            var index=0
                            //                    for (plug_same_mac in same_macs){
                            //                        plug_database2.updateStatusbyId(plug_same_mac!!.id,statusList[index])
                            //                        index+=1
                            //
                            //                    }

                        }


                    }


                }, 8089, 700, plug!!.mac,plug!!.subtype,createStatusString(pole , status!!.toInt()), checkIP(context),plug_database.get_from_db_Plug(plug.id)!!.ip)
            }else{
//                send_to_plug(plug!!.mac,plug!!.subtype,status, checkIP(context),plug_database.get_from_db_Plug(plug.id)!!.ip)
                receiveUdpMessage_send_inside_plug_cmnd({ receivedMessage ->
                    if (receivedMessage=="failed"){
                        println("direct IP failed..")
                        pass=true

                    }else{
                        pass=true
                        if ((!receivedMessage.startsWith("cmnd"))&&(!receivedMessage.startsWith("rfsh")) ){
                            is_ok=true
                            println("direct IP success..")
                            isResponseReceivedFirstTime = true
                            val plug_database2=plug_db.getInstance(context)
                            var receivedmessage_decoded=extract_response(receivedMessage)
                            var  macip=receivedmessage_decoded[0]
                            var  pole_num=receivedmessage_decoded[1]
                            var  key_status=receivedmessage_decoded[2]
                            var ip =receivedmessage_decoded[3]

                            //                    var statusList=light_sub_type_decoder(key_status,pole_num)

                            val plug_database = plug_db.getInstance(context)
                            val same_macs = plug_database.getPlugsByMacAddress(plug.mac).sortedBy { it!!.subtype!!.toInt() }
                            var index=0
                            //                    for (plug_same_mac in same_macs){
                            //                        plug_database2.updateStatusbyId(plug_same_mac!!.id,statusList[index])
                            //                        index+=1
                            //
                            //                    }

                        }


                    }


                }, 8089, 700, plug!!.mac,plug!!.subtype,status, checkIP(context),plug_database.get_from_db_Plug(plug.id)!!.ip)
            }












        }else if (loopCount == 1 && !isResponseReceivedFirstTime) {
            println("broadcasting..")
            val plug_database=plug_db.getInstance(context)
            val broadcastIP=convertIpToBroadcast(context)
            if(status!!.length <=2){


//                send_to_plug(plug!!.mac,plug.subtype,createStatusString(pole , status!!.toInt()), checkIP(context),broadcastIP)
                receiveUdpMessage_send_inside_plug_cmnd_broadcast({ receivedMessage ->
                    if (receivedMessage=="failed"){
                        println("broadcast failed..")
                        pass=true

                        println("Broadcast failed..")

                    }else{
                        pass=true

                        if ((!receivedMessage.startsWith("cmnd"))&&(!receivedMessage.startsWith("rfsh")) ){
                            println("success..")
                            is_ok=true
                            var receivedmessage_decoded=extract_response(receivedMessage)
                            var  macip=receivedmessage_decoded[0]
                            var  pole_num=receivedmessage_decoded[1]
                            var  key_status=receivedmessage_decoded[2]
                            var ip =receivedmessage_decoded[3]
                            val light_database3=plug_db.getInstance(context)
                            val same_macs = light_database3.getPlugsByMacAddress(plug.mac).sortedBy { it!!.subtype!!.toInt() }

                            //                    var statusList=light_sub_type_decoder(key_status,pole_num)
                            //                    var index=0
                            for (light_same_mac3 in same_macs){
                                light_database3.updatePlugbyId(light_same_mac3!!.id , ip)
                                //                        light_database3.updateStatusById(light_same_mac3!!.id,statusList[index])
                                //                        index+=1
                            }

                        }else{
                            println("Broadcast failed..")

                        }

                    }


                }, 8089, 700, plug!!.mac,plug.subtype,createStatusString(pole , status!!.toInt()), checkIP(context),broadcastIP)
            }else{
//                send_to_plug(plug!!.mac,plug.subtype,status, checkIP(context),broadcastIP)
                receiveUdpMessage_send_inside_plug_cmnd_broadcast({ receivedMessage ->
                    if (receivedMessage=="failed"){
                        println("broadcast failed..")
                        pass=true

                        println("Broadcast failed..")

                    }else{
                        pass=true

                        if ((!receivedMessage.startsWith("cmnd"))&&(!receivedMessage.startsWith("rfsh")) ){
                            println("success..")
                            is_ok=true
                            var receivedmessage_decoded=extract_response(receivedMessage)
                            var  macip=receivedmessage_decoded[0]
                            var  pole_num=receivedmessage_decoded[1]
                            var  key_status=receivedmessage_decoded[2]
                            var ip =receivedmessage_decoded[3]
                            val light_database3=plug_db.getInstance(context)
                            val same_macs = light_database3.getPlugsByMacAddress(plug.mac).sortedBy { it!!.subtype!!.toInt() }

                            //                    var statusList=light_sub_type_decoder(key_status,pole_num)
                            //                    var index=0
                            for (light_same_mac3 in same_macs){
                                light_database3.updatePlugbyId(light_same_mac3!!.id , ip)
                                //                        light_database3.updateStatusById(light_same_mac3!!.id,statusList[index])
                                //                        index+=1
                            }

                        }else{
                            println("Broadcast failed..")

                        }

                    }


                }, 8089, 700, plug!!.mac,plug.subtype,status, checkIP(context),broadcastIP)
            }




        }

        if (!is_ok){

            loopCount++
        }else{
            break
        }

        // تاخیر یک ثانیه قبل از ورود به دور جدید از حلقه
        if (!pass){
            Thread.sleep(700)

        }
    } while (loopCount < 2) // تکرار حلقه تا دو بار اجرا


    //    UdpListener8089.resume()
    return is_ok

}


fun udp_plug_scenario (context: Context, plugList: List<Plug>) :Boolean{
    //UdpListener8089.pause()
    var is_succes=false
    var isResponseReceivedFirstTime = false

    var pass=false
    val timeoutMillis = 1000
    var loopCount = 0

    do {


        if (loopCount == 0) {
            if (!isResponseReceivedFirstTime){

                val plug_database=plug_db.getInstance(context)
//                println(plug!!.status)
                for (plug in plugList){
                    if(plug.status=="1"){

                        plug_database.updateStatusbyId(plug.id, "1")

                    }else{
                        plug_database.updateStatusbyId(plug.id, "0")

                    }


                }

            }

            val plug_database=plug_db.getInstance(context)
//            send_to_plug(plugList[0]!!.mac,plugList[0]!!.subtype,plug_sub_type_coder(context,plugList[0].mac), checkIP(context),plug_database.get_from_db_Plug(plugList[0].id)!!.ip)
            println("direct IP sent")




            receiveUdpMessage_send_inside_plug_cmnd({ receivedMessage ->
                if (receivedMessage=="failed"){
                    println("direct IP failed..")
                    pass=true
                    is_succes=false

                }else{
                    pass=true
                    if ((!receivedMessage.startsWith("cmnd"))&&(!receivedMessage.startsWith("rfsh")) ){
                        println("direct IP success..")
                        is_succes=true
                        isResponseReceivedFirstTime = true
                        val plug_database2=plug_db.getInstance(context)
                        var receivedmessage_decoded=extract_response(receivedMessage)
                        var  macip=receivedmessage_decoded[0]
                        var  pole_num=receivedmessage_decoded[1]
                        var  key_status=receivedmessage_decoded[2]
                        var ip =receivedmessage_decoded[3]

//                    var statusList=light_sub_type_decoder(key_status,pole_num)

                        val plug_database = plug_db.getInstance(context)
                        val same_macs = plug_database.getPlugsByMacAddress(plugList[0].mac).sortedBy { it!!.subtype!!.toInt() }
                        var index=0
//                    for (plug_same_mac in same_macs){
//                        plug_database2.updateStatusbyId(plug_same_mac!!.id,statusList[index])
//                        index+=1
//
//                    }

                    }


                }



            }, 8089, 700,plugList[0]!!.mac,plugList[0]!!.subtype,plug_sub_type_coder(context,plugList[0].mac), checkIP(context),plug_database.get_from_db_Plug(plugList[0].id)!!.ip)






        }else if (loopCount == 1 && !isResponseReceivedFirstTime) {
            println("broadcasting..")
            val plug_database=plug_db.getInstance(context)
            val broadcastIP=convertIpToBroadcast(context)
//            send_to_plug(plugList[0]!!.mac,plugList[0].subtype,plug_sub_type_coder(context,plugList[0].mac), checkIP(context),broadcastIP)
//            send_to_plug(plugList[0]!!.mac,plugList[0].subtype,plug_sub_type_coder(context,plugList[0].mac), checkIP(context),broadcastIP)
//            send_to_plug(plugList[0]!!.mac,plugList[0].subtype,plug_sub_type_coder(context,plugList[0].mac), checkIP(context),broadcastIP)
//            send_to_plug(plugList[0]!!.mac,plugList[0].subtype,plug_sub_type_coder(context,plugList[0].mac), checkIP(context),broadcastIP)
            println("broadcast sent")
            var isrecived=false
            receiveUdpMessage_send_inside_plug_cmnd_broadcast({ receivedMessage ->
                if (receivedMessage=="failed"){
                    pass=true
                    println("broadcast failed..")
                    is_succes=false
//                    for (plug in plugList){
//                        if(plug_database.getStatusById(plug!!.id)=="1"){
//
//                            plug_database.updateStatusbyId(plug.id, "0")
//
//                        }else{
//                            plug_database.updateStatusbyId(plug.id, "1")
//
//                        }
//
//
//                    }
                    println("Broadcast failed..")

                }else{
                    pass=true

                    if ((!receivedMessage.startsWith("cmnd"))&&(!receivedMessage.startsWith("rfsh")) ){
                        println("success..")
                        is_succes=true
                        var receivedmessage_decoded=extract_response(receivedMessage)
                        var  macip=receivedmessage_decoded[0]
                        var  pole_num=receivedmessage_decoded[1]
                        var  key_status=receivedmessage_decoded[2]
                        var ip =receivedmessage_decoded[3]
                        val light_database3=plug_db.getInstance(context)
                        val same_macs = light_database3.getPlugsByMacAddress(plugList[0].mac).sortedBy { it!!.subtype!!.toInt() }

//                    var statusList=light_sub_type_decoder(key_status,pole_num)
//                    var index=0
                        for (light_same_mac3 in same_macs){
                            light_database3.updatePlugbyId(light_same_mac3!!.id , ip)
//                        light_database3.updateStatusById(light_same_mac3!!.id,statusList[index])
//                        index+=1
                        }

                    }else{
                        println("Broadcast failed..")
                        is_succes=false

                    }

                }


            }, 8089, 700,plugList[0]!!.mac,plugList[0].subtype,plug_sub_type_coder(context,plugList[0].mac), checkIP(context),broadcastIP)


        }

        if (!is_succes){

            loopCount++
        }else{
            break
        }

        // تاخیر یک ثانیه قبل از ورود به دور جدید از حلقه
        if (!pass){
            Thread.sleep(700)

        }

    } while (loopCount < 2) // تکرار حلقه تا دو بار اجرا
    //    UdpListener8089.resume()
    return is_succes



}







//
//fun refresh_plug_for_scenario(context:Context, plugList: List<Plug> ) : ArrayList<String>{
//
//    val ok_to_send= arrayListOf<String>()
//    val plug_database1=plug_db.getInstance(context)
//    for (same_mac_list in plugList) {
//        var isResponseReceivedFirstTime = false
//        var loopCount = 0
//
//        do {
//
//
//            if (loopCount == 0) {
//
//
//                send_refresh(same_mac_list.mac,checkIP(context),same_mac_list.ip)
//
//
//                println("direct IP sent")
//
//
//
//
//                receiveUdpMessage({ receivedMessage ->
//                    if (receivedMessage=="failed"){
//                        println("direct IP failed..")
//
//                    }else{
//                        if ((!receivedMessage.startsWith("cmnd"))&&(!receivedMessage.startsWith("rfsh")) ){
//                            println("direct IP success..")
//                            same_mac_list.mac?.let { ok_to_send.add(it) }
//                            isResponseReceivedFirstTime = true
//                            val plug_db=plug_db.getInstance(context)
//                            var receivedmessage_decoded=extract_response(receivedMessage)
//                            var  macip=receivedmessage_decoded[0]
//                            var  pole_num=receivedmessage_decoded[1]
//                            var  key_status=receivedmessage_decoded[2]
//                            var ip =receivedmessage_decoded[3]
//
//                            var statusList=plug_sub_type_decoder(key_status,plug_db.getPlugsByMacAddress(macip)[plug_db.getPlugsByMacAddress(macip).count()-1]!!.subtype.toString())
//
//
//                            val same_macs = plug_db.getPlugsByMacAddress(same_mac_list.mac).sortedBy { it!!.subtype!!.toInt() }
//                            var index=0
//                            for (lightt in same_macs){
//                                plug_database1.updateStatusbyId(lightt!!.id,statusList[0+index])
//                                index+=1
//
//                            }
//
//
//
//
//                        }else{
//                            println("direct IP failed..")
//                        }
//
//
//
//                    }
//
//
//
//                }, 8089, 700)
//
//
//
//
//
//
//            }else if (loopCount == 1 && !isResponseReceivedFirstTime) {
//                println("broadcasting..")
//
//
//                val broadcastIP=convertIpToBroadcast(context)
//                send_refresh(same_mac_list.mac,checkIP(context),broadcastIP)
//
//
//                println("broadcast sent")
//
//
//                receiveUdpMessage({ receivedMessage ->
//                    if (receivedMessage=="failed"){
//                        println("broadcast failed..")
//
//
//
//
//                    }else{
//                        if ((!receivedMessage.startsWith("cmnd"))&&(!receivedMessage.startsWith("rfsh")) ){
//                            same_mac_list.mac?.let { ok_to_send.add(it) }
//                            println("success..")
//                            val plug_db=plug_db.getInstance(context)
//                            var receivedmessage_decoded=extract_response(receivedMessage)
//                            var  macip=receivedmessage_decoded[0]
//                            var  pole_num=receivedmessage_decoded[1]
//                            var  key_status=receivedmessage_decoded[2]
//                            var new_ip =receivedmessage_decoded[3]
//
//
//
//                            var statusList=plug_sub_type_decoder(key_status,plug_db.getPlugsByMacAddress(macip)[plug_db.getPlugsByMacAddress(macip).count()-1]!!.subtype.toString())
//
//                            val same_macs = plug_db.getPlugsByMacAddress(same_mac_list.mac).sortedBy { it!!.subtype!!.toInt() }
//                            var index=0
//                            for (lightt in same_macs){
//                                plug_database1.updateStatusAndIpById(lightt!!.id,statusList[0+index],new_ip)
//                                index+=1
//
//                            }
//
//
//                        }else{
//
//                            println("broadcast failed..")
//
//                        }
//
//
//                    }
//
//
//                }, 8089, 700)
//
//
//            }
//
//            loopCount++
//
//            // تاخیر یک ثانیه قبل از ورود به دور جدید از حلقه
//            Thread.sleep(700)
//        } while (loopCount < 2)
//
//
//
//    }
//
//
//    // تکرار حلقه تا دو بار اجرا
//
//    return ok_to_send
//
//}

//fun udp_plug_scenario (context: Context, plugList: List<Plug>) :Boolean{
//    var is_succes=false
//    var isResponseReceivedFirstTime = false
//
//
//    val timeoutMillis = 1000
//    var loopCount = 0
//
//    do {
//
//
//        if (loopCount == 0) {
//            if (!isResponseReceivedFirstTime){
//
//                val plug_database=plug_db.getInstance(context)
////                println(plug!!.status)
//                for (plug in plugList){
//                    if(plug_database.getStatusById(plug!!.id)=="1"){
//
//                        plug_database.updateStatusbyId(plug.id, "0")
//
//                    }else{
//                        plug_database.updateStatusbyId(plug.id, "1")
//
//                    }
//
//
//                }
//
//            }
//
//            val plug_database=plug_db.getInstance(context)
//            send_to_plug(plugList[0]!!.mac,plugList[0]!!.subtype,plug_sub_type_coder(context,plugList[0].mac), checkIP(context),plug_database.get_from_db_Plug(plugList[0].id)!!.ip)
//            println("direct IP sent")
//
//
//
//
//            receiveUdpMessage({ receivedMessage ->
//                if (receivedMessage=="failed"){
//                    println("direct IP failed..")
//                    is_succes=false
//
//                }else{
//                    if ((!receivedMessage.startsWith("cmnd"))&&(!receivedMessage.startsWith("rfsh")) ){
//                        println("direct IP success..")
//                        is_succes=true
//                        isResponseReceivedFirstTime = true
//                        val plug_database2=plug_db.getInstance(context)
//                        var receivedmessage_decoded=extract_response(receivedMessage)
//                        var  macip=receivedmessage_decoded[0]
//                        var  pole_num=receivedmessage_decoded[1]
//                        var  key_status=receivedmessage_decoded[2]
//                        var ip =receivedmessage_decoded[3]
//
////                    var statusList=light_sub_type_decoder(key_status,pole_num)
//
//                        val plug_database = plug_db.getInstance(context)
//                        val same_macs = plug_database.getPlugsByMacAddress(plugList[0].mac).sortedBy { it!!.subtype!!.toInt() }
//                        var index=0
////                    for (plug_same_mac in same_macs){
////                        plug_database2.updateStatusbyId(plug_same_mac!!.id,statusList[index])
////                        index+=1
////
////                    }
//
//                    }
//
//
//                }
//
//
//
//            }, 8089, 700)
//
//
//
//
//
//
//        }else if (loopCount == 1 && !isResponseReceivedFirstTime) {
//            println("broadcasting..")
//            val plug_database=plug_db.getInstance(context)
//            val broadcastIP=convertIpToBroadcast(context)
//            send_to_plug(plugList[0]!!.mac,plugList[0].subtype,plug_sub_type_coder(context,plugList[0].mac), checkIP(context),broadcastIP)
//            println("broadcast sent")
//            var isrecived=false
//            receiveUdpMessage({ receivedMessage ->
//                if (receivedMessage=="failed"){
//                    println("broadcast failed..")
//                    is_succes=false
//                    for (plug in plugList){
//                        if(plug_database.getStatusById(plug!!.id)=="1"){
//
//                            plug_database.updateStatusbyId(plug.id, "0")
//
//                        }else{
//                            plug_database.updateStatusbyId(plug.id, "1")
//
//                        }
//
//
//                    }
//                    println("Broadcast failed..")
//
//                }else{
//
//                    if ((!receivedMessage.startsWith("cmnd"))&&(!receivedMessage.startsWith("rfsh")) ){
//                        println("success..")
//                        is_succes=true
//                        var receivedmessage_decoded=extract_response(receivedMessage)
//                        var  macip=receivedmessage_decoded[0]
//                        var  pole_num=receivedmessage_decoded[1]
//                        var  key_status=receivedmessage_decoded[2]
//                        var ip =receivedmessage_decoded[3]
//                        val light_database3=plug_db.getInstance(context)
//                        val same_macs = light_database3.getPlugsByMacAddress(plugList[0].mac).sortedBy { it!!.subtype!!.toInt() }
//
////                    var statusList=light_sub_type_decoder(key_status,pole_num)
////                    var index=0
//                        for (light_same_mac3 in same_macs){
//                            light_database3.updatePlugbyId(light_same_mac3!!.id , ip)
////                        light_database3.updateStatusById(light_same_mac3!!.id,statusList[index])
////                        index+=1
//                        }
//
//                    }else{
//                        println("Broadcast failed..")
//                        is_succes=false
//
//                    }
//
//                }
//
//
//            }, 8089, 700)
//
//
//        }
//
//        loopCount++
//
//        // تاخیر یک ثانیه قبل از ورود به دور جدید از حلقه
//        Thread.sleep(700)
//    } while (loopCount < 2) // تکرار حلقه تا دو بار اجرا
//    return is_succes
//
//
//
//}


fun refresh_plug_for_scenario(context:Context, plugList: List<Plug> ) : ArrayList<String>{
    //UdpListener8089.pause()
    var pass = false
    val ok_to_send= arrayListOf<String>()
    val plug_database1=plug_db.getInstance(context)
    for (same_mac_list in plugList) {
        var isResponseReceivedFirstTime = false
        var loopCount = 0

        do {


            if (loopCount == 0) {


                send_refresh(same_mac_list.mac,checkIP(context),same_mac_list.ip)


                println("direct IP sent")




                receiveUdpMessage({ receivedMessage ->
                    if (receivedMessage=="failed"){
                        println("direct IP failed..")
                        pass = true

                    }else{
                        pass = true
                        if ((!receivedMessage.startsWith("cmnd"))&&(!receivedMessage.startsWith("rfsh")) ){
                            println("direct IP success..")
                            same_mac_list.mac?.let { ok_to_send.add(it) }
                            isResponseReceivedFirstTime = true
                            val plug_db=plug_db.getInstance(context)
                            var receivedmessage_decoded=extract_response(receivedMessage)
                            var  macip=receivedmessage_decoded[0]
                            var  pole_num=receivedmessage_decoded[1]
                            var  key_status=receivedmessage_decoded[2]
                            var ip =receivedmessage_decoded[3]

                            var statusList=plug_sub_type_decoder(key_status,plug_db.getPlugsByMacAddress(macip)[plug_db.getPlugsByMacAddress(macip).count()-1]!!.subtype.toString())


                            val same_macs = plug_db.getPlugsByMacAddress(same_mac_list.mac).sortedBy { it!!.subtype!!.toInt() }
                            var index=0
                            for (lightt in same_macs){
                                plug_database1.updateStatusbyId(lightt!!.id,statusList[0+index])
                                index+=1

                            }




                        }else{
                            println("direct IP failed..")
                        }



                    }



                }, 8089, 700)






            }else if (loopCount == 1 && !isResponseReceivedFirstTime) {
                println("broadcasting..")


                val broadcastIP=convertIpToBroadcast(context)
                send_refresh(same_mac_list.mac,checkIP(context),broadcastIP)


                println("broadcast sent")


                receiveUdpMessage({ receivedMessage ->
                    if (receivedMessage=="failed"){
                        println("broadcast failed..")
                        pass = true



                    }else{
                        pass = true
                        if ((!receivedMessage.startsWith("cmnd"))&&(!receivedMessage.startsWith("rfsh")) ){
                            same_mac_list.mac?.let { ok_to_send.add(it) }
                            println("success..")
                            val plug_db=plug_db.getInstance(context)
                            var receivedmessage_decoded=extract_response(receivedMessage)
                            var  macip=receivedmessage_decoded[0]
                            var  pole_num=receivedmessage_decoded[1]
                            var  key_status=receivedmessage_decoded[2]
                            var new_ip =receivedmessage_decoded[3]



                            var statusList=plug_sub_type_decoder(key_status,plug_db.getPlugsByMacAddress(macip)[plug_db.getPlugsByMacAddress(macip).count()-1]!!.subtype.toString())

                            val same_macs = plug_db.getPlugsByMacAddress(same_mac_list.mac).sortedBy { it!!.subtype!!.toInt() }
                            var index=0
                            for (lightt in same_macs){
                                plug_database1.updateStatusAndIpById(lightt!!.id,statusList[0+index],new_ip)
                                index+=1

                            }


                        }else{

                            println("broadcast failed..")

                        }


                    }


                }, 8089, 700)


            }

            loopCount++

            // تاخیر یک ثانیه قبل از ورود به دور جدید از حلقه
            if (!pass ){
                Thread.sleep(700)

            }
        } while (loopCount < 2)



    }


    // تکرار حلقه تا دو بار اجرا

    //    UdpListener8089.resume()
    return ok_to_send

}




fun refresh_plug(context: Fragment, plug: Plug , rooms: rooms?):Boolean{
    //UdpListener8089.pause()
    var is_ok=false

    val plug_database1=plug_db.getInstance(context.requireContext())
    val plug_list=plug_database1.getplugsWithNonEmptyMacByRoomName2(rooms!!.room_name)
    for (same_mac_list in plug_list) {
        var pass=false
        var isResponseReceivedFirstTime = false
        var loopCount = 0

        do {


            if (loopCount == 0) {


                send_refresh(plug.mac,checkIP(context.requireContext()),plug.ip)


                println("direct IP sent")




                receiveUdpMessage({ receivedMessage ->
                    if (receivedMessage=="failed"){
                        println("direct IP failed..")
                        pass=true

                    }else{
                        pass=true
                        if ((!receivedMessage.startsWith("cmnd"))&&(!receivedMessage.startsWith("rfsh")) ){
                            println("direct IP success..")
                            is_ok=true
                            isResponseReceivedFirstTime = true
                            val plug_db=plug_db.getInstance(context.requireContext())
                            var receivedmessage_decoded=extract_response(receivedMessage)
                            var  macip=receivedmessage_decoded[0]
                            var  pole_num=receivedmessage_decoded[1]
                            var  key_status=receivedmessage_decoded[2]
                            var ip =receivedmessage_decoded[3]

                            var statusList=plug_sub_type_decoder(key_status,(plug_db.getPlugsByMacAddress(macip).count()).toString())


                            println("           "+statusList)

                            val same_macs = plug_db.getPlugsByMacAddress(same_mac_list[0]!!.mac).sortedBy { it!!.subtype!!.toInt() }
                            var index=0
                            for (lightt in same_macs){
                                plug_database1.updateStatusbyId(lightt!!.id,statusList[0+index])
                                index+=1

                            }




                        }else{
                            println("direct IP failed..")
                        }



                    }



                }, 8089, 700)






            }else if (loopCount == 1 && !isResponseReceivedFirstTime) {
                println("broadcasting..")


                val broadcastIP=convertIpToBroadcast(context.requireContext())
                send_refresh(plug.mac,checkIP(context.requireContext()),broadcastIP)


                println("broadcast sent")


                receiveUdpMessage({ receivedMessage ->
                    if (receivedMessage=="failed"){
                        println("broadcast failed..")
                        pass=true



                    }else{
                        pass=true
                        if ((!receivedMessage.startsWith("cmnd"))&&(!receivedMessage.startsWith("rfsh")) ){
                            is_ok=true
                            println("success..")
                            val plug_db=plug_db.getInstance(context.requireContext())
                            var receivedmessage_decoded=extract_response(receivedMessage)
                            var  macip=receivedmessage_decoded[0]
                            var  pole_num=receivedmessage_decoded[1]
                            var  key_status=receivedmessage_decoded[2]
                            var new_ip =receivedmessage_decoded[3]

                            var pole_num_f = plug_db.getPlugsByMacAddress(macip).sortedBy { it!!.subtype?.toInt() }[plug_db.getPlugsByMacAddress(macip).count()-1]!!.subtype.toString()


//                            var statusList=plug_sub_type_decoder(key_status,plug_db.getPlugsByMacAddress(macip)[plug_db.getPlugsByMacAddress(macip).count()-1]!!.subtype.toString())


                            var statusList:MutableList<String>
                            if (plug_db.getPlugsByMacAddress(macip).sortedBy { it!!.subtype?.toInt() }[0]!!.subtype!!.toInt() == 0){
//                                println(pole_num_f)

                                pole_num_f=(pole_num_f.toInt()+1).toString()
//                                println(pole_num_f)
                                statusList=light_sub_type_decoder(key_status,pole_num_f)

                            }else{
                                statusList=light_sub_type_decoder(key_status,pole_num_f)

                            }


                            val same_macs = plug_db.getPlugsByMacAddress(same_mac_list[0]!!.mac).sortedBy { it!!.subtype!!.toInt() }
                            var index=0
                            for (lightt in same_macs){
                                plug_database1.updateStatusAndIpById(lightt!!.id,statusList[0+index],new_ip)
                                index+=1

                            }


                        }else{

                            println("broadcast failed..")

                        }


                    }


                }, 8089, 700)


            }

            if (!pass){


                // تاخیر یک ثانیه قبل از ورود به دور جدید از حلقه
                Thread.sleep(700)
            }
            if (!is_ok) {
                loopCount++

            }else{
                break
            }

        } while (loopCount < 2)



    }


    // تکرار حلقه تا دو بار اجرا
    //    UdpListener8089.resume()

return is_ok

}




fun valve_sub_type_coder(context: Context, mac: String?): String {
    val valve_database = valve_db.getInstance(context)
    val same_macs = mac?.let { valve_database.getvalvesByMacAddress(it) }
    var key_status = "0000000000000000"

    if (same_macs != null) {
        var pole_count=0
        val first_plug=same_macs.sortedBy { it?.subtype }
        for (valve in same_macs) {
            val pole = valve?.subtype?.toInt() ?: 0
            if (valve?.status == "1") {
                println("on")
                if (key_status.length >= pole) {
                    var startIndex :Int
                    if (first_plug[0!!]!!.subtype == "0000"){
                        startIndex = pole

                    }else{
                        startIndex = pole-1

                    }
                    val endIndex = startIndex + 1
                    val newChar = "1"
                    key_status = key_status.substring(0, startIndex) + newChar + key_status.substring(endIndex)
                }
            }else if (valve?.status == "0") {
                println("off")
                if (key_status.length >= pole) {
                    val startIndex :Int
                    if (first_plug[0!!]!!.subtype == "0000"){
                        startIndex = pole

                    }else{
                        startIndex = pole-1

                    }
                    val endIndex = startIndex + 1
                    val newChar = "0"
                    key_status = key_status.substring(0, startIndex) + newChar + key_status.substring(endIndex)
                }
            }
            if (valve!!.subtype!!.toInt()>pole_count ){
                pole_count=valve.subtype!!.toInt()

            }

        }
        println(pole_count)
        println(key_status)
        key_status="${key_status.substring(0,pole_count)}${key_status.substring(pole_count).replace("0","-")}"

    }
    println(key_status)
    return key_status
}





fun send_to_valve(mac:String?,subtype:String?,status:String?,myip:String,ip:String?){
//    //UdpListener8089.pause()
    var targetIP=ip

    val message = "cmnd~>$mac~>Valv~>$subtype~>$status~>0000~>$myip"
    val serverAddress = InetAddress.getByName(targetIP)
    val serverPort = 8089

    val socket = DatagramSocket()
    val sendData = message.toByteArray()
    val sendPacket = DatagramPacket(sendData, sendData.size, serverAddress, serverPort)
    socket.send(sendPacket)
    socket.close()

//    //    UdpListener8089.resume()
}





fun udp_valve (context: Context, valve: valve?) :Boolean{

    //UdpListener8089.pause()
    var pass=false
    var is_ok=false
    var isResponseReceivedFirstTime = false


    val timeoutMillis = 1000
    var loopCount = 0

    do {


        if (loopCount == 0) {
            if (!isResponseReceivedFirstTime){

                val valve_database= valve_db.getInstance(context)
                println(valve!!.status)
                if(valve_database.getStatusById(valve!!.id)=="1"){

                    valve_database.updateStatusbyId(valve.id, "0")

                }else{
                    valve_database.updateStatusbyId(valve.id, "1")

                }
            }

            val valve_database= valve_db.getInstance(context)
//            send_to_valve(valve!!.mac,valve!!.subtype,
//                valve_sub_type_coder(context,valve.mac), checkIP(context),valve_database.get_from_db_valve(valve.id)!!.ip)
            println("direct IP sent")




            receiveUdpMessage_send_inside_valve_cmnd({ receivedMessage ->
                if (receivedMessage=="failed"){
                    println("direct IP failed..")
                    pass=true

                }else{
                    pass=true
                    if ((!receivedMessage.startsWith("cmnd"))&&(!receivedMessage.startsWith("rfsh")) ){
                        println("direct IP success..")
                        is_ok=true
                        isResponseReceivedFirstTime = true
                        val valve_database2= valve_db.getInstance(context)
                        var receivedmessage_decoded= extract_response(receivedMessage)
                        var  macip=receivedmessage_decoded[0]
                        var  pole_num=receivedmessage_decoded[1]
                        var  key_status=receivedmessage_decoded[2]
                        var ip =receivedmessage_decoded[3]

//                    var statusList=light_sub_type_decoder(key_status,pole_num)

                        val valve_database = valve_db.getInstance(context)
                        val same_macs = valve_database.getvalvesByMacAddress(valve?.mac).sortedBy { it!!.subtype!!.toInt() }
                        var index=0
//                    for (valve_same_mac in same_macs){
//                        valve_database2.updateStatusbyId(valve_same_mac!!.id,statusList[index])
//                        index+=1
//
//                    }

                    }


                }



            }, 8089, 700,valve!!.mac,valve!!.subtype,valve_sub_type_coder(context,valve.mac), checkIP(context),valve_database.get_from_db_valve(valve.id)!!.ip)






        }else if (loopCount == 1 && !isResponseReceivedFirstTime) {
            println("broadcasting..")
            val valve_database= valve_db.getInstance(context)
            val broadcastIP= convertIpToBroadcast(context)
//            send_to_valve(valve!!.mac,valve.subtype,
//                valve_sub_type_coder(context,valve.mac), checkIP(context),broadcastIP)
            println("broadcast sent")
            var isrecived=false
            receiveUdpMessage_send_inside_valve_cmnd_broadcast({ receivedMessage ->
                if (receivedMessage=="failed"){
                    println("broadcast failed..")
                    pass=true
                    if (valve != null) {
                        if(valve_database.getStatusById(valve.id)=="1"){

                            valve_database.updateStatusbyId(valve.id, "0")

                        }else{
                            valve_database.updateStatusbyId(valve.id, "1")

                        }
                    }
                    println("Broadcast failed..")

                }else{
                    pass=true
                    if ((!receivedMessage.startsWith("cmnd"))&&(!receivedMessage.startsWith("rfsh")) ){
                        println("success..")
                        is_ok=true
                        var receivedmessage_decoded= extract_response(receivedMessage)
                        var  macip=receivedmessage_decoded[0]
                        var  pole_num=receivedmessage_decoded[1]
                        var  key_status=receivedmessage_decoded[2]
                        var ip =receivedmessage_decoded[3]
                        val light_database3= valve_db.getInstance(context)
                        val same_macs = light_database3.getvalvesByMacAddress(valve?.mac).sortedBy { it!!.subtype!!.toInt() }

//                    var statusList=light_sub_type_decoder(key_status,pole_num)
//                    var index=0
                        for (light_same_mac3 in same_macs){
                            light_database3.updatevalvebyId(light_same_mac3!!.id , ip)
//                        light_database3.updateStatusById(light_same_mac3!!.id,statusList[index])
//                        index+=1
                        }

                    }else{
                        println("Broadcast failed..")

                    }

                }


            }, 8089, 700,valve!!.mac,valve.subtype,valve_sub_type_coder(context,valve.mac), checkIP(context),broadcastIP)


        }

        if (!is_ok){

            loopCount++
        }else{
            break
        }

        // تاخیر یک ثانیه قبل از ورود به دور جدید از حلقه
        if (!pass){
            Thread.sleep(700)

        }
    } while (loopCount < 2) // تکرار حلقه تا دو بار اجرا

    //    UdpListener8089.resume()

    return is_ok

}

fun udp_valve_for_scenario (context: Context, valve_list: List<valve>) :Boolean{
    //UdpListener8089.pause()
    var is_succes=false
    var isResponseReceivedFirstTime = false
    var pass = false

    val timeoutMillis = 700
    var loopCount = 0

    do {


        if (loopCount == 0) {
            if (!isResponseReceivedFirstTime){

                val valve_database= valve_db.getInstance(context)

                for (valve in valve_list){
                    if(valve.status=="1"){

                        valve_database.updateStatusbyId(valve.id, "1")

                    }else{
                        valve_database.updateStatusbyId(valve.id, "0")

                    }


                }

            }

            val valve_database= valve_db.getInstance(context)
//            send_to_valve(valve_list[0]!!.mac,valve_list[0]!!.subtype,valve_sub_type_coder(context,valve_list[0].mac), checkIP(context),valve_database.get_from_db_valve(valve_list[0].id)!!.ip)
            println("direct IP sent")




            receiveUdpMessage_send_inside_valve_cmnd({ receivedMessage ->
                if (receivedMessage=="failed"){
                    println("direct IP failed..")
                    pass = true
                    is_succes=false

                }else{
                    pass = true
                    if ((!receivedMessage.startsWith("cmnd"))&&(!receivedMessage.startsWith("rfsh")) ){
                        println("direct IP success..")
                        is_succes=true
                        isResponseReceivedFirstTime = true
                        val valve_database2= valve_db.getInstance(context)
                        var receivedmessage_decoded= extract_response(receivedMessage)
                        var  macip=receivedmessage_decoded[0]
                        var  pole_num=receivedmessage_decoded[1]
                        var  key_status=receivedmessage_decoded[2]
                        var ip =receivedmessage_decoded[3]

//                    var statusList=light_sub_type_decoder(key_status,pole_num)

                        val valve_database = valve_db.getInstance(context)
                        val same_macs = valve_database.getvalvesByMacAddress(valve_list[0].mac).sortedBy { it!!.subtype!!.toInt() }
                        var index=0
//                    for (valve_same_mac in same_macs){
//                        valve_database2.updateStatusbyId(valve_same_mac!!.id,statusList[index])
//                        index+=1
//
//                    }

                    }


                }



            }, 8089, 700,valve_list[0]!!.mac,valve_list[0]!!.subtype,valve_sub_type_coder(context,valve_list[0].mac), checkIP(context),valve_database.get_from_db_valve(valve_list[0].id)!!.ip)






        }else if (loopCount == 1 && !isResponseReceivedFirstTime) {
            println("broadcasting..")
            val valve_database= valve_db.getInstance(context)
            val broadcastIP= convertIpToBroadcast(context)
//            send_to_valve(valve_list[0]!!.mac,valve_list[0].subtype,
//                valve_sub_type_coder(context,valve_list[0].mac), checkIP(context),broadcastIP)
//            send_to_valve(valve_list[0]!!.mac,valve_list[0].subtype,
//                valve_sub_type_coder(context,valve_list[0].mac), checkIP(context),broadcastIP)
//            send_to_valve(valve_list[0]!!.mac,valve_list[0].subtype,
//                valve_sub_type_coder(context,valve_list[0].mac), checkIP(context),broadcastIP)
//            send_to_valve(valve_list[0]!!.mac,valve_list[0].subtype,
//                valve_sub_type_coder(context,valve_list[0].mac), checkIP(context),broadcastIP)

            println("broadcast sent")
            var isrecived=false
            receiveUdpMessage_send_inside_valve_cmnd_broadcast({ receivedMessage ->
                if (receivedMessage=="failed"){
                    pass = true
                    println("broadcast failed..")
                    is_succes=false
//                    for (valve in valve_list){
//                        if(valve_database.getStatusById(valve!!.id)=="1"){
//
//                            valve_database.updateStatusbyId(valve.id, "0")
//
//                        }else{
//                            valve_database.updateStatusbyId(valve.id, "1")
//
//                        }
//
//
//                    }

                    println("Broadcast failed..")

                }else{
                    pass = true

                    if ((!receivedMessage.startsWith("cmnd"))&&(!receivedMessage.startsWith("rfsh")) ){
                        println("success..")
                        is_succes=true
                        var receivedmessage_decoded= extract_response(receivedMessage)
                        var  macip=receivedmessage_decoded[0]
                        var  pole_num=receivedmessage_decoded[1]
                        var  key_status=receivedmessage_decoded[2]
                        var ip =receivedmessage_decoded[3]
                        val light_database3= valve_db.getInstance(context)
                        val same_macs = light_database3.getvalvesByMacAddress(valve_list[0].mac).sortedBy { it!!.subtype!!.toInt() }

//                    var statusList=light_sub_type_decoder(key_status,pole_num)
//                    var index=0
                        for (light_same_mac3 in same_macs){
                            light_database3.updatevalvebyId(light_same_mac3!!.id , ip)
//                        light_database3.updateStatusById(light_same_mac3!!.id,statusList[index])
//                        index+=1
                        }

                    }else{
                        println("Broadcast failed..")

                    }

                }


            }, 8089, 700,valve_list[0]!!.mac,valve_list[0].subtype,valve_sub_type_coder(context,valve_list[0].mac), checkIP(context),broadcastIP)


        }

        if (!is_succes){

            loopCount++
        }else{
            break
        }

        // تاخیر یک ثانیه قبل از ورود به دور جدید از حلقه
        if (!pass){
            Thread.sleep(700)

        }

    } while (loopCount < 2) // تکرار حلقه تا دو بار اجرا

    //    UdpListener8089.resume()

    return is_succes
}


fun refresh_valve(context: Fragment, valve: valve, rooms: rooms?):Boolean{
    //UdpListener8089.pause()
    var pass = false
    var is_ok=false
    val valve_database1= valve_db.getInstance(context.requireContext())
    val valve_list=valve_database1.getvalvesWithNonEmptyMacByRoomName2(rooms!!.room_name)
    for (same_mac_list in valve_list) {
        var isResponseReceivedFirstTime = false
        var loopCount = 0

        do {


            if (loopCount == 0) {


//                send_refresh(valve.mac, checkIP(context.requireContext()),valve.ip)


                println("direct IP sent")




                valve.ip?.let {
                    valve.mac?.let { it1 ->
                        receiveUdpMessage_send_inside({ receivedMessage ->
                            if (receivedMessage=="failed"){
                                println("direct IP failed..")
                                pass = true

                            }else{
                                pass = true
                                if ((!receivedMessage.startsWith("cmnd"))&&(!receivedMessage.startsWith("rfsh")) ){
                                    println("direct IP success..")
                                    is_ok=true
                                    isResponseReceivedFirstTime = true
                                    val valve_db= valve_db.getInstance(context.requireContext())
                                    var receivedmessage_decoded= extract_response(receivedMessage)
                                    var  macip=receivedmessage_decoded[0]
                                    var  pole_num=receivedmessage_decoded[1]
                                    var  key_status=receivedmessage_decoded[2]
                                    var ip =receivedmessage_decoded[3]

                                    var statusList= plug_sub_type_decoder(key_status,valve_db.getvalvesByMacAddress(macip)[valve_db.getvalvesByMacAddress(macip).count()-1]!!.subtype.toString())


                                    val same_macs = valve_db.getvalvesByMacAddress(same_mac_list[0]!!.mac).sortedBy { it!!.subtype!!.toInt() }
                                    var index=0
                                    for (lightt in same_macs){
                                        valve_database1.updateStatusbyId(lightt!!.id,statusList[0+index])
                                        index+=1

                                    }


                                }else{
                                    println("direct IP failed..")
                                }


                            }


                        }, 8089, 700, it, it1,checkIP(context.requireContext()))
                    }
                }






            }else if (loopCount == 1 && !isResponseReceivedFirstTime) {
                println("broadcasting..")


                val broadcastIP= convertIpToBroadcast(context.requireContext())
//                send_refresh(valve.mac, checkIP(context.requireContext()),broadcastIP)


                println("broadcast sent")


                valve.mac?.let {
                    receiveUdpMessage_send_inside_brodcast({ receivedMessage ->
                        if (receivedMessage=="failed"){
                            println("broadcast failed..")
                            pass = true


                        }else{
                            pass = true
                            if ((!receivedMessage.startsWith("cmnd"))&&(!receivedMessage.startsWith("rfsh")) ){
                                is_ok=true
                                println("success..")
                                val valve_db= valve_db.getInstance(context.requireContext())
                                var receivedmessage_decoded= extract_response(receivedMessage)
                                var  macip=receivedmessage_decoded[0]
                                var  pole_num=receivedmessage_decoded[1]
                                var  key_status=receivedmessage_decoded[2]
                                var new_ip =receivedmessage_decoded[3]


                                var statusList= plug_sub_type_decoder(key_status,valve_db.getvalvesByMacAddress(macip)[valve_db.getvalvesByMacAddress(macip).count()-1]!!.subtype.toString())

                                val same_macs = valve_db.getvalvesByMacAddress(same_mac_list[0]!!.mac).sortedBy { it!!.subtype!!.toInt() }
                                var index=0
                                for (lightt in same_macs){
                                    valve_database1.updateStatusAndIpById(lightt!!.id,statusList[0+index],new_ip)
                                    index+=1

                                }


                            }else{

                                println("broadcast failed..")

                            }


                        }


                    }, 8089, 700,broadcastIP, it,checkIP(context.requireContext()))
                }

            }


            if (!is_ok){

                loopCount++
            }else{
                break
            }
            // تاخیر یک ثانیه قبل از ورود به دور جدید از حلقه
            if (!pass){
                Thread.sleep(700)


            }
        } while (loopCount < 2)



    }

    //    UdpListener8089.resume()
    return is_ok
    // تکرار حلقه تا دو بار اجرا



}

fun refresh_valve_for_scenario(context: Context, valve_list: List<valve>): ArrayList<String> {
    //UdpListener8089.pause()
    val ok_to_send= arrayListOf<String>()
    val valve_database1= valve_db.getInstance(context)

    for (same_mac_list in valve_list) {
        var isResponseReceivedFirstTime = false
        var loopCount = 0

        do {


            if (loopCount == 0) {


                send_refresh(same_mac_list.mac, checkIP(context),same_mac_list.ip)


                println("direct IP sent")




                receiveUdpMessage({ receivedMessage ->
                    if (receivedMessage=="failed"){
                        println("direct IP failed..")

                    }else{
                        if ((!receivedMessage.startsWith("cmnd"))&&(!receivedMessage.startsWith("rfsh")) ){
                            println("direct IP success..")
                            same_mac_list.mac?.let { ok_to_send.add(it) }
                            isResponseReceivedFirstTime = true
                            val valve_db= valve_db.getInstance(context)
                            var receivedmessage_decoded= extract_response(receivedMessage)
                            var  macip=receivedmessage_decoded[0]
                            var  pole_num=receivedmessage_decoded[1]
                            var  key_status=receivedmessage_decoded[2]
                            var ip =receivedmessage_decoded[3]

                            var statusList= plug_sub_type_decoder(key_status,valve_db.getvalvesByMacAddress(macip)[valve_db.getvalvesByMacAddress(macip).count()-1]!!.subtype.toString())


                            val same_macs = valve_db.getvalvesByMacAddress(same_mac_list!!.mac).sortedBy { it!!.subtype!!.toInt() }
                            var index=0
                            for (lightt in same_macs){
                                valve_database1.updateStatusbyId(lightt!!.id,statusList[0+index])
                                index+=1

                            }




                        }else{
                            println("direct IP failed..")
                        }



                    }



                }, 8089, 700)






            }else if (loopCount == 1 && !isResponseReceivedFirstTime) {
                println("broadcasting..")


                val broadcastIP= convertIpToBroadcast(context)
                send_refresh(same_mac_list.mac, checkIP(context),broadcastIP)


                println("broadcast sent")


                receiveUdpMessage({ receivedMessage ->
                    if (receivedMessage=="failed"){
                        println("broadcast failed..")




                    }else{
                        if ((!receivedMessage.startsWith("cmnd"))&&(!receivedMessage.startsWith("rfsh")) ){
                            same_mac_list.mac?.let { ok_to_send.add(it) }
                            println("success..")
                            val valve_db= valve_db.getInstance(context)
                            var receivedmessage_decoded= extract_response(receivedMessage)
                            var  macip=receivedmessage_decoded[0]
                            var  pole_num=receivedmessage_decoded[1]
                            var  key_status=receivedmessage_decoded[2]
                            var new_ip =receivedmessage_decoded[3]



                            var statusList= plug_sub_type_decoder(key_status,valve_db.getvalvesByMacAddress(macip)[valve_db.getvalvesByMacAddress(macip).count()-1]!!.subtype.toString())

                            val same_macs = valve_db.getvalvesByMacAddress(same_mac_list!!.mac).sortedBy { it!!.subtype!!.toInt() }
                            var index=0
                            for (lightt in same_macs){
                                valve_database1.updateStatusAndIpById(lightt!!.id,statusList[0+index],new_ip)
                                index+=1

                            }


                        }else{

                            println("broadcast failed..")

                        }


                    }


                }, 8089, 700)


            }

            loopCount++

            // تاخیر یک ثانیه قبل از ورود به دور جدید از حلقه
            Thread.sleep(700)
        } while (loopCount < 2)




    }
    //    UdpListener8089.resume()

    return ok_to_send
    // تکرار حلقه تا دو بار اجرا



}


fun fan_sub_type_coder(context: Context, mac: String?): String {
    val fan_database = fan_db.getInstance(context)
    val same_macs = mac?.let { fan_database.getfansByMacAddress(it) }
    var key_status = "----------------"

    if (same_macs != null) {
        var pole_count=0
        val first_plug=same_macs.sortedBy { it?.subtype }
        for (fan in same_macs) {
            val pole = fan?.subtype?.toInt() ?: 0
            if (fan?.status == "1") {
                println("on")
                if (key_status.length >= pole) {
                    val startIndex :Int
                    if (first_plug[0!!]!!.subtype == "0000"){
                        startIndex = pole

                    }else{
                        startIndex = pole-1

                    }
                    val endIndex = startIndex + 1
                    val newChar = "1"
                    key_status = key_status.substring(0, startIndex) + newChar + key_status.substring(endIndex)
                }
            }else if (fan?.status == "0") {
                println("off")
                if (key_status.length >= pole) {
                    val startIndex :Int
                    if (first_plug[0!!]!!.subtype == "0000"){
                        startIndex = pole

                    }else{
                        startIndex = pole-1

                    }
                    val endIndex = startIndex + 1
                    val newChar = "0"
                    key_status = key_status.substring(0, startIndex) + newChar + key_status.substring(endIndex)
                }
            }
            if (fan!!.subtype!!.toInt()>pole_count ){
                pole_count=fan.subtype!!.toInt()

            }

        }
        println(pole_count)
        println(key_status)
        key_status="${key_status.substring(0,pole_count)}${key_status.substring(pole_count).replace("0","-")}"

    }
    println(key_status)
    return key_status
}





fun send_to_fan(mac:String?,subtype:String?,status:String?,myip:String,ip:String?){
//    //UdpListener8089.pause()
    var targetIP=ip

    val message = "cmnd~>$mac~>Fano~>$subtype~>$status~>0000~>$myip"
    val serverAddress = InetAddress.getByName(targetIP)
    val serverPort = 8089

    val socket = DatagramSocket()
    val sendData = message.toByteArray()
    val sendPacket = DatagramPacket(sendData, sendData.size, serverAddress, serverPort)
    socket.send(sendPacket)
    socket.close()
//    //    UdpListener8089.resume()

}





fun udp_fan (context: Context, fan: fan?):Boolean{
    //UdpListener8089.pause()
    var pass=false
    var is_ok=false
    var isResponseReceivedFirstTime = false


    val timeoutMillis = 1000
    var loopCount = 0

    do {


        if (loopCount == 0) {
            if (!isResponseReceivedFirstTime){

                val fan_database= fan_db.getInstance(context)
                println(fan!!.status)
                if(fan_database.getStatusById(fan!!.id)=="1"){

                    fan_database.updateStatusbyId(fan.id, "0")

                }else{
                    fan_database.updateStatusbyId(fan.id, "1")

                }
            }

            val fan_database= fan_db.getInstance(context)
            send_to_fan(fan!!.mac,fan!!.subtype, fan_sub_type_coder(context,fan.mac), checkIP(context),fan_database.get_from_db_fan(fan.id)!!.ip)
//            send_to_fan(fan!!.mac,fan!!.subtype, fan_sub_type_coder(context,fan.mac), checkIP(context),fan_database.get_from_db_fan(fan.id)!!.ip)
            println("direct IP sent")




            receiveUdpMessage({ receivedMessage ->
                if (receivedMessage=="failed"){
                    println("direct IP failed..")
                    pass=true

                }else{
                    pass=true
                    if ((!receivedMessage.startsWith("cmnd"))&&(!receivedMessage.startsWith("rfsh")) ){
                        is_ok=true
                        println("direct IP success..")
                        isResponseReceivedFirstTime = true
                        val fan_database2= fan_db.getInstance(context)
                        var receivedmessage_decoded= extract_response(receivedMessage)
                        var  macip=receivedmessage_decoded[0]
                        var  pole_num=receivedmessage_decoded[1]
                        var  key_status=receivedmessage_decoded[2]
                        var ip =receivedmessage_decoded[3]

//                    var statusList=light_sub_type_decoder(key_status,pole_num)

                        val fan_database = fan_db.getInstance(context)
                        val same_macs = fan_database.getfansByMacAddress(fan.mac).sortedBy { it!!.subtype!!.toInt() }
                        var index=0
//                    for (fan_same_mac in same_macs){
//                        fan_database2.updateStatusbyId(fan_same_mac!!.id,statusList[index])
//                        index+=1
//
//                    }

                    }


                }



            }, 8089, 1000)






        }else if (loopCount == 1 && !isResponseReceivedFirstTime) {
            println("broadcasting..")
            val fan_database= fan_db.getInstance(context)
            val broadcastIP= convertIpToBroadcast(context)
            send_to_fan(fan!!.mac,fan.subtype,
                fan_sub_type_coder(context,fan.mac), checkIP(context),broadcastIP)
            println("broadcast sent")
            var isrecived=false
            receiveUdpMessage({ receivedMessage ->
                if (receivedMessage=="failed"){
                    println("broadcast failed..")
                    pass=true
                    if(fan_database.getStatusById(fan.id)=="1"){

                        fan_database.updateStatusbyId(fan.id, "0")

                    }else{
                        fan_database.updateStatusbyId(fan.id, "1")

                    }
                    println("Broadcast failed..")

                }else{
                    pass=true

                    if ((!receivedMessage.startsWith("cmnd"))&&(!receivedMessage.startsWith("rfsh")) ){
                        println("success..")
                        is_ok=true
                        var receivedmessage_decoded= extract_response(receivedMessage)
                        var  macip=receivedmessage_decoded[0]
                        var  pole_num=receivedmessage_decoded[1]
                        var  key_status=receivedmessage_decoded[2]
                        var ip =receivedmessage_decoded[3]
                        val light_database3= fan_db.getInstance(context)
                        val same_macs = light_database3.getfansByMacAddress(fan.mac).sortedBy { it!!.subtype!!.toInt() }

//                    var statusList=light_sub_type_decoder(key_status,pole_num)
//                    var index=0
                        for (light_same_mac3 in same_macs){
                            light_database3.updatefanbyId(light_same_mac3!!.id , ip)
//                        light_database3.updateStatusById(light_same_mac3!!.id,statusList[index])
//                        index+=1
                        }

                    }else{
                        println("Broadcast failed..")

                    }

                }


            }, 8089, 1000)


        }

        if (!is_ok){

            loopCount++
        }else{
            break
        }

        // تاخیر یک ثانیه قبل از ورود به دور جدید از حلقه
        if (!pass){
            Thread.sleep(1000)

        }
    } while (loopCount < 2) // تکرار حلقه تا دو بار اجرا

    //    UdpListener8089.resume()
    return is_ok

}

fun udp_fan_for_scenario (context: Context, fan_list: List<fan>):Boolean{
    //UdpListener8089.pause()
    var is_ok=false
    var isResponseReceivedFirstTime = false
    var pass=false

    val timeoutMillis = 1000
    var loopCount = 0

    do {


        if (loopCount == 0) {
            if (!isResponseReceivedFirstTime){

                val fan_database= fan_db.getInstance(context)
//                println(fan!!.status)
                if (fan_list != null) {
                    for (fan in fan_list){

                        if(fan.status=="1"){

                            fan_database.updateStatusbyId(fan.id, "1")

                        }else{
                            fan_database.updateStatusbyId(fan.id, "0")

                        }
                    }
                }

            }

            val fan_database= fan_db.getInstance(context)
//            send_to_fan(fan_list[0]!!.mac,fan_list[0]!!.subtype, fan_sub_type_coder(context,fan_list[0].mac), checkIP(context),fan_database.get_from_db_fan(fan_list[0].id)!!.ip)
            println("direct IP sent")




            receiveUdpMessage_send_inside_fan_cmnd({ receivedMessage ->
                if (receivedMessage=="failed"){
                    println("direct IP failed..")
                    pass=true
                }else{
                    if ((!receivedMessage.startsWith("cmnd"))&&(!receivedMessage.startsWith("rfsh")) ){
                        println("direct IP success..")
                        pass=true
                        is_ok=true
                        isResponseReceivedFirstTime = true
                        val fan_database2= fan_db.getInstance(context)
                        var receivedmessage_decoded= extract_response(receivedMessage)
                        var  macip=receivedmessage_decoded[0]
                        var  pole_num=receivedmessage_decoded[1]
                        var  key_status=receivedmessage_decoded[2]
                        var ip =receivedmessage_decoded[3]

//                    var statusList=light_sub_type_decoder(key_status,pole_num)

                        val fan_database = fan_db.getInstance(context)
                        val same_macs = fan_database.getfansByMacAddress(fan_list[0].mac).sortedBy { it!!.subtype!!.toInt() }
                        var index=0
//                    for (fan_same_mac in same_macs){
//                        fan_database2.updateStatusbyId(fan_same_mac!!.id,statusList[index])
//                        index+=1
//
//                    }

                    }


                }



            }, 8089, 700,fan_list[0]!!.mac,fan_list[0]!!.subtype, fan_sub_type_coder(context,fan_list[0].mac), checkIP(context),fan_database.get_from_db_fan(fan_list[0].id)!!.ip)






        }else if (loopCount == 1 && !isResponseReceivedFirstTime) {
            println("broadcasting..")
            val fan_database= fan_db.getInstance(context)
            val broadcastIP= convertIpToBroadcast(context)
//            send_to_fan(fan_list[0]!!.mac,fan_list[0].subtype,
//                fan_sub_type_coder(context,fan_list[0].mac), checkIP(context),broadcastIP)
//            send_to_fan(fan_list[0]!!.mac,fan_list[0].subtype,
//                fan_sub_type_coder(context,fan_list[0].mac), checkIP(context),broadcastIP)
//            send_to_fan(fan_list[0]!!.mac,fan_list[0].subtype,
//                fan_sub_type_coder(context,fan_list[0].mac), checkIP(context),broadcastIP)
//            send_to_fan(fan_list[0]!!.mac,fan_list[0].subtype,
//                fan_sub_type_coder(context,fan_list[0].mac), checkIP(context),broadcastIP)
            println("broadcast sent")
            var isrecived=false
            receiveUdpMessage_send_inside_fan_cmnd_broadcast({ receivedMessage ->
                if (receivedMessage=="failed"){
                    println("broadcast failed..")
                    pass=true

                    println("Broadcast failed..")

                }else{
                    pass=true

                    if ((!receivedMessage.startsWith("cmnd"))&&(!receivedMessage.startsWith("rfsh")) ){
                        println("success..")
                        is_ok=true
                        var receivedmessage_decoded= extract_response(receivedMessage)
                        var  macip=receivedmessage_decoded[0]
                        var  pole_num=receivedmessage_decoded[1]
                        var  key_status=receivedmessage_decoded[2]
                        var ip =receivedmessage_decoded[3]
                        val light_database3= fan_db.getInstance(context)
                        val same_macs = light_database3.getfansByMacAddress(fan_list[0].mac).sortedBy { it!!.subtype!!.toInt() }

//                    var statusList=light_sub_type_decoder(key_status,pole_num)
//                    var index=0
                        for (light_same_mac3 in same_macs){
                            light_database3.updatefanbyId(light_same_mac3!!.id , ip)
//                        light_database3.updateStatusById(light_same_mac3!!.id,statusList[index])
//                        index+=1
                        }

                    }else{
                        println("Broadcast failed..")

                    }

                }


            }, 8089, 700,fan_list[0]!!.mac,fan_list[0].subtype,fan_sub_type_coder(context,fan_list[0].mac), checkIP(context),broadcastIP)


        }

        if (!is_ok){

            loopCount++
        }else{
            break
        }

        // تاخیر یک ثانیه قبل از ورود به دور جدید از حلقه
        if (!pass){
            Thread.sleep(700)
        }

    } while (loopCount < 2) // تکرار حلقه تا دو بار اجرا
    //    UdpListener8089.resume()
    return is_ok

}

fun refresh_fan(context: Fragment, fan: fan, rooms: rooms?):Boolean{
    //UdpListener8089.pause()
    var pass =false
    var is_ok= false
    val fan_database1= fan_db.getInstance(context.requireContext())
    val fan_list= mutableListOf<fan>()
    fan_list.add(fan)
    for (same_mac_list in fan_list) {
        var isResponseReceivedFirstTime = false
        var loopCount = 0

        do {


            if (loopCount == 0) {


                send_refresh(fan.mac, checkIP(context.requireContext()),fan.ip)
//                send_refresh(fan.mac, checkIP(context.requireContext()),fan.ip)


                println("direct IP sent")




                receiveUdpMessage({ receivedMessage ->
                    if (receivedMessage=="failed"){
                        println("direct IP failed..")
                        pass =true

                    }else{
                        pass =true
                        if (((!receivedMessage.startsWith("cmnd"))&&(!receivedMessage.startsWith("rfsh")))|| ((!receivedMessage.startsWith("cmnd"))&&(!receivedMessage.startsWith("fdbk"))) ){
                            println("direct IP success..")
                            is_ok= true
                            isResponseReceivedFirstTime = true
                            val fan_db= fan_db.getInstance(context.requireContext())
                            var receivedmessage_decoded= extract_response(receivedMessage)
                            var  macip=receivedmessage_decoded[0]
                            var  pole_num=receivedmessage_decoded[1]
                            var  key_status=receivedmessage_decoded[2]
                            var ip =receivedmessage_decoded[3]

                            var statusList= plug_sub_type_decoder(key_status,fan_db.getfansByMacAddress(macip)[fan_db.getfansByMacAddress(macip).count()-1]!!.subtype.toString())

                            val same2 = light_db.getInstance(context.requireContext()).getLightsByMacAddress(macip).count()
                            if (same2==1){
                                statusList.removeAt(0)
                                println("yekiiiiiiii")

                            }else if (same2 == 2 ){
                                println("dotaaaaaa")
                                statusList.removeAt(0)
                                statusList.removeAt(0)
                            }else if (same2 == 3 ){
                                println("setaaaaaa")
                                statusList.removeAt(0)
                                statusList.removeAt(0)
                                statusList.removeAt(0)
                            }

                            println("                "+statusList)

                            val same_macs = fan_db.getfansByMacAddress(same_mac_list!!.mac).sortedBy { it!!.subtype!!.toInt() }
                            println(same_macs[0]!!.Fname)
                            var index=0
                            for (lightt in same_macs){
                                fan_database1.updateStatusbyId(lightt!!.id,statusList[0+index])
                                index+=1

                            }




                        }else{
                            println("direct IP failed..")
                        }



                    }



                }, 8089, 1000)






            }else if (loopCount == 1 && !isResponseReceivedFirstTime) {
                println("broadcasting..")


                val broadcastIP= convertIpToBroadcast(context.requireContext())
                send_refresh(fan.mac, checkIP(context.requireContext()),broadcastIP)


                println("broadcast sent")


                receiveUdpMessage({ receivedMessage ->
                    if (receivedMessage=="failed"){
                        println("broadcast failed..")
                        pass =true




                    }else{
                        pass =true
                        if ((!receivedMessage.startsWith("cmnd"))&&(!receivedMessage.startsWith("rfsh")) ){
                            is_ok= true
                            println("success..")
                            val fan_db= fan_db.getInstance(context.requireContext())
                            var receivedmessage_decoded= extract_response(receivedMessage)
                            var  macip=receivedmessage_decoded[0]
                            var  pole_num=receivedmessage_decoded[1]
                            var  key_status=receivedmessage_decoded[2]
                            var new_ip =receivedmessage_decoded[3]



                            var statusList= plug_sub_type_decoder(key_status,fan_db.getfansByMacAddress(macip)[fan_db.getfansByMacAddress(macip).count()-1]!!.subtype.toString())

                            val same_macs = fan_db.getfansByMacAddress(same_mac_list!!.mac).sortedBy { it!!.subtype!!.toInt() }
                            var index=0
                            for (lightt in same_macs){
                                fan_database1.updateStatusAndIpById(lightt!!.id,statusList[0+index],new_ip)
                                index+=1

                            }


                        }else{

                            println("broadcast failed..")


                        }


                    }


                }, 8089, 1000)


            }

            if (!is_ok){

                loopCount++
            }else{
                break
            }

            // تاخیر یک ثانیه قبل از ورود به دور جدید از حلقه
            if (!pass){

                Thread.sleep(1000)

            }
        } while (loopCount < 2)



    }



    //    UdpListener8089.resume()
    return is_ok

}

fun refresh_fan_time(context: Context, fan: fan):Boolean{
    //UdpListener8089.pause()
    var pass =false
    var is_ok= false
    val fan_database1= fan_db.getInstance(context)
    val fan_list= mutableListOf<fan>()
    fan_list.add(fan)
    for (same_mac_list in fan_list) {
        var isResponseReceivedFirstTime = false
        var loopCount = 0

        do {


            if (loopCount == 0) {


                send_refresh(fan.mac, checkIP(context),fan.ip)
//                send_refresh(fan.mac, checkIP(context.requireContext()),fan.ip)


                println("direct IP sent")




                receiveUdpMessage({ receivedMessage ->
                    if (receivedMessage=="failed"){
                        println("direct IP failed..")
                        pass =true

                    }else{
                        pass =true
                        if (((!receivedMessage.startsWith("cmnd"))&&(!receivedMessage.startsWith("rfsh")))|| ((!receivedMessage.startsWith("cmnd"))&&(!receivedMessage.startsWith("fdbk"))) ){
                            println("direct IP success..")
                            is_ok= true
                            isResponseReceivedFirstTime = true
                            val fan_db= fan_db.getInstance(context)
                            var receivedmessage_decoded= extract_response(receivedMessage)
                            var  macip=receivedmessage_decoded[0]
                            var  pole_num=receivedmessage_decoded[1]
                            var  key_status=receivedmessage_decoded[2]
                            var ip =receivedmessage_decoded[3]

                            var statusList= plug_sub_type_decoder(key_status,fan_db.getfansByMacAddress(macip)[fan_db.getfansByMacAddress(macip).count()-1]!!.subtype.toString())

                            val same2 = light_db.getInstance(context).getLightsByMacAddress(macip).count()
                            if (same2==1){
                                statusList.removeAt(0)

                            }else if (same2 == 2 ){
                                statusList.removeAt(0)
                                statusList.removeAt(0)
                            }else if (same2 == 3 ){
                                println("setaaaaaa")
                                statusList.removeAt(0)
                                statusList.removeAt(0)
                                statusList.removeAt(0)
                            }

                            println("                "+statusList)

                            val same_macs = fan_db.getfansByMacAddress(same_mac_list!!.mac).sortedBy { it!!.subtype!!.toInt() }
                            var index=0
                            for (lightt in same_macs){
                                fan_database1.updateStatusbyId(lightt!!.id,statusList[0+index])
                                index+=1

                            }




                        }else{
                            println("direct IP failed..")
                        }



                    }



                }, 8089, 1000)






            }else if (loopCount == 1 && !isResponseReceivedFirstTime) {
                println("broadcasting..")


                val broadcastIP= convertIpToBroadcast(context)
                send_refresh(fan.mac, checkIP(context),broadcastIP)


                println("broadcast sent")


                receiveUdpMessage({ receivedMessage ->
                    if (receivedMessage=="failed"){
                        println("broadcast failed..")
                        pass =true




                    }else{
                        pass =true
                        if ((!receivedMessage.startsWith("cmnd"))&&(!receivedMessage.startsWith("rfsh")) ){
                            is_ok= true
                            println("success..")
                            val fan_db= fan_db.getInstance(context)
                            var receivedmessage_decoded= extract_response(receivedMessage)
                            var  macip=receivedmessage_decoded[0]
                            var  pole_num=receivedmessage_decoded[1]
                            var  key_status=receivedmessage_decoded[2]
                            var new_ip =receivedmessage_decoded[3]



                            var statusList= plug_sub_type_decoder(key_status,fan_db.getfansByMacAddress(macip)[fan_db.getfansByMacAddress(macip).count()-1]!!.subtype.toString())

                            val same_macs = fan_db.getfansByMacAddress(same_mac_list!!.mac).sortedBy { it!!.subtype!!.toInt() }
                            var index=0
                            for (lightt in same_macs){
                                fan_database1.updateStatusAndIpById(lightt!!.id,statusList[0+index],new_ip)
                                index+=1

                            }


                        }else{

                            println("broadcast failed..")


                        }


                    }


                }, 8089, 1000)


            }

            if (!is_ok){

                loopCount++
            }else{
                break
            }

            // تاخیر یک ثانیه قبل از ورود به دور جدید از حلقه
            if (!pass){

                Thread.sleep(1000)

            }
        } while (loopCount < 2)



    }



    //    UdpListener8089.resume()
    return is_ok

}




fun refresh_fan_for_scenario(context: Fragment, fanlist: List<fan>): ArrayList<String>{
    //UdpListener8089.pause()

    val ok_to_send= arrayListOf<String>()
    var is_ok= false
    val fan_database1= fan_db.getInstance(context.requireContext())

    for (same_mac_list in fanlist) {
        var isResponseReceivedFirstTime = false
        var loopCount = 0

        do {


            if (loopCount == 0) {


                send_refresh(same_mac_list.mac, checkIP(context.requireContext()),same_mac_list.ip)


                println("direct IP sent")




                receiveUdpMessage({ receivedMessage ->
                    if (receivedMessage=="failed"){
                        println("direct IP failed..")

                    }else{
                        if ((!receivedMessage.startsWith("cmnd"))&&(!receivedMessage.startsWith("rfsh")) ){
                            println("direct IP success..")
                            same_mac_list.mac?.let { ok_to_send.add(it) }
                            is_ok= true
                            isResponseReceivedFirstTime = true
                            val fan_db= fan_db.getInstance(context.requireContext())
                            var receivedmessage_decoded= extract_response(receivedMessage)
                            var  macip=receivedmessage_decoded[0]
                            var  pole_num=receivedmessage_decoded[1]
                            var  key_status=receivedmessage_decoded[2]
                            var ip =receivedmessage_decoded[3]
                            println(key_status)

                            var statusList= plug_sub_type_decoder(key_status,fan_db.getfansByMacAddress(macip)[fan_db.getfansByMacAddress(macip).count()-1]!!.subtype.toString())

                            val same2 = light_db.getInstance(context.requireContext()).getLightsByMacAddress(macip).count()
                            if (same2==1){
                                statusList.removeAt(0)

                            }else if (same2 == 2 ){
                                statusList.removeAt(0)
                                statusList.removeAt(0)
                            }else if (same2 == 3 ){
                                println("setaaaaaa")
                                statusList.removeAt(0)
                                statusList.removeAt(0)
                                statusList.removeAt(0)
                            }

                            val same_macs = fan_db.getfansByMacAddress(same_mac_list!!.mac).sortedBy { it!!.subtype!!.toInt() }
                            println(same_macs)
                            var index=0
                            for (lightt in same_macs){
                                fan_database1.updateStatusbyId(lightt!!.id,statusList[0+index])
                                index+=1

                            }




                        }else{
                            println("direct IP failed..")
                        }



                    }



                }, 8089, 700)






            }else if (loopCount == 1 && !isResponseReceivedFirstTime) {
                println("broadcasting..")


                val broadcastIP= convertIpToBroadcast(context.requireContext())
                send_refresh(same_mac_list.mac, checkIP(context.requireContext()),broadcastIP)


                println("broadcast sent")


                receiveUdpMessage({ receivedMessage ->
                    if (receivedMessage=="failed"){
                        println("broadcast failed..")




                    }else{
                        if ((!receivedMessage.startsWith("cmnd"))&&(!receivedMessage.startsWith("rfsh")) ){
                            is_ok= true
                            println("success..")
                            same_mac_list.mac?.let { ok_to_send.add(it) }
                            val fan_db= fan_db.getInstance(context.requireContext())
                            var receivedmessage_decoded= extract_response(receivedMessage)
                            var  macip=receivedmessage_decoded[0]
                            var  pole_num=receivedmessage_decoded[1]
                            var  key_status=receivedmessage_decoded[2]
                            var new_ip =receivedmessage_decoded[3]



                            var statusList= plug_sub_type_decoder(key_status,fan_db.getfansByMacAddress(macip)[fan_db.getfansByMacAddress(macip).count()-1]!!.subtype.toString())

                            val same2 = light_db.getInstance(context.requireContext()).getLightsByMacAddress(macip).count()
                            if (same2==1){
                                statusList.removeAt(0)

                            }else if (same2 == 2 ){
                                statusList.removeAt(0)
                                statusList.removeAt(0)
                            }else if (same2 == 3 ){
                                println("setaaaaaa")
                                statusList.removeAt(0)
                                statusList.removeAt(0)
                                statusList.removeAt(0)
                            }
                            val same_macs = fan_db.getfansByMacAddress(same_mac_list!!.mac).sortedBy { it!!.subtype!!.toInt() }
                            var index=0
                            for (lightt in same_macs){
                                fan_database1.updateStatusAndIpById(lightt!!.id,statusList[0+index],new_ip)
                                index+=1

                            }


                        }else{

                            println("broadcast failed..")

                        }


                    }


                }, 8089, 700)


            }

            loopCount++

            // تاخیر یک ثانیه قبل از ورود به دور جدید از حلقه
            Thread.sleep(700)
        } while (loopCount < 2)



    }




    //    UdpListener8089.resume()
    return ok_to_send

}

fun udp_fan_sms (context: Context, fan: fan?,status: String?):Boolean{
    //UdpListener8089.pause()
    var pass=false
    var is_ok=false
    var isResponseReceivedFirstTime = false


    val timeoutMillis = 1000
    var loopCount = 0

    do {


        if (loopCount == 0) {
            if (!isResponseReceivedFirstTime){


            }

            val fan_database= fan_db.getInstance(context)
            if(status!!.length <=2){


                send_to_fan(fan!!.mac,fan!!.subtype, createStatusString(fan.subtype!!.toInt()-1 , status!!.toInt()) , checkIP(context),fan_database.get_from_db_fan(fan.id)!!.ip)
                println("direct IP sent")
            }else{

                send_to_fan(fan!!.mac,fan!!.subtype, status , checkIP(context),fan_database.get_from_db_fan(fan.id)!!.ip)
                println("direct IP sent")
            }




            receiveUdpMessage({ receivedMessage ->
                if (receivedMessage=="failed"){
                    println("direct IP failed..")
                    pass=true

                }else{
                    pass=true
                    if ((!receivedMessage.startsWith("cmnd"))&&(!receivedMessage.startsWith("rfsh")) ){
                        is_ok=true
                        println("direct IP success..")
                        isResponseReceivedFirstTime = true
                        val fan_database2= fan_db.getInstance(context)
                        var receivedmessage_decoded= extract_response(receivedMessage)
                        var  macip=receivedmessage_decoded[0]
                        var  pole_num=receivedmessage_decoded[1]
                        var  key_status=receivedmessage_decoded[2]
                        var ip =receivedmessage_decoded[3]

//                    var statusList=light_sub_type_decoder(key_status,pole_num)

                        val fan_database = fan_db.getInstance(context)
                        val same_macs = fan_database.getfansByMacAddress(fan.mac).sortedBy { it!!.subtype!!.toInt() }
                        var index=0
//                    for (fan_same_mac in same_macs){
//                        fan_database2.updateStatusbyId(fan_same_mac!!.id,statusList[index])
//                        index+=1
//
//                    }

                    }


                }



            }, 8089, 700)






        }else if (loopCount == 1 && !isResponseReceivedFirstTime) {
            println("broadcasting..")
            val fan_database= fan_db.getInstance(context)
            val broadcastIP= convertIpToBroadcast(context)

            if(status!!.length <=2){


                send_to_fan(fan!!.mac,fan.subtype,
                    createStatusString(fan.subtype!!.toInt()-1 , status!!.toInt()) , checkIP(context),broadcastIP)
                println("broadcast sent")
            }else{
                send_to_fan(fan!!.mac,fan.subtype,
                    status , checkIP(context),broadcastIP)
                println("broadcast sent")
            }
            var isrecived=false
            receiveUdpMessage({ receivedMessage ->
                if (receivedMessage=="failed"){
                    println("broadcast failed..")
                    pass=true

                    println("Broadcast failed..")

                }else{
                    pass=true

                    if ((!receivedMessage.startsWith("cmnd"))&&(!receivedMessage.startsWith("rfsh")) ){
                        println("success..")
                        is_ok=true
                        var receivedmessage_decoded= extract_response(receivedMessage)
                        var  macip=receivedmessage_decoded[0]
                        var  pole_num=receivedmessage_decoded[1]
                        var  key_status=receivedmessage_decoded[2]
                        var ip =receivedmessage_decoded[3]
                        val light_database3= fan_db.getInstance(context)
                        val same_macs = light_database3.getfansByMacAddress(fan.mac).sortedBy { it!!.subtype!!.toInt() }

//                    var statusList=light_sub_type_decoder(key_status,pole_num)
//                    var index=0
                        for (light_same_mac3 in same_macs){
                            light_database3.updatefanbyId(light_same_mac3!!.id , ip)
//                        light_database3.updateStatusById(light_same_mac3!!.id,statusList[index])
//                        index+=1
                        }

                    }else{
                        println("Broadcast failed..")

                    }

                }


            }, 8089, 700)


        }

        if (!is_ok){

            loopCount++
        }else{
            break
        }

        // تاخیر یک ثانیه قبل از ورود به دور جدید از حلقه
        if (!pass){
            Thread.sleep(700)

        }
    } while (loopCount < 2) // تکرار حلقه تا دو بار اجرا

    //    UdpListener8089.resume()
    return is_ok

}



fun udp_valve_sms (context: Context, valve: valve?,status: String?):Boolean{
    //UdpListener8089.pause()
    var pass=false
    var is_ok=false
    var isResponseReceivedFirstTime = false

    val valve_database=valve_db.getInstance(context)
    val same_macs = valve!!.mac?.let { valve_database.getvalvesByMacAddress(it) }
    var pole = valve?.subtype?.toInt() ?: 0

    if (same_macs!!.sortedBy { it!!.subtype?.toInt() }[0]!!.subtype!!.toInt() == 1) {
        pole -= 1
    }

    val timeoutMillis = 1000
    var loopCount = 0

    do {


        if (loopCount == 0) {
            if (!isResponseReceivedFirstTime){


            }

            val valve_database= valve_db.getInstance(context)
            if(status!!.length <=2){

                send_to_valve(valve!!.mac,valve!!.subtype, createStatusString(pole , status!!.toInt()) , checkIP(context),valve_database.get_from_db_valve(valve.id)!!.ip)
                println("direct IP sent")

            }else{

                send_to_valve(valve!!.mac,valve!!.subtype, status , checkIP(context),valve_database.get_from_db_valve(valve.id)!!.ip)
                println("direct IP sent")
            }




            receiveUdpMessage({ receivedMessage ->
                if (receivedMessage=="failed"){
                    println("direct IP failed..")
                    pass=true

                }else{
                    pass=true
                    if ((!receivedMessage.startsWith("cmnd"))&&(!receivedMessage.startsWith("rfsh")) ){
                        is_ok=true
                        println("direct IP success..")
                        isResponseReceivedFirstTime = true
                        val fan_database2= fan_db.getInstance(context)
                        var receivedmessage_decoded= extract_response(receivedMessage)
                        var  macip=receivedmessage_decoded[0]
                        var  pole_num=receivedmessage_decoded[1]
                        var  key_status=receivedmessage_decoded[2]
                        var ip =receivedmessage_decoded[3]

//                    var statusList=light_sub_type_decoder(key_status,pole_num)

                        val valve_database = valve_db.getInstance(context)
                        val same_macs = valve_database.getvalvesByMacAddress(valve.mac).sortedBy { it!!.subtype!!.toInt() }
                        var index=0
//                    for (fan_same_mac in same_macs){
//                        fan_database2.updateStatusbyId(fan_same_mac!!.id,statusList[index])
//                        index+=1
//
//                    }

                    }


                }



            }, 8089, 700)






        }else if (loopCount == 1 && !isResponseReceivedFirstTime) {
            println("broadcasting..")

            val broadcastIP= convertIpToBroadcast(context)
            if(status!!.length <=2){

                send_to_valve(valve!!.mac,valve!!.subtype, createStatusString(pole , status!!.toInt()) , checkIP(context),broadcastIP)

                println("broadcast sent")

            }else{
                send_to_valve(valve!!.mac,valve!!.subtype, status , checkIP(context),broadcastIP)

                println("broadcast sent")
            }
            var isrecived=false
            receiveUdpMessage({ receivedMessage ->
                if (receivedMessage=="failed"){
                    println("broadcast failed..")
                    pass=true

                    println("Broadcast failed..")

                }else{
                    pass=true

                    if ((!receivedMessage.startsWith("cmnd"))&&(!receivedMessage.startsWith("rfsh")) ){
                        println("success..")
                        is_ok=true
                        var receivedmessage_decoded= extract_response(receivedMessage)
                        var  macip=receivedmessage_decoded[0]
                        var  pole_num=receivedmessage_decoded[1]
                        var  key_status=receivedmessage_decoded[2]
                        var ip =receivedmessage_decoded[3]
                        val valve_database3= valve_db.getInstance(context)
                        val same_macs = valve_database3.getvalvesByMacAddress(valve.mac).sortedBy { it!!.subtype!!.toInt() }

//                    var statusList=light_sub_type_decoder(key_status,pole_num)
//                    var index=0
                        for (light_same_mac3 in same_macs){
                            valve_database3.updatevalvebyId(light_same_mac3!!.id , ip)
//                        light_database3.updateStatusById(light_same_mac3!!.id,statusList[index])
//                        index+=1
                        }

                    }else{
                        println("Broadcast failed..")

                    }

                }


            }, 8089, 700)


        }

        if (!is_ok){

            loopCount++
        }else{
            break
        }

        // تاخیر یک ثانیه قبل از ورود به دور جدید از حلقه
        if (!pass){
            Thread.sleep(700)

        }
    } while (loopCount < 2) // تکرار حلقه تا دو بار اجرا

    //    UdpListener8089.resume()
    return is_ok

}


fun udp_thermostat_sms (context: Context, thermostst: Thermostst,status: String?):Boolean{
    //UdpListener8089.pause()
    var pass=false
    var is_ok=false
    var isResponseReceivedFirstTime = false

    val thermostst_database=Temperature_db.getInstance(context)
    val same_macs = thermostst!!.mac?.let { thermostst_database.getThermostatsByMac(it) }
    var pole = thermostst?.subtype?.toInt() ?: 0

    if (same_macs!!.sortedBy { it!!.subtype?.toInt() }[0]!!.subtype!!.toInt() == 1) {
        pole -= 1
    }

    val timeoutMillis = 1000
    var loopCount = 0

    do {


        if (loopCount == 0) {
            if (!isResponseReceivedFirstTime){


            }

            val thermostst_database= Temperature_db.getInstance(context)
            send_to_thermostat_sms(thermostst!!.mac, status , checkIP(context),thermostst_database.get_from_db_Temprature(thermostst.id)!!.ip)
            println("direct IP sent")




            receiveUdpMessage({ receivedMessage ->
                if (receivedMessage=="failed"){
                    println("direct IP failed..")
                    pass=true

                }else{
                    pass=true
                    if ((!receivedMessage.startsWith("cmnd"))&&(!receivedMessage.startsWith("rfsh")) ){
                        is_ok=true
                        println("direct IP success..")
                        isResponseReceivedFirstTime = true
                        val fan_database2= fan_db.getInstance(context)
                        var receivedmessage_decoded= extract_response(receivedMessage)
                        var  macip=receivedmessage_decoded[0]
                        var  pole_num=receivedmessage_decoded[1]
                        var  key_status=receivedmessage_decoded[2]
                        var ip =receivedmessage_decoded[3]

//                    var statusList=light_sub_type_decoder(key_status,pole_num)

//                        val valve_database = valve_db.getInstance(context)
//                        val same_macs = valve_database.getvalvesByMacAddress(valve.mac).sortedBy { it!!.subtype!!.toInt() }
//                        var index=0
//                    for (fan_same_mac in same_macs){
//                        fan_database2.updateStatusbyId(fan_same_mac!!.id,statusList[index])
//                        index+=1
//
//                    }

                    }


                }



            }, 8089, 700)






        }else if (loopCount == 1 && !isResponseReceivedFirstTime) {
            println("broadcasting..")

            val broadcastIP= convertIpToBroadcast(context)
            send_to_thermostat_sms(thermostst!!.mac, status , checkIP(context),broadcastIP)

            println("broadcast sent")
            var isrecived=false
            receiveUdpMessage({ receivedMessage ->
                if (receivedMessage=="failed"){
                    println("broadcast failed..")
                    pass=true

                    println("Broadcast failed..")

                }else{
                    pass=true

                    if ((!receivedMessage.startsWith("cmnd"))&&(!receivedMessage.startsWith("rfsh")) ){
                        println("success..")
                        is_ok=true

                        var receivedmessage_decoded=extract_response(receivedMessage)
                        var  macip=receivedmessage_decoded[0]
                        var  pole_num=receivedmessage_decoded[1]
                        var  key_status=receivedmessage_decoded[2]
                        var new_ip =receivedmessage_decoded[3]
                        val thermostst_database3= Temperature_db.getInstance(context)
                        val same_macs =
                            thermostst.mac?.let { thermostst_database3.getThermostatsByMac(it).sortedBy { it!!.subtype!!.toInt() } }

//                    var statusList=light_sub_type_decoder(key_status,pole_num)
//                    var index=0

                        var statusList= thermostat_subtype_decoder(key_status)

                        thermostst_database3.updatehermostatAndIpById(thermostst!!.id,"22",statusList[1],statusList[4],statusList[3],statusList[0],new_ip)



                    }else{
                        println("Broadcast failed..")

                    }

                }


            }, 8089, 700)


        }

        if (!is_ok){

            loopCount++
        }else{
            break
        }

        // تاخیر یک ثانیه قبل از ورود به دور جدید از حلقه
        if (!pass){
            Thread.sleep(700)

        }
    } while (loopCount < 2) // تکرار حلقه تا دو بار اجرا

    //    UdpListener8089.resume()
    return is_ok

}




//##################################### six worker ################################################

fun udp_six_worker (context: Fragment, mac: String?,pole:String?, type: String, subtype: String?, state: String, device_ip: String?,sixWorkert: six_workert) : Boolean{

    //UdpListener8089.pause()
    try {
       var is_succes=false

       var isResponseReceivedFirstTime = false


       var timeoutMillis = 700
       var loopCount = 0

       do {


           if (loopCount == 0) {
               if (type =="scenario"){
                   if (!(sixWorkert.ip?.startsWith("192"))!!){
                       sixWorkert.ip="192.168.1.1"

                   }

                   sixWorkert!!.mac?.let { send_to_six_worker_scenario(it,pole, checkIP(context.requireContext()),sixWorkert.ip) }


               }else{
                   try {

                       send_to_six_worker(mac,pole,subtype,type, state, device_ip,checkIP(context.requireContext()),sixWorkert.ip,sixWorkert.mac)

                   }catch (e:Exception){
                       println(e)
                   }


               }





               receiveUdpMessage({ receivedMessage ->
                   println(receivedMessage)
                   if (receivedMessage=="failed"){
                       println("direct IP failed..")
                       is_succes = false

                   }else{
                       if ("ok" in receivedMessage ){
                           println("direct IP success..")

                           is_succes = true
                           isResponseReceivedFirstTime=true
                       }

                   }



               }, 8089, 700)






           }else if (loopCount == 1 && !isResponseReceivedFirstTime) {
               println("broadcasting..")

               val broadcastIP=convertIpToBroadcast(context.requireContext())
               if (type =="scenario"){

                   sixWorkert.mac?.let { send_to_six_worker_scenario(it,pole, checkIP(context.requireContext()),broadcastIP) }
                   sixWorkert.mac?.let { send_to_six_worker_scenario(it,pole, checkIP(context.requireContext()),broadcastIP) }
                   sixWorkert.mac?.let { send_to_six_worker_scenario(it,pole, checkIP(context.requireContext()),broadcastIP) }


               }else{
                   send_to_six_worker(mac,pole,subtype,type, state, device_ip,checkIP(context.requireContext()),broadcastIP,sixWorkert.mac)
                   send_to_six_worker(mac,pole,subtype,type, state, device_ip,checkIP(context.requireContext()),broadcastIP,sixWorkert.mac)
                   send_to_six_worker(mac,pole,subtype,type, state, device_ip,checkIP(context.requireContext()),broadcastIP,sixWorkert.mac)

               }
               println("broadcast sent")
               var isrecived=false
               receiveUdpMessage({ receivedMessage ->
                   println(receivedMessage)
                   if (receivedMessage=="failed"){


                       println("Broadcast failed..")
                       is_succes = false

                   }else{


                       if ("ok" in receivedMessage ){
                           println("success..")
                           val current_received_message = receivedMessage.split("~>").toMutableList()
                           val six_worker_db = six_workert_db.getInstance(context.requireContext())

                           six_worker_db.updateIpById(sixWorkert.id,current_received_message[3].toString())
                           println(current_received_message[3])

                           is_succes = true

                       }else{
                           println("Broadcast failed..")
                           is_succes = false

                       }

                   }


               }, 8089, 1000)


           }

           loopCount++

           // تاخیر یک ثانیه قبل از ورود به دور جدید از حلقه
           Thread.sleep(700)
       } while (loopCount < 2) // تکرار حلقه تا دو بار اجرا


        //    UdpListener8089.resume()
       return is_succes
   }catch (e:Exception){
       println(e)

        //    UdpListener8089.resume()
       return false
   }


}




