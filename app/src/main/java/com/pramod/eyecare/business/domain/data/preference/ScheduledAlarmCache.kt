package com.pramod.eyecare.business.domain.data.preference

import kotlinx.coroutines.flow.Flow


/**
 * This class will help to get any cached alarm time (inMillis)
 */
interface ScheduledAlarmCache {
    data class AlarmData(
        val triggerAtMillis: Long,
        val alarmInterval: Long,
        val repeat: Boolean,
        val wasCompleted: Boolean = false,
        val wasCancelled: Boolean = false,
        val isStarted: Boolean = false,
    )

    fun getScheduledAlarmData(): Flow<AlarmData?>

    suspend fun storeAlarmData(alarmData: AlarmData)

    suspend fun remove()
}