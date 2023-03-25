package com.pramod.eyecare.framework.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_BOOT_COMPLETED
import com.pramod.eyecare.business.domain.data.preference.ScheduledAlarmCache
import com.pramod.eyecare.framework.service.EyeCarePersistentForegroundService
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class BootReceiver : BroadcastReceiver() {

    @Inject
    lateinit var alarmPoint: ScheduledAlarmCache

    override fun onReceive(p0: Context?, p1: Intent?) {
        if (p1?.action == ACTION_BOOT_COMPLETED) {
            Timber.d("Boot complete received!")
            p0?.let { context ->
                CoroutineScope(Main.immediate).launch {
                    alarmPoint.getScheduledAlarmData().firstOrNull()?.let { alarmData ->
                        if (!alarmData.wasCompleted) {
                            EyeCarePersistentForegroundService.startService(context, true)
                        }
                    }
                }
            }
        }
    }

}