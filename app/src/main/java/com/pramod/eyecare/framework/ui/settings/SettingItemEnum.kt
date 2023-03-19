package com.pramod.eyecare.framework.ui.settings

enum class SettingItemEnum(val id: String) {
    RATE_APP("id_rate_app"),
    SHARE_APP("id_share_app"),
    ABOUT_US("id_about"),
    UNKNOWN("id_unknown")
}

fun String.toSettingItemEnum(): SettingItemEnum {
    return when (this) {
        SettingItemEnum.RATE_APP.id -> SettingItemEnum.RATE_APP
        SettingItemEnum.SHARE_APP.id -> SettingItemEnum.SHARE_APP
        SettingItemEnum.ABOUT_US.id -> SettingItemEnum.ABOUT_US
        else -> SettingItemEnum.UNKNOWN
    }
}