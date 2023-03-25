package com.pramod.eyecare.business.domain.data.network

interface CopyHelper {

    companion object {
        const val ABOUT_PRIVACY_POLICY: String = "about_privacy_policy"
        const val ABOUT_TERM_AND_SERVICES: String = "about_term_and_services"
        const val ABOUT_OPEN_SOURCE_LIB_TITLE: String = "about_open_source_lib_title"
        const val OTHERS_TEXT: String = "others_text"
        const val ABOUT_RATE_US_DESC: String = "about_rate_us_desc"
        const val ABOUT_RATE_US_TITLE: String = "about_rate_us_title"
        const val ABOUT_DONATE_DESC: String = "about_donate_desc"
        const val ABOUT_DONATE_TITLE: String = "about_donate_title"
        const val ABOUT_FORK_ON_GITHUB_DESC: String = "about_fork_on_github_desc"
        const val ABOUT_FORK_ON_GITHUB_TITLE: String = "about_fork_on_github_title"
        const val SUPPORT_TEXT: String = "support_text"
        const val CONTACT_TEXT: String = "contact_text"
        const val CHANGELOG_TEXT: String = "changelog_text"
        const val PRIVACY_POLICY = "privacy_policy"
        const val TERM_AND_SERVICE = "term_and_service"
        const val NOTIFICATION_WORKING_TITLE = "notification_working_title"
        const val NOTIFICATION_WORKING_BODY = "notification_working_body"
        const val NOTIFICATION_RESTING_TITLE = "notification_resting_title"
        const val NOTIFICATION_RESTING_BODY = "notification_resting_body"
        const val NOTIFICATION_ACTION_STOP = "notification_action_stop"
        const val NOTIFICATION_ACTION_DONT_REMIND = "notification_action_dont_remind"
        const val NOTIFICATION_ACTION_DISMISS = "notification_action_dismiss"
    }

    fun getString(key: String, defaultKey: () -> String = { "-" }): String

}