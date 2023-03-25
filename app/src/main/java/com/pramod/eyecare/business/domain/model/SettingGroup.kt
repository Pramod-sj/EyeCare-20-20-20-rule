package com.pramod.eyecare.business.domain.model

data class SettingGroup(
    val id: String,
    val title: String,
    val items: List<SettingItem>,
)
