package com.pramod.eyecare.framework.datasource.model.settings

data class SettingGroupEntity(
    val id: String,
    val titleKey: String,
    val items: List<SettingItemEntity>,
)
