package com.pramod.eyecare.framework.ui.utils

import android.os.Build
import android.view.View
import android.view.ViewGroup
import androidx.annotation.Px
import androidx.core.view.*

fun View.doWithInset(insetCallback: (view: View, top: Int, bottom: Int) -> Unit) {
    var isInsetConsumed = false
    setOnApplyWindowInsetsListener { view, windowInsets ->
        if (!isInsetConsumed) {
            isInsetConsumed = true
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                windowInsets.getInsets(WindowInsetsCompat.Type.systemBars()).let {
                    insetCallback(this, it.top, it.bottom)
                }
            } else {
                windowInsets.consumeSystemWindowInsets().let {
                    insetCallback(this, it.systemWindowInsetTop, it.systemWindowInsetBottom)
                }
            }
        }
        windowInsets
    }
}

fun View.updateMargin(
    @Px top: Int = marginTop,
    @Px left: Int = marginLeft,
    @Px bottom: Int = marginBottom,
    @Px right: Int = marginRight,
) {
    val tempLayoutParams = layoutParams as ViewGroup.MarginLayoutParams
    tempLayoutParams.bottomMargin = bottom
    tempLayoutParams.topMargin = top
    tempLayoutParams.leftMargin = left
    tempLayoutParams.rightMargin = right
    layoutParams = tempLayoutParams
}