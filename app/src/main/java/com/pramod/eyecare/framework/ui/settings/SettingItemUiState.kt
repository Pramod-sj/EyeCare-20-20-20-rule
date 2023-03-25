package com.pramod.eyecare.framework.ui.settings

import com.pramod.eyecare.business.SettingPreference
import com.pramod.eyecare.business.domain.SettingGroup
import com.pramod.eyecare.business.domain.SettingItem
import com.pramod.eyecare.business.domain.SettingItemEnum
import kotlinx.coroutines.flow.firstOrNull


suspend fun List<SettingGroup>.toUiStateList(settingPreference: SettingPreference): List<SettingGroupUiState> {
    return map {
        SettingGroupUiState(
            id = it.id,
            title = it.title,
            settingItem = it.items.map { settingItem ->
                SettingItemUiState(
                    settingItem = settingItem,
                    isSwitchChecked = when (settingItem.id) {
                        SettingItemEnum.PLAY_WORK_RINGTONE -> settingPreference.getPlayWorkRingtone()
                            .firstOrNull() ?: false
                        else -> false
                    }
                )
            }
        )
    }
}


data class SettingGroupUiState(
    val id: String,
    val title: String,
    val settingItem: List<SettingItemUiState>
)

data class SettingItemUiState(val settingItem: SettingItem, val isSwitchChecked: Boolean = false)