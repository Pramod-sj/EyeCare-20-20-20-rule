package com.pramod.eyecare.framework.data

import com.pramod.eyecare.business.CopyHelper
import com.pramod.eyecare.business.RemoteConfig
import javax.inject.Inject

class CopyHelperImpl @Inject constructor(
    private val remoteConfig: RemoteConfig
) : CopyHelper {

    override fun getString(key: String, defaultKey: () -> String): String {
        return remoteConfig.getTranslations().getOrElse(key, defaultKey)
    }
}