package com.pramod.eyecare.framework.helper

import android.os.Build
import android.view.HapticFeedbackConstants
import android.view.MotionEvent
import android.view.View

/**
 * This class perform haptic on [MotionEvent.ACTION_DOWN] and [MotionEvent.ACTION_UP] touch events
 */
class HapticTouchListener : View.OnTouchListener {

    override fun onTouch(view: View, event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN ->
                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            MotionEvent.ACTION_UP -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
                    view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY_RELEASE)
                }
                view.performClick()
            }
        }
        return true
    }
}