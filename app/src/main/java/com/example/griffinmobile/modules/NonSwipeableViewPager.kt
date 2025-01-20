package com.example.griffinmobile.mudels

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.viewpager.widget.ViewPager

class NonSwipeableViewPager(context: Context, attrs: AttributeSet?) : ViewPager(context, attrs) {

    override fun onTouchEvent(event: MotionEvent): Boolean {
        // تغییر این برگشتی به false می‌تواند منجر به غیرفعال شدن اسکرول باشد
        return false
    }

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        // تغییر این برگشتی به false می‌تواند منجر به غیرفعال شدن اسکرول باشد
        return false
    }
}