package com.pramod.eyecare.framework.ui.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pramod.eyecare.business.domain.data.preference.SettingPreference
import com.pramod.eyecare.business.interactor.GetSettingItemUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val getSettingItemUseCase: GetSettingItemUseCase,
    private val settingPreference: SettingPreference
) : ViewModel() {

    private val _settingItems = MutableLiveData<List<SettingGroupUiState>>()
    val settingItems: LiveData<List<SettingGroupUiState>>
        get() = _settingItems

    private fun fetchSettingsItem() {
        viewModelScope.launch {
            _settingItems.value = getSettingItemUseCase().toUiStateList(settingPreference)
        }
    }

    fun togglePlayWorkRingtone() {
        viewModelScope.launch {
            val newPlayRingtoneValue = !(settingPreference.getPlayWorkRingtone().firstOrNull() ?: false)
            settingPreference.setPlayWorkRingtone(newPlayRingtoneValue)
        }
    }


    init {
        fetchSettingsItem()
    }
}