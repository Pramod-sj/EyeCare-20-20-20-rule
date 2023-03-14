package com.pramod.eyecare.framework.ui

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.pramod.eyecare.business.Constant
import com.pramod.eyecare.business.Constant.ALARM_TIME_INTERVAL_202020_RULE
import com.pramod.eyecare.business.EyeCareUiCountDownTimer
import com.pramod.eyecare.business.ScheduledAlarmCache
import com.pramod.eyecare.framework.helper.MyCountDownTimer
import com.pramod.eyecare.framework.impl.ScheduledAlarmCacheImpl
import com.pramod.eyecare.framework.service.EyeCarePersistentForegroundService
import com.pramod.eyecare.framework.service.NotificationForegroundService
import com.pramod.eyecare.framework.ui.utils.isServiceRunning
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class EyeCareUiCountDownTimerImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val myCountDownTimer: MyCountDownTimer,
    private val scheduledAlarmCache: ScheduledAlarmCache,
    private val localBroadcastManager: LocalBroadcastManager,
) : EyeCareUiCountDownTimer {

    private val timer =
        MutableStateFlow<EyeCareUiCountDownTimer.AlarmState>(EyeCareUiCountDownTimer.AlarmState.NotStarted)

    private val gazePercentage = MutableStateFlow(0)

    private val coroutineScope = CoroutineScope(Dispatchers.Main.immediate)

    private var mPercentage = 0

    private var mCacheAlarmData: ScheduledAlarmCacheImpl.AlarmData? = null

    private val gazeTimerBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {
            Timber.d("onReceive: ")
            when (p1?.action) {
                NotificationForegroundService.ACTION_GAZE_TIMER_IN_PROGRESS -> {
                    val gazeSeconds = p1.getLongExtra(
                        NotificationForegroundService.EXTRA_GAZE_TIMER_REMAINING_SECONDS, 0L
                    )
                    gazePercentage.value = (100 - ((gazeSeconds * 100) / 20)).toInt()
                }
                NotificationForegroundService.ACTION_GAZE_TIMER_COMPLETED -> {
                    gazePercentage.value = 100
                }
                NotificationForegroundService.ACTION_GAZE_TIMER_CANCELLED -> {

                }
            }
        }
    }

    override fun getCountDownTimerData(): MutableStateFlow<EyeCareUiCountDownTimer.AlarmState> {
        return timer
    }

    override fun get20SecondsGazePercentageValue(): MutableStateFlow<Int> {
        return gazePercentage
    }

    override fun clear() {
        localBroadcastManager.unregisterReceiver(gazeTimerBroadcastReceiver)
        myCountDownTimer.stop()
        coroutineScope.coroutineContext.cancelChildren()
    }

    private fun listen20SecondGazeCountDownFromService() {
        localBroadcastManager.registerReceiver(
            gazeTimerBroadcastReceiver, IntentFilter().apply {
                addAction(NotificationForegroundService.ACTION_GAZE_TIMER_CANCELLED)
                addAction(NotificationForegroundService.ACTION_GAZE_TIMER_COMPLETED)
                addAction(NotificationForegroundService.ACTION_GAZE_TIMER_IN_PROGRESS)
            }
        )
    }

    private fun update202020RemainingTime() {
        coroutineScope.launch {
            scheduledAlarmCache.getScheduledAlarmData().collect { alarmData ->
                Timber.d("AlarmData:" + alarmData?.toString())
                if (alarmData?.wasCancelled == true) {
                    timer.value = EyeCareUiCountDownTimer.AlarmState.Cancelled
                } else if (alarmData?.wasCompleted == true) {
                    timer.value = EyeCareUiCountDownTimer.AlarmState.Completed
                } else if (alarmData?.isStarted == true) {
                    val millisInFuture = alarmData.triggerAtMillis - System.currentTimeMillis()
                    myCountDownTimer.start(millisInFuture = millisInFuture,
                        onTimerTick = { ticker, remainingMillis ->
                            if (context.isServiceRunning(EyeCarePersistentForegroundService::class.java)) {
                                val minutes = (remainingMillis / 1000) / 60
                                val seconds = (remainingMillis / 1000) % 60
                                mPercentage =
                                    100 - ((remainingMillis * 100) / ALARM_TIME_INTERVAL_202020_RULE).toInt()

                                timer.value = EyeCareUiCountDownTimer.AlarmState.InProgress(
                                    String.format("%02d:%02d", minutes, seconds), mPercentage
                                )
                            } else {
                                myCountDownTimer.stop()
                            }
                        },
                        onCompleted = {
                            timer.value = EyeCareUiCountDownTimer.AlarmState.Completed
                        },
                        onCancelled = {
                            timer.value = EyeCareUiCountDownTimer.AlarmState.Cancelled
                        })
                } else {
                    timer.value = EyeCareUiCountDownTimer.AlarmState.NotStarted
                }
                mCacheAlarmData = alarmData
            }
        }
    }

    init {
        listen20SecondGazeCountDownFromService()
        update202020RemainingTime()
    }
}