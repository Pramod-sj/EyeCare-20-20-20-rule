package com.pramod.eyecare.business.domain.model

enum class SettingItemEnum(val id: String) {
    RATE_APP("id_rate_app"),
    ABOUT_US("id_about"),
    NOTIFICATION("id_setting"),
    PLAY_WORK_RINGTONE("id_play_work_ringtone"),
    APP_NOT_WORKING_PROPERLY("id_app_not_working"),
    UNKNOWN("id_unknown")
}

fun String.toSettingItemEnum(): SettingItemEnum {
    return SettingItemEnum.values().find { it.id == this } ?: SettingItemEnum.UNKNOWN
}