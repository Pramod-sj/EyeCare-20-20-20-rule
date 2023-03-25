package com.pramod.eyecare.business.domain.model


data class SettingItem(
    val id: SettingItemEnum,
    val title: String,
    val subTitle: String? = null,
    val showSwitch: Boolean = false
)