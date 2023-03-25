package com.pramod.eyecare.framework.ui.fragment.about

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.pramod.eyecare.business.interactor.GetAboutItemUseCase
import com.pramod.eyecare.framework.datasource.model.about.AboutCardItem
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AboutViewModel @Inject constructor(
    private val getAboutItemUseCase: GetAboutItemUseCase
) : ViewModel() {

    private val _aboutUiItems = MutableLiveData<List<AboutCardItem>>()
    val aboutUiItems: LiveData<List<AboutCardItem>>
        get() = _aboutUiItems

    init {
        _aboutUiItems.value = getAboutItemUseCase()
    }
}