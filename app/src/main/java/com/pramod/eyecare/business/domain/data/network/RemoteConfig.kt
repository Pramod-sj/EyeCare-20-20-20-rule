package com.pramod.eyecare.business.domain.data.network

import com.pramod.eyecare.framework.datasource.model.SettingGroupEntity

interface RemoteConfig {

    companion object {

        const val TRANSLATIONS_EN = "translations_en"

        const val EYE_CARE_TIPS = "eye_care_tips"

        const val SETTINGS = "settings"
    }

    suspend fun initialize(): Boolean

    fun getEyeCareTips(): List<String>

    fun getSettings(): List<SettingGroupEntity>

    fun getTranslations(): Map<String, String>

}