package com.pramod.eyecare.business.interactor

import com.pramod.eyecare.business.domain.SettingItem
import javax.inject.Inject

class GetSettingItemUseCase @Inject constructor() {

    suspend operator fun invoke(): List<SettingItem> {
        return listOf(
            SettingItem(
                id = "id_rate_app",
                iconUrl = "ic_rate_app",
                title = "Rate app",
                subTitle = "Rate this app on google playe store"
            ),
            SettingItem(
                id = "id_share_app",
                iconUrl = "ic_share",
                title = "Share app",
                subTitle = "Share app with your colleague and friends"
            ),
            SettingItem(id = "id_about", iconUrl = "ic_about_us", title = "About")
        )
    }

}