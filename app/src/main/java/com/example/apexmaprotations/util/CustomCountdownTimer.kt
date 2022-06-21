package com.example.apexmaprotations.util

import android.os.Handler
import android.os.Looper
import android.os.Message
import android.os.SystemClock
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

//  Countdown timer with ability to reset mMillisInFuture
abstract class CustomCountdownTimer(
    /**
     * Millis since epoch when alarm should stop.
     */
    private var mMillisInFuture: Long,
    /**
     * The interval in millis that the user receives callbacks
     */
    private var mCountdownInterval: Long
) {
    private var mStopTimeInFuture: Long = 0

    /**
     * boolean representing if the timer was cancelled
     */
    private var mCancelled = false

    //  Set new time for timer
    fun setMillisInFuture(millisInFuture: Long) {
        mMillisInFuture = millisInFuture
    }

    fun setCountdownInterval(countdownInterval: Long) {
        mCountdownInterval = countdownInterval
    }

    val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)

    /**
     * Cancel the countdown.
     */
    @Synchronized
    fun cancel() {
        mCancelled = true
        mHandler.removeMessages(MSG)
    }

    /**
     * Start the countdown.
     */
    @Synchronized
    fun start(): CustomCountdownTimer {
        mCancelled = false
        if (mMillisInFuture <= 0) {
            scope.launch {
                onFinish()
            }
            return this
        }
        mStopTimeInFuture = SystemClock.elapsedRealtime() + mMillisInFuture
        mHandler.sendMessage(mHandler.obtainMessage(MSG))
        return this
    }

    /**
     * Callback fired on regular interval.
     * @param millisUntilFinished The amount of time until finished.
     */
    abstract fun onTick(millisUntilFinished: Long)

    /**
     * Callback fired when the time is up.
     */
    abstract suspend fun onFinish()

    // handles counting down
    private val mHandler: Handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            synchronized(this@CustomCountdownTimer) {
                if (mCancelled) {
                    return
                }
                val millisLeft = mStopTimeInFuture - SystemClock.elapsedRealtime()
                if (millisLeft <= 0) {
                    scope.launch {
                        onFinish()
                    }
                } else {
                    val lastTickStart = SystemClock.elapsedRealtime()
                    onTick(millisLeft)

                    // take into account user's onTick taking time to execute
                    val lastTickDuration = SystemClock.elapsedRealtime() - lastTickStart
                    var delay: Long
                    if (millisLeft < mCountdownInterval) {
                        // just delay until done
                        delay = millisLeft - lastTickDuration

                        // special case: user's onTick took more than interval to
                        // complete, trigger onFinish without delay
                        if (delay < 0) delay = 0
                    } else {
                        delay = mCountdownInterval - lastTickDuration

                        // special case: user's onTick took more than interval to
                        // complete, skip to next interval
                        while (delay < 0) delay += mCountdownInterval
                    }
                    sendMessageDelayed(obtainMessage(MSG), delay)
                }
            }
        }
    }

    companion object {
        private const val MSG = 1
    }
}