package com.pramod.eyecare.framework.impl

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.pramod.eyecare.business.PersistentAlarmScheduler
import com.pramod.eyecare.business.PersistentAlarmScheduler.Companion.ACTION_ON_ALARM_RECEIVE
import com.pramod.eyecare.business.domain.data.preference.ScheduledAlarmCache
import com.pramod.eyecare.framework.service.EyeCarePersistentForegroundService.Companion.ACTION_GAZE_TIMER_COMPLETED
import com.pramod.eyecare.framework.ui.utils.MyObserver
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ServiceScoped
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.firstOrNull
import timber.log.Timber
import javax.inject.Inject

@ServiceScoped
class PersistentAlarmSchedulerImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val alarmManager: AlarmManager,
    private val scheduledAlarmCache: ScheduledAlarmCache,
    private val localBroadcastManager: LocalBroadcastManager,
) : PersistentAlarmScheduler, DefaultLifecycleObserver,
    MyObserver<PersistentAlarmScheduler.AlarmReceivedListener>() {

    companion object {
        private const val TAG = "PersistentAlarmSchedule"
    }


    private val coroutineScope = CoroutineScope(Dispatchers.Main.immediate)

    private var alarmPendingIntent: PendingIntent? = null

    //region broadcast receiver for 20 sec timer started when notification is shown
    private val gazeTimerBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {
            if (p1?.action == ACTION_GAZE_TIMER_COMPLETED) {
                coroutineScope.launch {
                    scheduledAlarmCache.getScheduledAlarmData().firstOrNull()?.let { alarmData ->
                        if (alarmData.repeat) {
                            Timber.tag(TAG).i("onReceive: Scheduling a new alarm")
                            schedule(
                                triggerAtMillis = System.currentTimeMillis() + alarmData.alarmInterval,
                                interval = alarmData.alarmInterval,
                                repeat = true
                            )
                        }
                    }
                }
            }
        }
    }
    //endregion

    private val alarmBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {
            if (p1?.action == ACTION_ON_ALARM_RECEIVE) {
                coroutineScope.launch {
                    withContext(Dispatchers.IO) {
                        scheduledAlarmCache.getScheduledAlarmData().firstOrNull()
                            ?.let { alarmData ->
                                scheduledAlarmCache.storeAlarmData(alarmData.copy(wasCompleted = true))
                            }
                        listeners.forEach { it.onAlarmReceived() }
                    }
                }
            }
        }
    }

    override fun onStart() {
        context.registerReceiver(alarmBroadcastReceiver, IntentFilter(ACTION_ON_ALARM_RECEIVE))
        localBroadcastManager.registerReceiver(
            gazeTimerBroadcastReceiver, IntentFilter(ACTION_GAZE_TIMER_COMPLETED)
        )
    }

    override suspend fun schedule(triggerAtMillis: Long, interval: Long, repeat: Boolean) {
        withContext(Dispatchers.IO) {
            Timber.d("scheduleAlarm: new")
            scheduledAlarmCache.storeAlarmData(
                ScheduledAlarmCache.AlarmData(
                    triggerAtMillis = triggerAtMillis,
                    repeat = repeat,
                    alarmInterval = interval,
                    isStarted = true
                )
            )
            setAlarm(triggerAtMillis, repeat)
        }
    }

    override suspend fun restore() {
        withContext(Dispatchers.IO) {
            val scheduleAlarmData = scheduledAlarmCache.getScheduledAlarmData().firstOrNull()
            if (scheduleAlarmData != null) {
                Timber.d("restoreAlarm: restore")
                setAlarm(
                    scheduleAlarmData.triggerAtMillis,
                    scheduleAlarmData.repeat
                )
            } else {
                Timber.d("restoreAlarm: no alarm data to restore!")
            }
        }
    }

    override suspend fun cancel() {
        withContext(Dispatchers.IO) {
            Timber.d("stopAlarms:cancel")
            scheduledAlarmCache.getScheduledAlarmData().firstOrNull()?.let { data ->
                scheduledAlarmCache.storeAlarmData(alarmData = data.copy(wasCancelled = true))
            }
            alarmPendingIntent?.let { alarmManager.cancel(alarmPendingIntent) }
        }
    }

    override fun onDestroy() {
        context.unregisterReceiver(alarmBroadcastReceiver)
        localBroadcastManager.unregisterReceiver(gazeTimerBroadcastReceiver)
        listeners.clear()
        coroutineScope.coroutineContext.cancelChildren()
    }

    override fun registerAlarmReceivedListener(alarmReceivedListener: PersistentAlarmScheduler.AlarmReceivedListener) {
        registerObserver(alarmReceivedListener)
    }

    override fun unregisterAlarmReceivedListener(alarmReceivedListener: PersistentAlarmScheduler.AlarmReceivedListener) {
        unregisterObserver(alarmReceivedListener)
    }

    private fun setAlarm(
        triggerAtMillis: Long, repeat: Boolean,
    ) {
        Timber.d("setAlarm: $triggerAtMillis; repeat:$repeat")
        alarmPendingIntent = PendingIntent.getBroadcast(
            /* context = */ context,
            /* requestCode = */ ACTION_ON_ALARM_RECEIVE.hashCode(),
            /* intent = */ Intent(ACTION_ON_ALARM_RECEIVE),
            /* flags = */ PendingIntent.FLAG_CANCEL_CURRENT + PendingIntent.FLAG_IMMUTABLE
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            /*alarmManager.setExactAndAllowWhileIdle(
                *//* type = *//* AlarmManager.RTC_WAKEUP,
                *//* triggerAtMillis = *//* System.currentTimeMillis() + alarmInterval,
                *//* operation = *//* alarmPendingIntent
            )*/
            alarmManager.setAlarmClock(
                AlarmManager.AlarmClockInfo(
                    /* triggerTime = */ triggerAtMillis,
                    /* showIntent = */ null
                ), alarmPendingIntent
            )
        } else {
            alarmManager.setExact(
                /* type = */ AlarmManager.RTC_WAKEUP,
                /* triggerAtMillis = */ triggerAtMillis,
                /* operation = */ alarmPendingIntent
            )
        }
    }

}