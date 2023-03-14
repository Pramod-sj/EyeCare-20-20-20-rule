package com.pramod.eyecare.business

import android.app.AlarmManager

interface PersistentAlarmScheduler {

    companion object {

        const val ACTION_ON_ALARM_RECEIVE = "com.pramod.eyecare.service.ON_ALARM_RECEIVE"

    }

    interface AlarmReceivedListener {
        fun onAlarmReceived()
    }

    /**
     * Delegate method for onStart() of service
     */
    fun onStart()

    /**
     * Schedule alarm with given time i.e. triggerAtMillis
     * and repeat itself with interval if repeat is true
     * and store alarm data in local cache
     * @param triggerAtMillis alarm to be trigger at
     * @param interval interval between repeating alarms
     * @param repeat should be repeat or not
     */
    suspend fun schedule(
        triggerAtMillis: Long = System.currentTimeMillis() + Constant.ALARM_TIME_INTERVAL_202020_RULE,
        interval: Long = Constant.ALARM_TIME_INTERVAL_202020_RULE,
        repeat: Boolean = false,
    )

    /**
     * Restore alarm if any alarm exists in local cache
     */
    suspend fun restore()

    /**
     * Stop/cancel alarm and clear from local cache
     */
    suspend fun cancel()


    /**
     * Delegate method for onDestroy() of service
     */
    fun onDestroy()

    fun registerAlarmReceivedListener(alarmReceivedListener: AlarmReceivedListener)

    fun unregisterAlarmReceivedListener(alarmReceivedListener: AlarmReceivedListener)
}