package com.pramod.eyecare.framework.ui.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pramod.eyecare.business.domain.SettingGroup
import com.pramod.eyecare.business.domain.SettingItem
import com.pramod.eyecare.business.interactor.GetSettingItemUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val getSettingItemUseCase: GetSettingItemUseCase,
) : ViewModel() {

    private val _settingItems = MutableLiveData<List<SettingGroup>>()
    val settingItems: LiveData<List<SettingGroup>>
        get() = _settingItems

    private fun fetchSettingsItem() {
        viewModelScope.launch {
            _settingItems.value = getSettingItemUseCase().orEmpty()
        }
    }

    init {
        fetchSettingsItem()
    }
}