package com.pramod.eyecare.framework.ui.utils

import android.view.animation.AccelerateDecelerateInterpolator
import androidx.fragment.app.Fragment
import com.google.android.material.transition.MaterialSharedAxis

fun Fragment.applyMaterialAxisTransition() {

    //enter from right edge
    enterTransition = MaterialSharedAxis(MaterialSharedAxis.X, true).apply {
        duration = 350L
        interpolator = AccelerateDecelerateInterpolator()
    }

    //exit from left edge
    exitTransition = MaterialSharedAxis(MaterialSharedAxis.X, true).apply {
        duration = 350L
        interpolator = AccelerateDecelerateInterpolator()
    }

    //reenter (popup) from left edge
    reenterTransition = MaterialSharedAxis(MaterialSharedAxis.X, false).apply {
        duration = 300L
        interpolator = AccelerateDecelerateInterpolator()
    }

    //return (popup) from right edge
    returnTransition = MaterialSharedAxis(MaterialSharedAxis.X, false).apply {
        duration = 300L
        interpolator = AccelerateDecelerateInterpolator()
    }

}
