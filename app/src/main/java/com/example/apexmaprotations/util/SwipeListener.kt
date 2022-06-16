package com.example.apexmaprotations.util

import android.view.MotionEvent
import android.view.View
import kotlin.math.abs


interface SwipeListener {
    fun onSwipeLeft()
    fun onSwipeRight()
}

open class SwipeGestureListener internal constructor(
    private val listener: SwipeListener,
    private val minDistance: Int = DEFAULT_SWIPE_MIN_DISTANCE
) : View.OnTouchListener {
    companion object {
        const val DEFAULT_SWIPE_MIN_DISTANCE = 200
    }

    private var anchorX = 0F

    override fun onTouch(view: View, event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                anchorX = event.x
                return false
            }
            MotionEvent.ACTION_UP -> {
                if (abs(event.x - anchorX) > minDistance) {
                    if (event.x > anchorX) {
                        listener.onSwipeRight()
                    } else {
                        listener.onSwipeLeft()
                    }
                }
                return true
            }
        }
        return view.performClick()
    }
}