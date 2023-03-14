package com.pramod.eyecare.framework.helper

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import androidx.core.app.NotificationCompat.*
import com.pramod.eyecare.R
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.concurrent.ConcurrentLinkedQueue
import javax.inject.Inject

fun Intent.toActivityPendingIntent(
    context: Context,
    requestCode: Int,
): PendingIntent {
    return PendingIntent.getActivity(
        context, requestCode, this, PendingIntent.FLAG_IMMUTABLE
    )
}

fun Intent.toBroadcastPendingIntent(
    context: Context,
    requestCode: Int,
    flag: Int = PendingIntent.FLAG_IMMUTABLE,
): PendingIntent {
    return PendingIntent.getBroadcast(
        context, requestCode, this, PendingIntent.FLAG_IMMUTABLE
    )
}


interface NotificationActionCallback {
    fun onActionPerformed(action: String, intent: Intent?)
}

class NotificationHelper @Inject constructor(
    @ApplicationContext private val context: Context,
    private val notificationManager: NotificationManager,
) {

    companion object {

        private const val TAG = "NotificationHelper"

        const val CHANNEL_ID_DEFAULT = "notification_deault"
        const val CHANNEL_NAME_DEFAULT = "Default"

        const val CHANNEL_ID_SERVICE_RUNNING_IMPORTANT_LOW = "notification_service_running_status"
        const val CHANNEL_NAME_SERVICE_RUNNING_IMPORTANT_LOW = "Service running status"

        const val CHANNEL_ID_202020_REMINDER_IMPORTANT_HIGH = "notification_20_20_20_Reminder"
        const val CHANNEL_NAME_202020_REMINDER_IMPORTANT_HIGH = "20-20-20 Reminder"
    }

    enum class NotificationChannel(
        val id: String,
        val channelName: String,
    ) {
        DEFAULT(CHANNEL_ID_DEFAULT, CHANNEL_NAME_DEFAULT), SERVICE_RUNNING(
            CHANNEL_ID_SERVICE_RUNNING_IMPORTANT_LOW, CHANNEL_NAME_SERVICE_RUNNING_IMPORTANT_LOW
        ),
        REMINDER_202020(
            CHANNEL_ID_202020_REMINDER_IMPORTANT_HIGH, CHANNEL_NAME_202020_REMINDER_IMPORTANT_HIGH
        )
    }

    data class NotificationButton(val text: String, val pendingIntent: PendingIntent)

    private var isAnyBroadcastReceiverRegistered = false

    private val intentActionFilter = IntentFilter()

    private val listeners = ConcurrentLinkedQueue<NotificationActionCallback>()

    //region creating notification channels
    private val defaultNotificationChannel = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        NotificationChannel(
            CHANNEL_ID_DEFAULT, CHANNEL_NAME_DEFAULT, NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            enableVibration(true)
        }
    } else null

    private val reminding202020NotificationChannel =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel(
                CHANNEL_ID_202020_REMINDER_IMPORTANT_HIGH,
                CHANNEL_NAME_202020_REMINDER_IMPORTANT_HIGH,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                enableVibration(true)
            }
        } else null

    private val serviceRunningNotificationChannel =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel(
                CHANNEL_ID_SERVICE_RUNNING_IMPORTANT_LOW,
                CHANNEL_NAME_SERVICE_RUNNING_IMPORTANT_LOW,
                NotificationManager.IMPORTANCE_LOW
            )
        } else null
    //endregion creating notification channels

    private val notificationActionBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {
            p1?.let { intent ->
                listeners.forEach { it.onActionPerformed(intent.action.orEmpty(), intent) }
            }
        }
    }

    fun registerListenerNotificationButtonClick(
        notificationActionCallback: NotificationActionCallback,
        actions: List<String>,
    ) {
        listeners.add(notificationActionCallback)
        updateIntentFilterAndRegister(actions)
    }

    fun unregisterListenerNotificationButtonClick(notificationActionCallback: NotificationActionCallback) {
        listeners.remove(notificationActionCallback)
    }

    fun buildNotification(
        title: String,
        desc: String,
        pendingIntent: PendingIntent,
        notificationChannel: NotificationChannel,
        isOnGoing: Boolean = false,
        buttons: Array<NotificationButton> = emptyArray(),
        vibrate: Boolean = false,
    ): Notification {
        return Builder(
            context, notificationChannel.id
        ).setContentTitle(title)//"EyeCare service is running")
            .setContentText(desc)//"Please don't remove this notification its improtant for correct functioning!")
            .setSmallIcon(R.drawable.ic_launcher_foreground).setContentIntent(pendingIntent).apply {
                priority = when (notificationChannel) {
                    NotificationChannel.DEFAULT -> PRIORITY_DEFAULT
                    NotificationChannel.SERVICE_RUNNING -> PRIORITY_LOW
                    NotificationChannel.REMINDER_202020 -> PRIORITY_MAX
                }
                buttons.forEach { addAction(-1, it.text, it.pendingIntent) }
                if (vibrate) {
                    setVibrate(arrayOf(0L, 200, 200, 400).toLongArray())
                }
            }.setOngoing(isOnGoing).setOnlyAlertOnce(isOnGoing).build()
    }

    fun showNotification(id: Int, notification: Notification) {
        notificationManager.notify(id, notification)
    }

    fun cancelNotification(id: Int) {
        notificationManager.cancel(id)
    }

    private fun updateIntentFilterAndRegister(actions: List<String>) {
        intentActionFilter.apply {
            actions.forEach { action ->
                if (!intentActionFilter.hasAction(action)) {
                    addAction(action)
                }
            }
        }
        if (isAnyBroadcastReceiverRegistered) {
            context.unregisterReceiver(notificationActionBroadcastReceiver)
        }
        context.registerReceiver(notificationActionBroadcastReceiver, intentActionFilter)
        isAnyBroadcastReceiverRegistered = true
    }

    fun clear() {
        context.unregisterReceiver(notificationActionBroadcastReceiver)
    }

    init {
        //region create notification channel for android O and greater devices
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            defaultNotificationChannel?.let {
                notificationManager.createNotificationChannel(it)
            }
            serviceRunningNotificationChannel?.let {
                notificationManager.createNotificationChannel(it)
            }
            reminding202020NotificationChannel?.let {
                notificationManager.createNotificationChannel(it)
            }
        }
    }
}
