package com.pramod.eyecare.framework.datasource.remote.impl

import com.pramod.eyecare.business.domain.data.network.CopyHelper
import com.pramod.eyecare.business.domain.data.network.RemoteConfig
import javax.inject.Inject

class CopyHelperImpl @Inject constructor(
    private val remoteConfig: RemoteConfig
) : CopyHelper {

    override fun getString(key: String, defaultKey: () -> String): String {
        return remoteConfig.getTranslations().getOrElse(key, defaultKey)
    }
}