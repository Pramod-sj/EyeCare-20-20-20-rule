package com.pramod.eyecare.framework.service

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.pramod.eyecare.business.Constant.ALARM_TIME_INTERVAL_202020_RULE
import com.pramod.eyecare.business.Constant.GAZE_TIME
import com.pramod.eyecare.business.CopyHelper
import com.pramod.eyecare.business.CopyHelper.Companion.NOTIFICATION_ACTION_STOP
import com.pramod.eyecare.business.CopyHelper.Companion.NOTIFICATION_RESTING_BODY
import com.pramod.eyecare.business.CopyHelper.Companion.NOTIFICATION_RESTING_TITLE
import com.pramod.eyecare.business.CopyHelper.Companion.NOTIFICATION_WORKING_BODY
import com.pramod.eyecare.business.CopyHelper.Companion.NOTIFICATION_WORKING_TITLE
import com.pramod.eyecare.business.PersistentAlarmScheduler
import com.pramod.eyecare.business.ScheduledAlarmCache
import com.pramod.eyecare.business.SettingPreference
import com.pramod.eyecare.framework.helper.*
import com.pramod.eyecare.framework.ui.MainActivity
import com.pramod.eyecare.framework.ui.utils.isServiceRunning
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@AndroidEntryPoint
class EyeCarePersistentForegroundService : LifecycleService(), NotificationActionCallback,
    PersistentAlarmScheduler.AlarmReceivedListener {

    companion object {

        private val NOTIFICATION_ID = "NOTIFICATION_ID".hashCode()

        private val NOTIFICATION_REMINDER_ID = "NOTIFICATION_REMINDER_ID".hashCode()

        private const val TAG = "NotificationAlertForegr"

        const val ACTION_BUTTON_STOP_SERVICE =
            "com.pramod.eyecare.framework.ACTION_BUTTON_STOP_SERVICE"

        const val EXTRA_RESTORE_ALARM = "extra_restore_alarm"

        //region actions and extra declaration for the notification shown for gazing
        const val ACTION_WORK_TIMER_IN_PROGRESS =
            "com.pramod.eyecare.framework.service.NotificationForegroundService.ACTION_WORK_TIMER_IN_PROGRESS"
        const val ACTION_WORK_TIMER_COMPLETED =
            "com.pramod.eyecare.framework.service.NotificationForegroundService.ACTION_WORK_TIMER_COMPLETED"
        const val ACTION_WORK_TIMER_CANCELLED =
            "com.pramod.eyecare.framework.service.NotificationForegroundService.ACTION_WORK_TIMER_CANCELLED"
        const val EXTRA_WORK_TIMER_REMAINING_SECONDS = "extra_gaze_timer_remaining_seconds"
        //endregion

        //region actions and extra declaration for the notification shown for gazing
        const val ACTION_GAZE_TIMER_IN_PROGRESS =
            "com.pramod.eyecare.framework.service.NotificationForegroundService.ACTION_NOTIFICATION_GAZE_IN_PROGRESS"
        const val ACTION_GAZE_TIMER_COMPLETED =
            "com.pramod.eyecare.framework.service.NotificationForegroundService.ACTION_NOTIFICATION_GAZE_COMPLETED"
        const val ACTION_GAZE_TIMER_CANCELLED =
            "com.pramod.eyecare.framework.service.NotificationForegroundService.ACTION_NOTIFICATION_GAZE_CANCELLED"
        const val EXTRA_GAZE_TIMER_REMAINING_SECONDS = "extra_gaze_timer_remaining_seconds"
        //endregion

        const val ACTION_BUTTON_DO_NOT_REMIND_AGAIN =
            "com.pramod.eyecare.202020_reminder.ACTION_BUTTON_DO_NOT_REMIND_AGAIN"

        const val ACTION_BUTTON_DISMISS = "com.pramod.eyecare.202020_reminder.ACTION_BUTTON_DISMISS"

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

    //region inject helper objects
    @Inject
    lateinit var workCountDownTimer: MyCountDownTimer

    @Inject
    lateinit var restingCountDownTimer: MyCountDownTimer

    @Inject
    lateinit var notificationHelper: NotificationHelper

    @Inject
    lateinit var alarmScheduler: PersistentAlarmScheduler

    @Inject
    lateinit var localBroadcastManager: LocalBroadcastManager

    @Inject
    lateinit var copyHelper: CopyHelper

    @Inject
    lateinit var scheduledAlarmCache: ScheduledAlarmCache

    @Inject
    lateinit var vibrationHelper: VibrationHelper

    @Inject
    lateinit var settingPreference: SettingPreference

    //endregion

    private var jobWorking: Job? = null

    private var jobResting: Job? = null

    override fun onCreate() {
        super.onCreate()
        alarmScheduler.onStart()
        notificationHelper.registerListenerNotificationButtonClick(
            this, listOf(ACTION_BUTTON_STOP_SERVICE)
        )
        Timber.tag(TAG).i("onCreate: ")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        lifecycleScope.launch {
            startForeground(
                NOTIFICATION_ID, notificationHelper.buildNotification(
                    title = copyHelper.getString(NOTIFICATION_WORKING_TITLE) { "EyeCare service is running" },
                    desc = copyHelper.getString(NOTIFICATION_WORKING_BODY) { "Please don't remove this notification its important for correct functioning!" },
                    pendingIntent = Intent(baseContext, MainActivity::class.java)
                        .toActivityPendingIntent(
                            applicationContext, NOTIFICATION_ID
                        ),
                    buttons = arrayOf(
                        NotificationHelper.NotificationButton(
                            text = copyHelper.getString(NOTIFICATION_ACTION_STOP) { "Stop service" },
                            pendingIntent = Intent(
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
            )
            startOrRestoreAlarm(intent)
        }
        return START_NOT_STICKY
    }

    private suspend fun startOrRestoreAlarm(intent: Intent?) {
        alarmScheduler.registerAlarmReceivedListener(this)
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

    override fun onBind(intent: Intent): IBinder? {
        super.onBind(intent)
        return null
    }

    override fun onDestroy() {
        Timber.d("onDestroy: ")
        jobWorking?.cancel()
        jobResting?.cancel()
        restingCountDownTimer.stop()
        workCountDownTimer.stop()
        alarmScheduler.onDestroy()
        notificationHelper.clear()
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

    private fun showNotificationAndStartRestTimer() {
        jobResting?.cancel()
        jobResting = lifecycleScope.launch {
            notificationHelper.showNotification(
                id = NOTIFICATION_REMINDER_ID,
                notification = getRestingNotification(20, false)
            )
            scheduledAlarmCache.getScheduledAlarmData().firstOrNull { it?.wasCompleted == true }
                ?.let { alarmData ->
                    restingCountDownTimer.start(
                        millisInFuture = GAZE_TIME,
                        onTimerTick = { ticker, remainingMillis ->
                            val gazeSecondRemaining =
                                TimeUnit.MILLISECONDS.toSeconds(remainingMillis)
                            localBroadcastManager.sendBroadcast(Intent(ACTION_GAZE_TIMER_IN_PROGRESS).apply {
                                putExtra(
                                    EXTRA_GAZE_TIMER_REMAINING_SECONDS,
                                    gazeSecondRemaining
                                )
                            })
                            notificationHelper.showNotification(
                                id = NOTIFICATION_REMINDER_ID,
                                notification = getRestingNotification(gazeSecondRemaining, true)
                            )
                        },
                        onCompleted = {
                            lifecycleScope.launch {
                                if (settingPreference.getPlayWorkRingtone().firstOrNull() == true) {
                                    notificationHelper.playNotificationDismissSound()
                                }
                            }
                            vibrationHelper.vibrate(400)
                            notificationHelper.cancelNotification(NOTIFICATION_REMINDER_ID)
                            localBroadcastManager.sendBroadcast(Intent(ACTION_GAZE_TIMER_COMPLETED))
                        },
                        onCancelled = {
                            notificationHelper.cancelNotification(NOTIFICATION_REMINDER_ID)
                            localBroadcastManager.sendBroadcast(Intent(ACTION_GAZE_TIMER_CANCELLED))
                        })
                }
        }

    }

    private fun getRestingNotification(
        gazeSecondRemaining: Long,
        isOnGoing: Boolean
    ): Notification {
        val pendingIntent = Intent(baseContext, MainActivity::class.java)
            .toActivityPendingIntent(context = applicationContext, requestCode = NOTIFICATION_ID)
        return notificationHelper.buildNotification(
            title = copyHelper.getString(NOTIFICATION_RESTING_TITLE) { "Okay now let's look away! ({remainingSec} secs remaining)" }
                .replace("{remainingSec}", gazeSecondRemaining.toString()),
            desc = copyHelper.getString(NOTIFICATION_RESTING_BODY) { "It's the time to view away from your laptop or phone screen" },
            pendingIntent = pendingIntent,
            buttons = arrayOf(
                NotificationHelper.NotificationButton(
                    text = copyHelper.getString(NOTIFICATION_ACTION_STOP) { "Stop service" },
                    pendingIntent = Intent(
                        ACTION_BUTTON_STOP_SERVICE
                    ).toBroadcastPendingIntent(
                        applicationContext,
                        System.currentTimeMillis().hashCode(),
                        PendingIntent.FLAG_UPDATE_CURRENT
                    )
                )
            ),
            notificationChannel = NotificationHelper.NotificationChannel.REMINDER_202020,
            isOnGoing = isOnGoing
        )
    }

    override fun onAlarmReceived() {
        Timber.d("Alarm!!!")
        showNotificationAndStartRestTimer()
    }

}