package com.pramod.eyecare.framework.data.model

data class SettingGroupEntity(
    val id: String,
    val titleKey: String,
    val items: List<SettingItemEntity>,
)
