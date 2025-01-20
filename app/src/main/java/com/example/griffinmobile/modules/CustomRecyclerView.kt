package com.example.griffinmobile.modules

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class CustomRecyclerView(context: Context, attrs: AttributeSet) : RecyclerView(context, attrs) {

    private var isScrolling = false
    private val gridLayoutManager: GridLayoutManager = GridLayoutManager(context, 3) // تعداد ستون‌ها

    init {
        // تنظیم LayoutManager برای گرید
        layoutManager = gridLayoutManager
        setHasFixedSize(true)
    }

    override fun onTouchEvent(e: MotionEvent): Boolean {
        // وقتی که انگشت از صفحه برداشته شد، اسکرول به نزدیک‌ترین خط می‌رود
        if (e.action == MotionEvent.ACTION_UP) {
            snapToNearestLine()
        }
        return super.onTouchEvent(e)
    }

    override fun onScrolled(dx: Int, dy: Int) {
        super.onScrolled(dx, dy)
        isScrolling = true
    }

    // تابعی برای اسکرول به نزدیک‌ترین خط
    private fun snapToNearestLine() {
        // محاسبه موقعیت فعلی
        val totalHeight = computeVerticalScrollRange()
        val currentPosition = computeVerticalScrollOffset()

        // محاسبه ارتفاع هر خط (بر اساس تعداد ستون‌ها)
        val itemHeight = (totalHeight / adapter?.itemCount!!) ?: 1

        // محاسبه نزدیک‌ترین خط
        val nearestLine = (currentPosition + itemHeight / 2) / itemHeight * itemHeight

        // اسکرول به موقعیت خط
        smoothScrollBy(0, nearestLine - currentPosition)
    }
}