package com.pramod.eyecare.business

import com.pramod.eyecare.framework.impl.ScheduledAlarmCacheImpl
import kotlinx.coroutines.flow.Flow


/**
 * This class will help to get any cached alarm time (inMillis)
 */
interface ScheduledAlarmCache {

    fun getScheduledAlarmData(): Flow<ScheduledAlarmCacheImpl.AlarmData?>

    suspend fun setAlarmData(alarmData: ScheduledAlarmCacheImpl.AlarmData)

    suspend fun remove()
}