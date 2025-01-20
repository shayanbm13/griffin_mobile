package com.example.griffinmobile

//import android.app.Application
//import android.app.job.JobInfo
//import android.app.job.JobScheduler
//import android.content.ComponentName
//import android.content.Context
//import android.content.Intent
//import android.content.IntentFilter
//import android.os.Handler
//import android.os.Looper
//import android.os.PowerManager
//import android.view.WindowManager
//import androidx.core.content.ContextCompat.registerReceiver
//import androidx.lifecycle.ViewModelProvider
////import com.example.griffin.mudels.ScreenReceiver
////
////import androidx.work.OneTimeWorkRequest
////import androidx.work.WorkManager
////import com.example.griffin.mudels.Music_player
////import com.example.griffin.mudels.MyJobService
////import com.example.griffin.mudels.SharedViewModel
////import com.example.griffin.mudels.SoundManager
////import com.example.griffin.mudels.aplicationSharedvVewModel
////import com.example.griffin.mudels.handler
//
////import com.example.griffin.mudels.BootWorker
//
//class MyApplication : Application() {
//
//    private lateinit var screenReceiver: ScreenReceiver
//    private lateinit var handler: Handler
//    private lateinit var runnable: Runnable
//
//    val aplicationSharedvVewModel: aplicationSharedvVewModel by lazy {
//        aplicationSharedvVewModel(this) // ایجاد یک نمونه از ViewModel
//    }
//    override fun onCreate() {
//        super.onCreate()
//
//        val musicPlayer = Music_player.getInstance(this)
//        SoundManager.initialize(this)
//// در کد اصلی یا در یک نقطه مناسب
////        val workRequest = OneTimeWorkRequest.Builder(BootWorker::class.java)
////            .build()
////
////        WorkManager.getInstance(this).enqueue(workRequest)
//        handler = Handler(Looper.getMainLooper())
//        startCheckingLoginStatus()
//
////        val sharedPreferences = getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE)
////        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)
////
////
////        if (!isLoggedIn) {
////
////        } else {
////            val powerManager2 = applicationContext.getSystemService(Context.POWER_SERVICE) as PowerManager
////            val wakeLock2 = powerManager2.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP, "MyApp:WakeLockTag")
////            wakeLock2.acquire()
////            screenReceiver = ScreenReceiver()
////            val filter = IntentFilter(Intent.ACTION_SCREEN_OFF)
////            registerReceiver(screenReceiver, filter)
////        }
//
//
//
////        val jobScheduler = getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
////        val jobInfo = JobInfo.Builder(1, ComponentName(this, MyJobService::class.java))
////            .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
////            .setRequiresCharging(false)
////            .setPersisted(true)
////            .build()
////        jobScheduler.schedule(jobInfo)
//
//    }
//
//
//    private fun startCheckingLoginStatus() {
//        runnable = Runnable {
//            val sharedPreferences = getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE)
//            val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)
//
//            if (!isLoggedIn) {
//                // اگر هنوز لاگین نشده، دوباره بعد از سه دقیقه چک می‌کنیم
//                handler.postDelayed(runnable, 3 * 60 * 1000) // سه دقیقه
//            } else {
//                // کارهای لازم در صورتی که لاگین شده باشد
//                val powerManager2 = applicationContext.getSystemService(Context.POWER_SERVICE) as PowerManager
//                val wakeLock2 = powerManager2.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP, "MyApp:WakeLockTag")
//                wakeLock2.acquire()
//
//                // راه‌اندازی ScreenReceiver و تنظیم فیلترها
//                val screenReceiver = ScreenReceiver()
//                val filter = IntentFilter(Intent.ACTION_SCREEN_OFF)
//                registerReceiver(screenReceiver, filter)
//
//                // متوقف کردن چک کردن وضعیت لاگین
//                handler.removeCallbacks(runnable)
//            }
//        }
//
//        // اجرای اولین چک
//        handler.post(runnable)
//    }
//
//    override fun onTerminate() {
//        super.onTerminate()
//        println("terrrrminated")
//        unregisterReceiver(screenReceiver)
//        SoundManager.release()
//        val musicPlayer = Music_player.getInstance(this)
//        musicPlayer.stopMusic()
//    }
//
//}
