package com.pramod.eyecare.framework.datasource.local.datastore

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import com.pramod.eyecare.business.domain.data.preference.SettingPreference
import com.pramod.eyecare.framework.appDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SettingPreferenceImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : SettingPreference {

    private val dataStore = context.appDataStore

    private val playWorkRingtoneBoolean = booleanPreferencesKey(SettingPreference.KEY_PLAY_RINGTONE)

    override fun getPlayWorkRingtone(): Flow<Boolean> {
        return dataStore.data.map { it[playWorkRingtoneBoolean] ?: false }
    }

    override suspend fun setPlayWorkRingtone(play: Boolean) {
        dataStore.edit { it[playWorkRingtoneBoolean] = play }
    }
}