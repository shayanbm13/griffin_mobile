package com.example.griffinmobile.mudels

import android.content.Context
import java.util.logging.Handler

val handler = android.os.Handler()

class MarqueeTextView(context: Context) : androidx.appcompat.widget.AppCompatTextView(context) {
    private val scrollRunnable = object : Runnable {
        override fun run() {
            scrollBy(5, 0)
            if (scrollX < width) {
                handler.postDelayed(this, 20)
            } else {
                scrollTo(0, 0)
                handler.postDelayed(this, 2000) // delay before restarting scroll
            }
        }
    }

    init {
        post {
            handler.post(scrollRunnable)
        }
    }
}
