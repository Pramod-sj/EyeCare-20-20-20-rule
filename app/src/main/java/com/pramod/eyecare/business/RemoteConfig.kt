package com.pramod.eyecare.business

interface RemoteConfig {

    companion object {
        const val EYE_CARE_TIPS = "eye_care_tips"
    }

    suspend fun initialize(): Boolean

    fun getEyeCareTips(): List<String>

}