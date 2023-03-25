package com.pramod.eyecare.framework.ui

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.pramod.eyecare.business.Constant.ALARM_TIME_INTERVAL_202020_RULE
import com.pramod.eyecare.business.Constant.GAZE_TIME
import com.pramod.eyecare.business.EyeCareUiCountDownTimer
import com.pramod.eyecare.business.domain.data.preference.ScheduledAlarmCache
import com.pramod.eyecare.framework.helper.MyCountDownTimer
import com.pramod.eyecare.framework.service.EyeCarePersistentForegroundService
import com.pramod.eyecare.framework.service.EyeCarePersistentForegroundService.Companion.ACTION_GAZE_TIMER_CANCELLED
import com.pramod.eyecare.framework.service.EyeCarePersistentForegroundService.Companion.ACTION_GAZE_TIMER_COMPLETED
import com.pramod.eyecare.framework.service.EyeCarePersistentForegroundService.Companion.ACTION_GAZE_TIMER_IN_PROGRESS
import com.pramod.eyecare.framework.service.EyeCarePersistentForegroundService.Companion.EXTRA_GAZE_TIMER_REMAINING_SECONDS
import com.pramod.eyecare.framework.ui.utils.isServiceRunning
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class EyeCareUiCountDownTimerImpl @Inject constructor(
    private val localBroadcastManager: LocalBroadcastManager,
    private val scheduledAlarmCache: ScheduledAlarmCache,
    private val myCountDownTimer: MyCountDownTimer,
    @ApplicationContext private val context: Context
) : EyeCareUiCountDownTimer {

    private val coroutineScope = CoroutineScope(Dispatchers.Main.immediate)

    private val timer =
        MutableStateFlow<EyeCareUiCountDownTimer.AlarmState>(EyeCareUiCountDownTimer.AlarmState.NotStarted)

    private var mAlarmData: ScheduledAlarmCache.AlarmData? = null

    private val gazeTimerBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {
            Timber.d("onReceive: " + p1?.action)
            if (mAlarmData?.wasCompleted == true) {
                when (p1?.action) {
                    ACTION_GAZE_TIMER_IN_PROGRESS -> {
                        val gazeSeconds = p1.getLongExtra(
                            EXTRA_GAZE_TIMER_REMAINING_SECONDS, 0L
                        )
                        val percentage =
                            (100 - ((gazeSeconds * 100) / TimeUnit.MILLISECONDS.toSeconds(GAZE_TIME))).toInt()
                        Timber.d("Percentage:$gazeSeconds")
                        timer.value =
                            EyeCareUiCountDownTimer.AlarmState.InProgressRest("", percentage)
                    }
                    ACTION_GAZE_TIMER_COMPLETED -> {
                        timer.value = EyeCareUiCountDownTimer.AlarmState.InProgressRest("", 100)
                    }
                    ACTION_GAZE_TIMER_CANCELLED -> {
                        timer.value = EyeCareUiCountDownTimer.AlarmState.NotStarted
                    }
                }
            }
        }
    }

    override fun getCountDownTimerData(): MutableStateFlow<EyeCareUiCountDownTimer.AlarmState> {
        return timer
    }

    override fun clear() {
        coroutineScope.coroutineContext.cancelChildren()
        localBroadcastManager.unregisterReceiver(gazeTimerBroadcastReceiver)
    }

    private fun listen20SecondGazeCountDownFromService() {
        localBroadcastManager.registerReceiver(
            gazeTimerBroadcastReceiver, IntentFilter().apply {
                addAction(ACTION_GAZE_TIMER_CANCELLED)
                addAction(ACTION_GAZE_TIMER_COMPLETED)
                addAction(ACTION_GAZE_TIMER_IN_PROGRESS)
            }
        )
    }

    private fun update202020RemainingTime() {
        coroutineScope.launch {
            scheduledAlarmCache.getScheduledAlarmData().collect { alarmData ->
                Timber.d("AlarmData:" + alarmData?.toString())
                mAlarmData = alarmData
                if (alarmData != null && alarmData.triggerAtMillis > System.currentTimeMillis()) {
                    val millisInFuture = alarmData.triggerAtMillis - System.currentTimeMillis()
                    myCountDownTimer.start(millisInFuture = millisInFuture,
                        onTimerTick = { ticker, remainingMillis ->
                            if (context.isServiceRunning(EyeCarePersistentForegroundService::class.java)) {
                                val minutes = (remainingMillis / 1000) / 60
                                val seconds = (remainingMillis / 1000) % 60
                                val mPercentage =
                                    100 - ((remainingMillis * 100) / ALARM_TIME_INTERVAL_202020_RULE).toInt()

                                timer.value = EyeCareUiCountDownTimer.AlarmState.InProgressWork(
                                    String.format("%02d:%02d", minutes, seconds), mPercentage
                                )
                            } else {
                                myCountDownTimer.stop()
                            }
                        },
                        onCancelled = {
                            timer.value = EyeCareUiCountDownTimer.AlarmState.NotStarted
                        })
                }
            }
        }
    }

    init {
        listen20SecondGazeCountDownFromService()
        update202020RemainingTime()
    }
}