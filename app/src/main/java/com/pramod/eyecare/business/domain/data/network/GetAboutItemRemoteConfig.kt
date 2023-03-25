package com.pramod.eyecare.business.domain.data.network

import com.pramod.eyecare.framework.datasource.model.about.AboutCardItem

interface GetAboutItemRemoteConfig {

    operator fun invoke(): List<AboutCardItem>

}