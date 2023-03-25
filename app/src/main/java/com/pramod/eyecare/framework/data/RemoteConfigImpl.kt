package com.pramod.eyecare.framework.data

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.pramod.eyecare.R
import com.pramod.eyecare.business.RemoteConfig
import com.pramod.eyecare.framework.data.model.SettingGroupEntity
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class RemoteConfigImpl @Inject constructor(
    private val remoteConfig: FirebaseRemoteConfig, private val gson: Gson
) : RemoteConfig {

    private val stringListType =
        TypeToken.getParameterized(List::class.java, String::class.java).type

    private val mapSettingGroupEntityType =
        TypeToken.getParameterized(List::class.java, SettingGroupEntity::class.java).type

    private val mapStringStringType =
        TypeToken.getParameterized(Map::class.java, String::class.java, String::class.java).type

    override suspend fun initialize() = suspendCoroutine<Boolean> { continuation ->
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 3600
        }
        remoteConfig.setConfigSettingsAsync(configSettings)
        remoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)

        remoteConfig.fetch(1000 * 60 * 15).addOnSuccessListener {
            remoteConfig.activate()
            continuation.resume(true)
        }.addOnFailureListener {
            continuation.resume(false)
        }.addOnCanceledListener {
            continuation.resume(false)
        }
    }

    override fun getEyeCareTips(): List<String> {
        return remoteConfig.getString(RemoteConfig.EYE_CARE_TIPS).let {
            if (it.isEmpty()) listOf()
            else gson.fromJson(it, stringListType)
        }
    }

    override fun getSettings(): List<SettingGroupEntity> {
        return remoteConfig.getString(RemoteConfig.SETTINGS).let {
            if (it.isEmpty()) listOf()
            else gson.fromJson(it, mapSettingGroupEntityType)
        }
    }

    override fun getTranslations(): Map<String, String> {
        return remoteConfig.getString(RemoteConfig.TRANSLATIONS_EN).let {
            if (it.isEmpty()) mapOf()
            else gson.fromJson(it, mapStringStringType)
        }
    }

}