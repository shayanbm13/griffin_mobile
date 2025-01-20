package com.example.griffinmobile.mudels
//import android.app.*
//import android.content.Context
//import android.content.Intent
//import android.net.wifi.WifiManager
//import android.os.Build
//import android.os.IBinder
//import android.os.PowerManager
//import androidx.core.app.NotificationCompat
//
//import java.io.IOException
//import java.net.DatagramPacket
//import java.net.DatagramSocket
//import kotlin.concurrent.thread
//
//class MyForegroundService : Service() {
//    private lateinit var wakeLock: PowerManager.WakeLock
//
//    override fun onBind(intent: Intent?): IBinder? {
//        return null
//    }
//
//    override fun onCreate() {
//        super.onCreate()
//        createNotificationChannel()
//
//
//        val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
//        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyApp::WakeLockTag")
//        wakeLock.acquire(10*60*1000L /*10 minutes*/)
//    }
//
//    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
//        startForegroundService()
//        return START_STICKY
//    }
//
//    private fun startForegroundService() {
//        val notificationIntent = Intent(this, dashboard::class.java)
//        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE)
//
//        val notification: Notification = NotificationCompat.Builder(this, NotificationChannels.FOREGROUND_SERVICE_CHANNEL_ID)
//            .setContentTitle("Service Running")
//            .setContentText("The service is running in the background.")
////            .setSmallIcon(R.drawable.ic_notification) // Ensure you have this drawable
//            .setContentIntent(pendingIntent)
//            .build()
//
//        startForeground(1, notification)
//    }
//
//    private fun createNotificationChannel() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val channelName = "Foreground Service Channel"
//            val channelDescription = "Channel for Foreground Service notifications."
//            val importance = NotificationManager.IMPORTANCE_LOW
//            val channel = NotificationChannel(NotificationChannels.FOREGROUND_SERVICE_CHANNEL_ID, channelName, importance).apply {
//                description = channelDescription
//            }
//            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//            notificationManager.createNotificationChannel(channel)
//        }
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        // Release WakeLock
////        if (::wakeLock.isInitialized && wakeLock.isHeld) {
////            wakeLock.release()
////        }
//    }
//}