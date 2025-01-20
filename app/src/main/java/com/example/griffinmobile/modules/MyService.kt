package com.example.griffinmobile.mudels
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.os.PowerManager

class MyService : Service() {

    private lateinit var wakeLock: PowerManager.WakeLock

    override fun onCreate() {
        super.onCreate()

//         به دست آوردن PowerManager
        val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager

        // ایجاد WakeLock
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyApp::MyWakelockTag")

        // بیدار نگه داشتن CPU
        wakeLock.acquire()
    }

    override fun onDestroy() {
        super.onDestroy()
        // آزاد کردن WakeLock
//        if (wakeLock.isHeld) {
//            wakeLock.release()
//        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // کد مربوط به سرویس شما اینجا قرار می‌گیرد
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}