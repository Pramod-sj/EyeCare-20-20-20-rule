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
    requestApplyInsetsWhenAttached()
}

private fun View.requestApplyInsetsWhenAttached() {
    if (isAttachedToWindow) {
        // We're already attached, just request as normal
        requestApplyInsets()
    } else {
        // We're not attached to the hierarchy, add a listener to
        // request when we are
        addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
            override fun onViewAttachedToWindow(v: View) {
                v.removeOnAttachStateChangeListener(this)
                v.requestApplyInsets()
            }

            override fun onViewDetachedFromWindow(v: View) = Unit
        })
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