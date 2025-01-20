package com.example.griffinmobile.mudels

import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Handler

    class CarHornSoundPlayer private constructor() {
    private var toneGenerator: ToneGenerator? = null
    private var isSoundOn = false

    private val hornSoundHandler = Handler()
    private val hornSoundRunnable = object : Runnable {
        override fun run() {
            if (isSoundOn) {
                playHornSound()
                hornSoundHandler.postDelayed(this, HORN_INTERVAL)
            }
        }
    }

    fun startHornSound() {
        if (toneGenerator == null) {
            toneGenerator = ToneGenerator(AudioManager.STREAM_ALARM, 100)
        }
        isSoundOn = true
        hornSoundHandler.post(hornSoundRunnable)
    }

    fun stopHornSound() {
        isSoundOn = false
        toneGenerator?.release()
        toneGenerator = null
        hornSoundHandler.removeCallbacks(hornSoundRunnable)
    }

    private fun playHornSound() {
        toneGenerator?.startTone(ToneGenerator.TONE_CDMA_ALERT_NETWORK_LITE)
    }

    companion object {
        private const val HORN_INTERVAL = 1000L // زمان فاصله بین هر بوق (در میلی‌ثانیه)

        @Volatile
        private var instance: CarHornSoundPlayer? = null

        fun getInstance(): CarHornSoundPlayer {
            return instance ?: synchronized(this) {
                instance ?: CarHornSoundPlayer().also { instance = it }
            }
        }
    }
}
