package com.pramod.eyecare.business.domain.data.network

import com.pramod.eyecare.business.domain.model.SettingGroup

interface GetSettingRemoteConfig {

    suspend operator fun invoke(): List<SettingGroup>

}