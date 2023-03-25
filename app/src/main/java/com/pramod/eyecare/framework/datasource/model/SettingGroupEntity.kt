package com.pramod.eyecare.framework.datasource.model

data class SettingGroupEntity(
    val id: String,
    val titleKey: String,
    val items: List<SettingItemEntity>,
)
