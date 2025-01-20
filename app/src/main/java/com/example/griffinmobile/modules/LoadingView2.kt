package com.example.griffinmobile.mudels

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.View


class LoadingView2(context: Context, attrs: AttributeSet?) : View(context, attrs) {

    private val paint = Paint()
    private val handlerr = Handler(Looper.getMainLooper())
    private var progress: Float = 0f
    private var isLoading = false

    private val gPath = Path()

    init {
        gPath.moveTo(50f, 20f)
        gPath.lineTo(80f, 20f)
        gPath.lineTo(80f, 60f)
        gPath.lineTo(50f, 60f)
        gPath.lineTo(60f, 80f)
        gPath.lineTo(70f, 80f)
        gPath.close()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (isLoading) {
            paint.color = Color.parseColor("#777777")
            canvas.drawPath(gPath, paint)
        } else {
            paint.color = Color.WHITE
            canvas.drawPath(gPath, paint)
        }

        if (isLoading) {
            paint.color = Color.WHITE
            canvas.drawRect(
                progress * width.toFloat(),
                0f,
                (progress + 0.2f) * width.toFloat(),
                height.toFloat(),
                paint
            )
        }
    }

    fun startLoading() {
        isLoading = true
        progress = 0f

        handlerr.postDelayed({
            progress += 0.2f
            if (progress < 1f) {
                startLoading()
            } else {
                isLoading = false
            }
        }, 100)
    }

    fun stopLoading() {
        isLoading = false
    }

}
