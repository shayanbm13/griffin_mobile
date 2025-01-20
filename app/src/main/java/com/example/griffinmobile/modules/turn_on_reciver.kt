package com.example.griffinmobile.mudels

import android.app.KeyguardManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
//import com.example.griffinmobile.dashboard

class turn_on_reciver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        println("oooooooooooooooo")
        if (Intent.ACTION_BOOT_COMPLETED == intent.action) {

/*            val keyguardManager = context?.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager

            // دریافت KeyguardLock
            val keyguardLock = keyguardManager.newKeyguardLock(Context.KEYGUARD_SERVICE)

            // باز کردن قفل صفحه نمایش
            keyguardLock.disableKeyguard()
            println("helloooooooo")
            Toast.makeText(context, "dsdadasdasdad", Toast.LENGTH_SHORT).show()
//            val facebookIntent = context.applicationContext.packageManager.getLaunchIntentForPackage("com.example.griffinmobile")
//            if (facebookIntent != null) {
//                context.applicationContext.startActivity(facebookIntent)
//            } else {
//                Toast.makeText(context, "griffin not installed", Toast.LENGTH_SHORT).show()
//            }
            val startIntent = Intent(context, dashboard::class.java)
            startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(startIntent)*/
        }
    }
}