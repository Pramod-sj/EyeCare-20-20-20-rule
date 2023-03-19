package com.pramod.eyecare.framework.ui.fragment.about

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AboutViewModel : ViewModel() {

    private val _aboutUiItems = MutableLiveData<List<AboutUiItem>>()
    val aboutUiItems: LiveData<List<AboutUiItem>>
        get() = _aboutUiItems

    init {
        _aboutUiItems.value = listOf(
            AboutUiItem.About(
                cardTitle = "Developer Info",
                developerName = "Pramod S. Jantwal",
                developerAndDesignLabel = "Design & developed by",
                developerPicUrl = "https://avatars.githubusercontent.com/u/21077584?v=4"
            )
        )
    }
}