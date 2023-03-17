package com.pramod.eyecare.business

interface CopyHelper {

    companion object {
        const val NOTIFICATION_WORKING_TITLE = "notification_working_title"
        const val NOTIFICATION_WORKING_BODY = "notification_working_body"
        const val NOTIFICATION_RESTING_TITLE = "notification_resting_title"
        const val NOTIFICATION_RESTING_BODY = "notification_resting_body"
        const val NOTIFICATION_ACTION_STOP = "notification_action_stop"
        const val NOTIFICATION_ACTION_DONT_REMIND = "notification_action_dont_remind"
        const val NOTIFICATION_ACTION_DISMISS = "notification_action_dismiss"
    }

    fun getString(key: String, defaultKey: () -> String): String

}