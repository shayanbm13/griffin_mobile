package com.example.griffinmobile.modules

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Handler
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.OnSuccessListener
fun getLocationFor45Seconds(context: Context, activity: Activity, fusedLocationClient: FusedLocationProviderClient, callback: (Location?) -> Unit) {
    // بررسی مجوزهای لازم
    if (ActivityCompat.checkSelfPermission(
            activity,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            activity,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        // درخواست مجوز
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            1
        )
        return
    }

    var lastLocation: Location? = null
    val handler = Handler()
    val maxTime = 45000L  // 45 ثانیه
    val interval = 10000L  // 10 ثانیه

    // تابعی که هر 10 ثانیه یکبار موقعیت مکانی می‌گیرد
    val runnable = object : Runnable {
        override fun run() {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }

            // درخواست موقعیت مکانی
            fusedLocationClient.lastLocation.addOnSuccessListener(activity, OnSuccessListener<Location> { location ->
                if (location != null) {
                    // آخرین موقعیت مکانی را ذخیره می‌کنیم
                    lastLocation = location
                }
            })

            // اگر زمان بیشتر از 45 ثانیه شد، تابع متوقف می‌شود
            handler.postDelayed(this, interval)
        }
    }

    // شروع به گرفتن موقعیت مکانی
    handler.post(runnable)

    // پس از 45 ثانیه، آخرین موقعیت مکانی را به کال‌بک ارسال می‌کنیم
    handler.postDelayed({
        handler.removeCallbacks(runnable)
        callback(lastLocation)
    }, maxTime)
}