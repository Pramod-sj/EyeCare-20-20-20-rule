package com.pramod.eyecare.business.domain

data class SettingGroup(
    val id: String,
    val title: String,
    val items: List<SettingItem>,
)

data class SettingItem(
    val id: String,
    val title: String,
    val subTitle: String? = null,
)