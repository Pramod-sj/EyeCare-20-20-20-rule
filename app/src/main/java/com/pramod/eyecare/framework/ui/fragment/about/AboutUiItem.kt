package com.pramod.eyecare.framework.ui.fragment.about

sealed class AboutUiItem {

    data class About(
        val cardTitle: String,
        val developerName: String,
        val developerAndDesignLabel: String,
        val developerPicUrl: String,
    ) : AboutUiItem()

}