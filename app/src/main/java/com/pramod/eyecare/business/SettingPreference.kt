package com.pramod.eyecare.business

import kotlinx.coroutines.flow.Flow


interface SettingPreference {

    companion object {
        const val KEY_PLAY_RINGTONE = "key_play_ringtone"
    }

    fun getPlayWorkRingtone(): Flow<Boolean>

    suspend fun setPlayWorkRingtone(play: Boolean)

}