package com.example.griffinmobile.mudels

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import kotlin.math.ceil

class CustomLoadingLine @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val borderPaint: Paint = Paint()
    private val linePaint: Paint = Paint()
    private var currentProgress = 0.0 // مقدار پیشرفت لودینگ درصدی

    init {
        borderPaint.color = Color.BLACK // رنگ کادر
        borderPaint.strokeWidth = 5f // ضخامت خطوط کادر
        borderPaint.style = Paint.Style.STROKE
        borderPaint.isAntiAlias = true

        linePaint.color =  Color.parseColor("#7D33CC")// رنگ خط لودینگ
        linePaint.strokeWidth = 37f // ضخامت خط لودینگ
        linePaint.isAntiAlias = true

        // شروع یک Runnable برای افزایش مقدار پیشرفت هر دو ثانیه
//        val handler = Handler()
//        handler.postDelayed(object : Runnable {
//            override fun run() {
//                increaseProgress()
//                handler.postDelayed(this, 2000) // انجام Runnable هر دو ثانیه
//            }
//        }, 2000)
    }


    // تابع برای افزایش مقدار پیشرفت به صورت 10 درصد
    fun increaseProgress(progressToAdd: Double) {
        if (progressToAdd == 0.0 ){
            currentProgress = progressToAdd
        }else{
            currentProgress +=  ceil(progressToAdd * 100) / 100.0
            if (currentProgress > 100) {
                currentProgress = 100.0
            }


        }
        invalidate()

    }
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val width = width
        val height = height
        val centerX = width / 2.toFloat()
        val centerY = height / 2.toFloat()

        val borderSize = 20f // اندازه کادر
        val lineLength = width - 2 * borderSize - borderSize / 2 // طول خط لودینگ برابر با عرض منطقی کادر با فاصله

        // رسم کادر
        canvas.drawRect(
            borderSize / 2, borderSize / 2, width - borderSize / 2, height - borderSize / 2,
            borderPaint
        )

        // محاسبه موقعیت خط لودینگ بر اساس مقدار پیشرفت
        val startX = centerX - lineLength / 2
        val endX = centerX + lineLength / 2
        val progressX = startX + (currentProgress / 100f) * lineLength

        // رسم خط لودینگ داخل کادر
        canvas.drawLine(
            startX, centerY, progressX.toFloat(), centerY,
            linePaint
        )

        // رسم متن درصد
        var a= ceil(currentProgress * 100) / 100.0
        val text = "$a% Loading..."
        val textPaint = Paint().apply {
            color = Color.BLACK
            textSize = 38f
            textAlign = Paint.Align.CENTER
        }
        val textHeight = textPaint.descent() - textPaint.ascent()
        canvas.drawText(
            text, centerX, height - borderSize / 2 - textHeight / 2,
            textPaint
        )
    }
}