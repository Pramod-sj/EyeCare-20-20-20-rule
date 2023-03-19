package com.pramod.eyecare.framework.ui.fragment.about

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.pramod.eyecare.R

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
            ), AboutUiItem.Support(
                cardTitle = "Support", innerItems = listOf(
                    AboutInnerItem(
                        AboutInnerItemIdEnum.FORK_ON_GITHUB,
                        "Fork on github",
                        "View source code or fork on github",
                        R.drawable.ic_source_fork
                    ),
                    AboutInnerItem(
                        AboutInnerItemIdEnum.DONATE,
                        "Donate",
                        "Like my work?",
                        R.drawable.ic_donate
                    ),
                    AboutInnerItem(
                        AboutInnerItemIdEnum.SHARE_APP,
                        "Share",
                        "Share with your friends and colleagues",
                        R.drawable.ic_share
                    ),
                    AboutInnerItem(
                        AboutInnerItemIdEnum.RATE_US,
                        "Rate us",
                        "Take a moment to rate this app on Play Store",
                        R.drawable.ic_rate_review
                    ),
                )
            ), AboutUiItem.Credit(
                cardTitle = "Credit", innerItems = listOf(
                    AboutInnerItem(
                        AboutInnerItemIdEnum.APP_LOGO_CREDIT,
                        "App logo",
                        "App logo made by Freepik from Flaticon",
                        R.drawable.ic_freepik
                    ),
                )
            ), AboutUiItem.Others(
                cardTitle = "Others", innerItems = listOf(
                    AboutInnerItem(
                        AboutInnerItemIdEnum.OPEN_SOURCE_LIBRARIES,
                        "Open source libraries",
                        iconRes = R.drawable.ic_open_source_license
                    ),
                    AboutInnerItem(
                        AboutInnerItemIdEnum.TERM_AND_SERVICES,
                        "Terms & services",
                        iconRes = R.drawable.ic_terms_and_conditions
                    ),
                    AboutInnerItem(AboutInnerItemIdEnum.PRIVACY_POLICY, "Privacy policy"),
                )
            )
        )
    }
}