package com.pramod.eyecare.framework.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.lifecycle.LifecycleService
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.pramod.eyecare.framework.helper.*
import com.pramod.eyecare.framework.ui.MainActivity
import com.pramod.eyecare.framework.ui.utils.isServiceRunning
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class NotificationForegroundService : LifecycleService(), NotificationActionCallback {

    companion object {

        const val ACTION_NOTIFICATION_GAZE_SHOWN =
            "com.pramod.eyecare.framework.service.NotificationForegroundService.ACTION_NOTIFICATION_GAZE_SHOWN"

        //region actions and extra declaration for the notification shown for gazing
        const val ACTION_GAZE_TIMER_IN_PROGRESS =
            "com.pramod.eyecare.framework.service.NotificationForegroundService.ACTION_NOTIFICATION_GAZE_IN_PROGRESS"
        const val ACTION_GAZE_TIMER_COMPLETED =
            "com.pramod.eyecare.framework.service.NotificationForegroundService.ACTION_NOTIFICATION_GAZE_COMPLETED"
        const val ACTION_GAZE_TIMER_CANCELLED =
            "com.pramod.eyecare.framework.service.NotificationForegroundService.ACTION_NOTIFICATION_GAZE_CANCELLED"
        const val EXTRA_GAZE_TIMER_REMAINING_SECONDS = "extra_gaze_timer_remaining_seconds"
        //endregion

        const val NOTIFICATION_ID_202020_REMINDER = "202020_reminder"

        const val ACTION_BUTTON_DO_NOT_REMIND_AGAIN =
            "com.pramod.eyecare.202020_reminder.ACTION_BUTTON_DO_NOT_REMIND_AGAIN"

        const val ACTION_BUTTON_DISMISS = "com.pramod.eyecare.202020_reminder.ACTION_BUTTON_DISMISS"

        private val NOTIFICATION_ID = NOTIFICATION_ID_202020_REMINDER.hashCode()

        fun startService(context: Context) {
            if (!context.isServiceRunning(NotificationForegroundService::class.java)) {
                Timber.d("startService")
                context.startService(
                    Intent(context, NotificationForegroundService::class.java)
                )
            }
        }

        fun stopService(context: Context) {
            if (context.isServiceRunning(NotificationForegroundService::class.java)) {
                context.stopService(
                    Intent(context, NotificationForegroundService::class.java)
                )
            }
        }
    }

    //region defining notification title desc and channel etc
    private val title = "Okay now let's look away!"
    private val desc = "It's the time to view away from your laptop or phone screen"
    private val channel = NotificationHelper.NotificationChannel.REMINDER_202020
    //endregion

    //region broadcast receiver for 20 sec timer started when notification is shown
    private val gazeTimerBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {
            Timber.d("onReceive: " + p1?.action)
            when (p1?.action) {
                ACTION_GAZE_TIMER_IN_PROGRESS -> {
                    val gazeSecondRemaining =
                        p1.getLongExtra(EXTRA_GAZE_TIMER_REMAINING_SECONDS, 0L)
                    Timber.d("gazeSecondRemaining:$gazeSecondRemaining")
                    val pendingIntent =
                        Intent(p0, MainActivity::class.java).toActivityPendingIntent(
                            context = applicationContext, requestCode = NOTIFICATION_ID
                        )
                    val buttons = arrayOf(
                        NotificationHelper.NotificationButton(
                            text = "Dismiss", pendingIntent = Intent(
                                ACTION_BUTTON_DISMISS
                            ).toBroadcastPendingIntent(
                                applicationContext, ACTION_BUTTON_DISMISS.hashCode()
                            )
                        ), NotificationHelper.NotificationButton(
                            text = "Don't remind me", pendingIntent = Intent(
                                ACTION_BUTTON_DO_NOT_REMIND_AGAIN
                            ).toBroadcastPendingIntent(
                                applicationContext, ACTION_BUTTON_DO_NOT_REMIND_AGAIN.hashCode()
                            )
                        )
                    )
                    notificationHelper.showNotification(
                        id = NOTIFICATION_ID, notification = notificationHelper.buildNotification(
                            title = title + " (${gazeSecondRemaining} seconds remaining)",
                            desc = desc,
                            pendingIntent = pendingIntent,
                            buttons = buttons,
                            notificationChannel = channel,
                            isOnGoing = true
                        )
                    )
                }
                ACTION_GAZE_TIMER_COMPLETED, ACTION_GAZE_TIMER_CANCELLED -> {
                    vibrationHelper.vibrate(400)
                    stopNotificationService()
                }
            }
        }

    }
    //endregion

    //region injecting helper objects
    @Inject
    lateinit var localBroadcastManager: LocalBroadcastManager

    @Inject
    lateinit var notificationHelper: NotificationHelper

    @Inject
    lateinit var vibrationHelper: VibrationHelper
    //endregion

    override fun onCreate() {
        super.onCreate()
        localBroadcastManager.registerReceiver(
            gazeTimerBroadcastReceiver,
            IntentFilter().apply {
                addAction(ACTION_GAZE_TIMER_CANCELLED)
                addAction(ACTION_GAZE_TIMER_COMPLETED)
                addAction(ACTION_GAZE_TIMER_IN_PROGRESS)
            }
        )
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        showForegroundNotification()
        localBroadcastManager.sendBroadcast(Intent(ACTION_NOTIFICATION_GAZE_SHOWN))
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent): IBinder? {
        super.onBind(intent)
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        localBroadcastManager.unregisterReceiver(gazeTimerBroadcastReceiver)
        notificationHelper.unregisterListenerNotificationButtonClick(this)
    }

    override fun onActionPerformed(action: String, intent: Intent?) {
        when (action) {
            ACTION_BUTTON_DO_NOT_REMIND_AGAIN -> {
                EyeCarePersistentForegroundService.stopService(applicationContext)
            }
            ACTION_BUTTON_DISMISS -> {
                stopNotificationService()
            }
            else -> Unit
        }
    }

    private fun showForegroundNotification() {
        notificationHelper.registerListenerNotificationButtonClick(
            notificationActionCallback = this,
            actions = listOf(ACTION_BUTTON_DO_NOT_REMIND_AGAIN, ACTION_BUTTON_DISMISS)
        )
        val pendingIntent = Intent(this, MainActivity::class.java).toActivityPendingIntent(
            context = applicationContext, requestCode = NOTIFICATION_ID
        )

        val buttons = arrayOf(
            NotificationHelper.NotificationButton(
                text = "Dismiss", pendingIntent = Intent(
                    ACTION_BUTTON_DISMISS
                ).toBroadcastPendingIntent(
                    applicationContext, ACTION_BUTTON_DISMISS.hashCode()
                )
            ), NotificationHelper.NotificationButton(
                text = "Don't remind me", pendingIntent = Intent(
                    ACTION_BUTTON_DO_NOT_REMIND_AGAIN
                ).toBroadcastPendingIntent(
                    applicationContext, ACTION_BUTTON_DO_NOT_REMIND_AGAIN.hashCode()
                )
            )
        )

        val foregroundNotification = notificationHelper.buildNotification(
            title = title,
            desc = desc,
            pendingIntent = pendingIntent,
            buttons = buttons,
            notificationChannel = channel,
            vibrate = true
        )
        startForeground(NOTIFICATION_ID, foregroundNotification)
    }

    private fun stopNotificationService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            stopForeground(STOP_FOREGROUND_REMOVE)
        } else {
            stopForeground(true)
        }
        stopSelf()
    }

}