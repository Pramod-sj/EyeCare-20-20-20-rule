package com.pramod.eyecare.framework.impl

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.google.gson.Gson
import com.pramod.eyecare.business.ScheduledAlarmCache
import com.pramod.eyecare.framework.appDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ScheduledAlarmCacheImpl @Inject constructor(
    @ApplicationContext private val context: Context,
) : ScheduledAlarmCache {

    data class AlarmData(
        val triggerAtMillis: Long,
        val alarmInterval: Long,
        val repeat: Boolean,

        val wasCompleted: Boolean = false,
        val wasCancelled: Boolean = false,
        val isStarted: Boolean = false,
    )

    private val scheduleAlarmDataKey = stringPreferencesKey("scheduleAlarmDataKey")

    private val gson = Gson()

    override fun getScheduledAlarmData(): Flow<AlarmData?> {
        return context.appDataStore.data.map { pref ->
            gson.fromJson(
                pref[scheduleAlarmDataKey], AlarmData::class.java
            )
        }
    }

    override suspend fun setAlarmData(alarmData: AlarmData) {
        withContext(Dispatchers.IO) {
            context.appDataStore.edit { pref ->
                pref[scheduleAlarmDataKey] = gson.toJson(alarmData)
            }
        }
    }

    override suspend fun remove() {
        withContext(Dispatchers.IO) {
            context.appDataStore.edit { pref -> pref.remove(scheduleAlarmDataKey) }
        }
    }
}