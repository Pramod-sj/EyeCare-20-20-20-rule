package com.pramod.eyecare.framework.ui.common

import android.content.Context
import android.content.res.Configuration
import androidx.fragment.app.Fragment

val Fragment.isLight: Boolean
    get() = requireContext().isLight

val Context.isLight: Boolean
    get() = when (resources.configuration.uiMode + Configuration.UI_MODE_NIGHT_MASK) {
        Configuration.UI_MODE_NIGHT_YES -> false
        Configuration.UI_MODE_NIGHT_NO -> true
        else -> false
    }