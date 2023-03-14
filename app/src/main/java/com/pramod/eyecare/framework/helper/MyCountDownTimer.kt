package com.pramod.eyecare.framework.helper

import android.os.CountDownTimer
import android.util.Log
import kotlinx.coroutines.*
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale.US
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class MyCountDownTimer @Inject constructor() {

    companion object {
        private const val TAG = "MyCountDownTimer"
    }

    private var job: Job? = null

    /**
     * @param millisInFuture Future time in milli seconds
     * @param onTimerTick provide ticker and remaining seconds
     * @param onCompleted invoked when timer is completed
     * @param onCancelled invoke when timer is cancelled using [stop]
     */
    fun start(
        millisInFuture: Long,
        onTimerTick: (ticker: Int, remainingMillis: Long) -> Unit = { _, _ -> },
        onCompleted: () -> Unit = {},
        onCancelled: () -> Unit = {},
    ) {
        job?.cancel()
        job = CoroutineScope(Dispatchers.Main.immediate).launch {
            try {
                val seconds =
                    TimeUnit.MILLISECONDS.toSeconds(millisInFuture)
                Timber.tag(TAG).i("start: total seconds:%s", seconds)
                for (i in seconds downTo 0) {
                    onTimerTick(seconds.toInt() - i.toInt(), TimeUnit.SECONDS.toMillis(i))
                    delay(1000)
                }
                onCompleted()
            } catch (_: CancellationException) {
                onCancelled()
            }
        }
    }

    fun stop() {
        job?.cancel()
        job = null
    }
}

fun Long.extractHours(): Int {
    val simpleDateFormat = SimpleDateFormat("HH", US)
    val value =
        simpleDateFormat.format(
            Calendar.getInstance().apply { timeInMillis = this@extractHours }.time
        )
    return value.toIntOrNull() ?: 0
}

fun Long.extractMinutes(): Int {
    val simpleDateFormat = SimpleDateFormat("mm", US)
    val value =
        simpleDateFormat.format(
            Calendar.getInstance().apply { timeInMillis = this@extractMinutes }.time
        )
    return value.toIntOrNull() ?: 0
}
