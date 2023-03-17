package com.pramod.eyecare.framework.data

import com.pramod.eyecare.business.CopyHelper
import javax.inject.Inject

class CopyHelperImpl @Inject constructor() : CopyHelper {

    override fun getString(key: String, defaultKey: () -> String): String {
        return defaultKey()
    }
}