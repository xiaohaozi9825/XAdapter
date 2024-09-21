package pw.xiaohaozi.xadapterdemo.utils

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import kotlin.math.absoluteValue
import kotlin.math.min

fun View.click(minTime: Long = 10L, listener: (View) -> Unit) {
    var lastTime = 0L
    this.setOnClickListener {
        val tmpTime = System.currentTimeMillis()
        if (tmpTime - lastTime > minTime) {
            lastTime = tmpTime
            listener.invoke(it)
        }
    }
    this.anima()
}


@SuppressLint("ClickableViewAccessibility")
fun View.anima(d: Int = -1) {
    var downX = -1f
    var downY = -1f
    var isDown = false
    val touchSlop = ViewConfiguration.get(context).scaledTouchSlop
    this.setOnTouchListener { view, motionEvent ->
        when (motionEvent.action) {
            MotionEvent.ACTION_DOWN -> {
                downX = motionEvent.x
                downY = motionEvent.y
                view.postDelayed({
                    if (isDown) {
                        val value = if (d < 0) min(view.width, view.height) / 10f else d.toFloat()
                        view.scaleX = (view.width - value) / view.width
                        view.scaleY = (view.height - value) / view.height
                    }
                }, 150)
            }
            MotionEvent.ACTION_MOVE -> {
                isDown =
                    downX > 0 && downY > 0 && (motionEvent.x - downX).absoluteValue < touchSlop && (motionEvent.y - downY).absoluteValue < touchSlop
            }
            MotionEvent.ACTION_UP -> {
                view.scaleX = 1f
                view.scaleY = 1f
                isDown = false
                downX = -1f
                downY = -1f
            }
            MotionEvent.ACTION_CANCEL -> {
                view.scaleX = 1f
                view.scaleY = 1f
                isDown = false
                downX = -1f
                downY = -1f
            }
        }
        return@setOnTouchListener false
    }
}