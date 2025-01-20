package com.example.griffinmobile.mudels

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.example.griffinmobile.R
import kotlin.math.cos
import kotlin.math.sin

class CircularSeekBar(context: Context, attrs: AttributeSet?) : View(context, attrs) {

    private val circlePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val thumbPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val dotPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val selectedDotPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    private var previousAngle = 0f

    private val defaultDotImage: Bitmap
    private val blueDotImage: Bitmap

    private var radius = 0f
    private var centerX = 0f
    private var centerY = 0f
    private var angle = 0f

    private var selectedImage: Bitmap? = null
    private var imageX = 0f
    private var imageY = 0f

    private val numDots = 15
    private val dotRadius = 8f
    private val selectedDotRadius = 12f
    private val dotSpacingAngle = 310f / numDots
    private var selectedDotIndex = 0
    var currentSelectedIndex=""



    private var circularSeekBarChangeListener: OnCircularSeekBarChangeListener? = null
    interface OnCircularSeekBarChangeListener {
        fun onProgressChanged(seekBar: CircularSeekBar?, progress: Int, fromUser: Boolean)
        fun onStartTrackingTouch(seekBar: CircularSeekBar?)
        fun onStopTrackingTouch(seekBar: CircularSeekBar?)
    }


    init {
        circlePaint.color = Color.GRAY
        thumbPaint.color = Color.RED
        dotPaint.color = Color.GRAY
        selectedDotPaint.color = Color.BLUE


        defaultDotImage = BitmapFactory.decodeResource(resources, R.drawable.circular_dot_off)
        blueDotImage = BitmapFactory.decodeResource(resources, R.drawable.circular_dot_on)

    }


    fun setSelectedImage(bitmap: Bitmap) {
        selectedImage = bitmap
        invalidate()
    }

    fun setProgress(progress: Int) {
        // محدود کردن مقدار progress به محدوده مجاز
        val minProgress = 0
        val maxProgress = numDots
        val progress1=progress-16
        val clampedProgress = progress1.coerceIn(minProgress, maxProgress)

        // تنظیم مقدار selectedDotIndex به مقدار clampedProgress
        selectedDotIndex = clampedProgress
        currentSelectedIndex=(selectedDotIndex+16).toString().replace("°","")
        // اعمال تغییرات به تصویر
        invalidate()
    }

    fun setOnCircularSeekBarChangeListener(listener: OnCircularSeekBarChangeListener?) {
        circularSeekBarChangeListener = listener
    }


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        radius = (Math.min(w, h) / 2 * 0.5).toFloat()
        centerX = (w / 2).toFloat()
        centerY = (h / 2).toFloat()

        // محاسبه قطر دایره و تنظیم آن به عنوان ابعاد بک‌گراند
        val diameter = radius * 2
        val desiredWidth = diameter.toInt() +17
        val desiredHeight = diameter.toInt()+17

        val backgroundImage = BitmapFactory.decodeResource(resources, R.drawable.circular_image)
        val resizedBackgroundImage = Bitmap.createScaledBitmap(backgroundImage, desiredWidth, desiredHeight, true)
        setSelectedImage(resizedBackgroundImage)

        // محاسبه موقعیت مرکز بک‌گراند در مرکز دایره سیرکولار سیک بار
        imageX = centerX - (resizedBackgroundImage.width / 2f)
        imageY = centerY - (resizedBackgroundImage.height / 2f)
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawCircle(centerX, centerY, radius, circlePaint)

        val startPoint = -180f // ساعت 9 به معنای زاویه صفر در دایره

        for (i in 0 until numDots) {
            val dotAngle = startPoint + i * dotSpacingAngle
            if (dotAngle >= 140 && dotAngle <= 180) {
                continue
            }

            val distanceFromCenter = radius + 60f
            val dotX = centerX + distanceFromCenter * cos(Math.toRadians(dotAngle.toDouble())).toFloat()
            val dotY = centerY + distanceFromCenter * sin(Math.toRadians(dotAngle.toDouble())).toFloat()

            val imageToDraw = if (i <= selectedDotIndex) blueDotImage else defaultDotImage
            canvas.drawBitmap(imageToDraw, dotX - imageToDraw.width / 2f, dotY - imageToDraw.height / 2f, null)
        }

        // Draw text in the center of the circle
        val text = "${selectedDotIndex + 16}°"
        currentSelectedIndex=text.replace("°","")
        val textPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        textPaint.color = Color.WHITE
        textPaint.textSize = 90f
        textPaint.textAlign = Paint.Align.CENTER
        val textBounds = Rect()
        textPaint.getTextBounds(text, 0, text.length, textBounds)
        val textX = centerX
        val textY = centerY + textBounds.height() / 2f
        canvas.drawText(text, textX, textY, textPaint)

        // Draw selected image and degree text
        selectedImage?.let {
            canvas.drawBitmap(it, imageX, imageY, null) // Using null for imagePaint

            // Calculate the position for the degree text at the bottom of the selected image
            textPaint.textAlign = Paint.Align.CENTER
            val textBounds = Rect()
            textPaint.getTextBounds(text, 0, text.length, textBounds)
            val textX = centerX
            val textY = centerY + textBounds.height() / 2f
            canvas.drawText(text, textX, textY, textPaint)

            val text = "${selectedDotIndex + 16}°"

            // Draw the text
            val textPaint = Paint(Paint.ANTI_ALIAS_FLAG)
            textPaint.color = Color.WHITE
            textPaint.textSize = 90f
            textPaint.textAlign = Paint.Align.CENTER
            canvas.drawText(text, textX, textY, textPaint)


        }
    }


    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                // محاسبه زاویه انتخاب شده بر اساس تاچ
                angle = Math.atan2(event.y - centerY.toDouble(), event.x - centerX.toDouble()).toFloat()
                circularSeekBarChangeListener?.onStartTrackingTouch(this)

                // تغییر زاویه تاچ به 180 درجه (ساعت 12)
                angle += Math.PI.toFloat()

                // محاسبه درجه انتخاب شده
                val degree = (Math.toDegrees(angle.toDouble()) + 360) % 360

                // محدوده‌های مجاز برای نمایش
                val minAllowedDegree = 0.0
                val maxAllowedDegree = 290.0

                // محدود کردن مقادیر مجاز
                if (degree >= minAllowedDegree && degree <= maxAllowedDegree) {
                    // تبدیل مقدار انتخاب شده به مقیاس نسبی بین 0 و 1
                    val relativeValue = (degree - minAllowedDegree) / (maxAllowedDegree - minAllowedDegree)
                    // محاسبه نمایه نقطه بر اساس مقیاس نسبی و تعداد نقاط - 1
                    selectedDotIndex = ((numDots ) * relativeValue).toInt()
                    currentSelectedIndex=(selectedDotIndex+16).toString()
                    // ارسال تغییر به گوش‌کننده تغییرات سیکبار
                    circularSeekBarChangeListener?.onProgressChanged(this, selectedDotIndex, true)
                }

                invalidate()
                return true
            }
            MotionEvent.ACTION_UP -> {
                // دستورات مرتبط با پایان تغییر مقدار را اجرا کنید
                circularSeekBarChangeListener?.onStopTrackingTouch(this)
                // ...
                return true
            }
        }
        return super.onTouchEvent(event)
    }
}