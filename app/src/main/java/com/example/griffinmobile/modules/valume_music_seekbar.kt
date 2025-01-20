package com.example.griffinmobile.mudels

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.media.AudioManager
import android.util.AttributeSet
import android.widget.SeekBar
import androidx.appcompat.widget.AppCompatSeekBar
import androidx.core.content.ContextCompat.getSystemService
import com.example.griffinmobile.R

class CustomSeekBar(context: Context, attrs: AttributeSet) : AppCompatSeekBar(context, attrs) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val totalDots = 10
    private val dotPositions = mutableListOf<Float>()

    private val dotBitmapOff: Bitmap
    private val dotBitmapOn: Bitmap

    init {
        // بارگذاری دکمه‌ها
        dotBitmapOff = BitmapFactory.decodeResource(resources, R.drawable.circular_dot_off)
        dotBitmapOn = BitmapFactory.decodeResource(resources, R.drawable.circular_dot_on)

        // لیسنر پیش‌فرض
        setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
                val volume = ((progress / max.toFloat()) * audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)).toInt()

                if (audioManager.getStreamVolume(AudioManager.STREAM_MUSIC) != volume) {
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        calculateDotPositions()
    }

    private fun calculateDotPositions() {
        dotPositions.clear()
        val availableWidth = width - paddingLeft - paddingRight
        val interval = availableWidth.toFloat() / (totalDots - 1)

        for (i in 0 until totalDots) {
            dotPositions.add(paddingLeft + i * interval)
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // محاسبه موقعیت عمودی دکمه‌ها
        val dotY = height / 2f

        for (i in 0 until totalDots) {
            val dotBitmap = if (progress >= max * i / (totalDots - 1)) dotBitmapOn else dotBitmapOff
            val dotX = dotPositions[i]
            canvas.drawBitmap(dotBitmap, dotX - dotBitmap.width / 2, dotY - dotBitmap.height / 2, paint)
        }
    }

    fun setCustomProgress(progress: Int) {
        if (this.progress != progress) {
            setProgress(progress)
            invalidate()
        }
    }

    fun getCustomProgress(): Int = progress
}