package com.example.griffinmobile.mudels

import android.annotation.SuppressLint
import android.app.job.JobParameters
import android.app.job.JobService

class MyJobService : JobService() {
    @SuppressLint("SpecifyJobSchedulerIdRange")
    override fun onStartJob(params: JobParameters?): Boolean {

        println("sssssdddfdfffffffffffffffffffffffffffff")
        // کد مربوط به اجرای وظیفه شما
        return false // اگر کار شما به صورت غیرهمزمان است، باید true برگردانید
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        return true
    }
}