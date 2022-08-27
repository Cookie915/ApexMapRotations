package com.cooksmobilesolutions.apexmaprotations.util

import android.util.Log
import android.view.MotionEvent
import android.view.View
import kotlin.math.abs


interface SwipeListener {
    fun onSwipeLeft()
    fun onSwipeRight()
    fun onSwipeDown()
    fun onSwipeUp()
}

open class SwipeGestureListener internal constructor(
    private val listener: SwipeListener,
    private val minDistanceHorizontal: Int = DEFAULT_SWIPE_HORIZONTAL_MIN_DISTANCE,
    private val minDistanceVertical: Int = DEFAULT_SWIPE_VERTICAL_MIN_DISTANCE
) : View.OnTouchListener {
    companion object {
        const val DEFAULT_SWIPE_HORIZONTAL_MIN_DISTANCE = 200
        const val DEFAULT_SWIPE_VERTICAL_MIN_DISTANCE = 400
    }

    private var anchorX = 0F
    private var anchorY = 0F

    override fun onTouch(view: View, event: MotionEvent): Boolean {
        Log.i("tester12", "onTouch")
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {

                Log.i("tester12", "actionDown")
                anchorX = event.x
                anchorY = event.y
                return true
            }
            MotionEvent.ACTION_UP -> {
                if (abs(event.x - anchorX) > minDistanceHorizontal) {
                    if (event.x > anchorX) {
                        Log.i("tester12", "onSwipeRight Listener")
                        Log.i("tester12", "swipeRight")
                        listener.onSwipeRight()
                    } else {
                        Log.i("tester12", "Swipe Left")
                        listener.onSwipeLeft()
                    }
                }
                if (abs(event.y - anchorY) > minDistanceVertical) {
                    if (event.y > anchorY) {
                        listener.onSwipeDown()
                    } else {
                        listener.onSwipeUp()
                    }
                }
                return true
            }
        }
        return view.performClick()
    }
}