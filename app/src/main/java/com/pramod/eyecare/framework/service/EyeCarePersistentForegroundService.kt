package com.pramod.eyecare.framework.service

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.IBinder
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.pramod.eyecare.business.Constant.ALARM_TIME_INTERVAL_202020_RULE
import com.pramod.eyecare.business.Constant.GAZE_TIME
import com.pramod.eyecare.business.PersistentAlarmScheduler
import com.pramod.eyecare.framework.helper.*
import com.pramod.eyecare.framework.ui.MainActivity
import com.pramod.eyecare.framework.ui.utils.isServiceRunning
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@AndroidEntryPoint
class EyeCarePersistentForegroundService : LifecycleService(), NotificationActionCallback {

    companion object {
        private const val TAG = "NotificationAlertForegr"

        const val ACTION_BUTTON_STOP_SERVICE =
            "com.pramod.eyecare.framework.ACTION_BUTTON_STOP_SERVICE"

        const val EXTRA_RESTORE_ALARM = "extra_restore_alarm"

        fun startService(context: Context, restoreAlarm: Boolean = false) {
            if (!context.isServiceRunning(EyeCarePersistentForegroundService::class.java)) {
                context.startService(Intent(
                    context, EyeCarePersistentForegroundService::class.java
                ).apply { putExtra(EXTRA_RESTORE_ALARM, restoreAlarm) })
            }
        }

        fun stopService(context: Context) {
            if (context.isServiceRunning(EyeCarePersistentForegroundService::class.java)) {
                context.stopService(
                    Intent(
                        context, EyeCarePersistentForegroundService::class.java
                    )
                )
            }
        }

    }

    //region broadcast receiver when gaze notification is shown
    private val notificationShown202020ReminderReceiver = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {
            if (p1?.action == NotificationForegroundService.ACTION_NOTIFICATION_GAZE_SHOWN) {
                myCountDownTimer.start(onTimerTick = { ticker, remainingMillis ->
                    if (isServiceRunning(EyeCarePersistentForegroundService::class.java)) {
                        val gazeSecondRemaining = TimeUnit.MILLISECONDS.toSeconds(remainingMillis)
                        localBroadcastManager.sendBroadcast(
                            Intent(NotificationForegroundService.ACTION_GAZE_TIMER_IN_PROGRESS).apply {
                                putExtra(
                                    NotificationForegroundService.EXTRA_GAZE_TIMER_REMAINING_SECONDS,
                                    gazeSecondRemaining
                                )
                            })
                    }
                },
                    onCompleted = {
                        if (isServiceRunning(EyeCarePersistentForegroundService::class.java)) {
                            localBroadcastManager.sendBroadcast(Intent(NotificationForegroundService.ACTION_GAZE_TIMER_COMPLETED))
                        }
                    },
                    millisInFuture = GAZE_TIME,
                    onCancelled = {
                        if (isServiceRunning(EyeCarePersistentForegroundService::class.java)) {
                            localBroadcastManager.sendBroadcast(Intent(NotificationForegroundService.ACTION_GAZE_TIMER_CANCELLED))
                        }
                    })
            }
        }
    }
    //endregion

    //region inject helper objects
    @Inject
    lateinit var myCountDownTimer: MyCountDownTimer

    @Inject
    lateinit var notificationHelper: NotificationHelper

    @Inject
    lateinit var alarmScheduler: PersistentAlarmScheduler

    @Inject
    lateinit var localBroadcastManager: LocalBroadcastManager
    //endregion

    override fun onCreate() {
        super.onCreate()
        alarmScheduler.onStart()
        registerBroadcastReceiverForGazeNotificationShown()
        Timber.tag(TAG).i("onCreate: ")
    }

    private fun registerBroadcastReceiverForGazeNotificationShown() {
        localBroadcastManager.registerReceiver(
            notificationShown202020ReminderReceiver,
            IntentFilter(NotificationForegroundService.ACTION_NOTIFICATION_GAZE_SHOWN)
        )
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        Timber.d("Received start id $startId: $intent");
        lifecycleScope.launch {
            showForegroundNotification()
            alarmScheduler.registerAlarmReceivedListener(object :
                PersistentAlarmScheduler.AlarmReceivedListener {
                override fun onAlarmReceived() {
                    Timber.d("Alarm!!!")
                    NotificationForegroundService.startService(applicationContext)
                }
            })

            val triggerAtMillis = System.currentTimeMillis() + ALARM_TIME_INTERVAL_202020_RULE

            if (intent?.extras?.getBoolean(EXTRA_RESTORE_ALARM) == true) {
                alarmScheduler.restore()
            } else {
                alarmScheduler.schedule(
                    triggerAtMillis = triggerAtMillis,
                    interval = ALARM_TIME_INTERVAL_202020_RULE,
                    repeat = true,
                )
            }
        }
        return START_NOT_STICKY
    }

    private fun showForegroundNotification() {
        notificationHelper.registerListenerNotificationButtonClick(
            this, listOf(ACTION_BUTTON_STOP_SERVICE)
        )
        val id = 1
        val notification = notificationHelper.buildNotification(
            title = "EyeCare service is running",
            desc = "Please don't remove this notification its important for correct functioning!",
            pendingIntent = Intent(this, MainActivity::class.java).toActivityPendingIntent(
                applicationContext, id
            ),
            buttons = arrayOf(
                NotificationHelper.NotificationButton(
                    text = "Stop service", pendingIntent = Intent(
                        ACTION_BUTTON_STOP_SERVICE
                    ).toBroadcastPendingIntent(
                        applicationContext,
                        System.currentTimeMillis().hashCode(),
                        PendingIntent.FLAG_UPDATE_CURRENT
                    )
                )
            ),
            notificationChannel = NotificationHelper.NotificationChannel.SERVICE_RUNNING,
        )
        startForeground(1, notification)
    }

    override fun onBind(intent: Intent): IBinder? {
        super.onBind(intent)
        return null
    }

    override fun onDestroy() {
        Timber.d("onDestroy: ")
        myCountDownTimer.stop()
        alarmScheduler.onDestroy()
        NotificationForegroundService.stopService(applicationContext)
        notificationHelper.clear()
        localBroadcastManager.unregisterReceiver(notificationShown202020ReminderReceiver)
        runBlocking { alarmScheduler.cancel() }
        super.onDestroy()
    }

    override fun onActionPerformed(action: String, intent: Intent?) {
        when (action) {
            ACTION_BUTTON_STOP_SERVICE -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    stopForeground(STOP_FOREGROUND_REMOVE)
                } else {
                    stopForeground(true)
                }
                stopSelf()
            }
            else -> Unit
        }
    }

}