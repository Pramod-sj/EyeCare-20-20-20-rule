package com.pramod.eyecare.business.interactor

import com.pramod.eyecare.business.domain.data.network.GetAboutItemRemoteConfig
import com.pramod.eyecare.framework.datasource.model.about.AboutCardItem
import javax.inject.Inject

class GetAboutItemUseCase @Inject constructor(
    private val getAboutItemRemoteConfig: GetAboutItemRemoteConfig
) {

    operator fun invoke(): List<AboutCardItem> {
        return getAboutItemRemoteConfig()
    }

}
