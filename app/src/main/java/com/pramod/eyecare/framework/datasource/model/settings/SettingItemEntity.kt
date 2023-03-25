package com.pramod.eyecare.framework.datasource.model.settings

data class SettingItemEntity(
    val id: String,
    val titleKey: String,
    val subtitleKey: String? = null,
    val showSwitch: Boolean = false
)