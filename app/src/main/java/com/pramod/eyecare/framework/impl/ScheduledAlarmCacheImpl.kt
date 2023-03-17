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

    private val scheduleAlarmDataKey = stringPreferencesKey("scheduleAlarmDataKey")

    private val gson = Gson()

    override fun getScheduledAlarmData(): Flow<ScheduledAlarmCache.AlarmData?> {
        return context.appDataStore.data.map { pref ->
            gson.fromJson(
                pref[scheduleAlarmDataKey], ScheduledAlarmCache.AlarmData::class.java
            )
        }
    }

    override suspend fun setAlarmData(alarmData: ScheduledAlarmCache.AlarmData) {
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