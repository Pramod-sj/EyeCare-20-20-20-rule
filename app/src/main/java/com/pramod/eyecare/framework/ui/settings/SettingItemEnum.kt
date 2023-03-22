package com.pramod.eyecare.framework.ui.settings

enum class SettingItemEnum(val id: String) {
    RATE_APP("id_rate_app"),
    SHARE_APP("id_share_app"),
    ABOUT_US("id_about"),
    NOTIFICATION("id_notification"),
    APP_NOT_WORKING_PROPERLY("app_not_working_properly"),
    UNKNOWN("id_unknown")
}

fun String.toSettingItemEnum(): SettingItemEnum {
    return when (this) {
        SettingItemEnum.RATE_APP.id -> SettingItemEnum.RATE_APP
        SettingItemEnum.SHARE_APP.id -> SettingItemEnum.SHARE_APP
        SettingItemEnum.ABOUT_US.id -> SettingItemEnum.ABOUT_US
        SettingItemEnum.NOTIFICATION.id -> SettingItemEnum.NOTIFICATION
        SettingItemEnum.APP_NOT_WORKING_PROPERLY.id -> SettingItemEnum.APP_NOT_WORKING_PROPERLY
        else -> SettingItemEnum.UNKNOWN
    }
}