package com.example.griffinmobile.mudels

//import android.app.Activity
//import android.app.KeyguardManager
//import android.content.BroadcastReceiver
//import android.content.Context
//import android.content.Intent
//import android.os.Build
//import android.os.PowerManager
//import androidx.core.app.ActivityCompat.startActivity
//import com.example.griffinmobile.black_screen
//import com.example.griffinmobile.dashboard
//
//class ScreenReceiver() : BroadcastReceiver() {
//
//    override fun onReceive(context: Context, intent: Intent) {
//        if (intent.action == Intent.ACTION_SCREEN_OFF) {
//            turnScreenOn(context)
//            unlockScreen(context)
//            val activityIntent = Intent(context, black_screen::class.java).apply {
//                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
//            }
//            context.startActivity(activityIntent)
//        }
//    }
//
//    private fun turnScreenOn(context: Context) {
//        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
//        val wakeLock = powerManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP, "MyApp:MyWakelockTag")
//        wakeLock.acquire(10) // Acquire wake lock for 3 seconds
//
//
//
//
//
////        wakeLock.release() // Release the wake lock after acquiring it
//    }
//
//    private fun unlockScreen(context: Context) {
//        val keyguardManager = context.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager?
//        keyguardManager?.run {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                val activity = context as? Activity ?: return
//                requestDismissKeyguard(activity, object : KeyguardManager.KeyguardDismissCallback() {
//                    override fun onDismissError() {
//                        super.onDismissError()
//                    }
//
//                    override fun onDismissSucceeded() {
//                        super.onDismissSucceeded()
//                    }
//
//                    override fun onDismissCancelled() {
//                        super.onDismissCancelled()
//                    }
//                })
//            } else {
//                val keyguardLock = newKeyguardLock("MyApp:UnlockScreenTag")
//                keyguardLock.disableKeyguard()
//            }
//        }
//    }
//}