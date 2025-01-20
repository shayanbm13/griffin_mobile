package com.example.griffinmobile.mudels

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import com.example.griffinmobile.R

//object SoundManager {
//
//    private var soundPool: SoundPool? = null
//    private var soundId: Int = 0
//
//    fun initialize(context: Context) {
//        if (soundPool == null) {
//            val audioAttributes = AudioAttributes.Builder()
//                .setUsage(AudioAttributes.USAGE_MEDIA)
//                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
//                .build()
//
//            soundPool = SoundPool.Builder()
//                .setMaxStreams(1)
//                .setAudioAttributes(audioAttributes)
//                .build()
//
//            soundId = soundPool!!.load(context, R.raw.zapsplat_multimedia_button_click_bright_003_92100, 1) // بارگذاری صدا
//        }
//    }
//
//    fun playSound() {
//        soundPool?.play(soundId, 1f, 1f, 0, 0, 1f) // پخش صدا
//    }
//
//    fun release() {
//        soundPool?.release() // آزادسازی منابع
//        soundPool = null
//    }
//}
