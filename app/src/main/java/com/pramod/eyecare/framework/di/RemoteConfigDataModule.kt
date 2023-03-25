package com.pramod.eyecare.framework.di

import com.pramod.eyecare.business.domain.data.network.GetAboutItemRemoteConfig
import com.pramod.eyecare.business.domain.data.network.GetSettingRemoteConfig
import com.pramod.eyecare.framework.datasource.remote.impl.GetAboutItemRemoteConfigImpl
import com.pramod.eyecare.framework.datasource.remote.impl.GetSettingRemoteConfigImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface RemoteConfigDataModule {

    @Binds
    fun provideGetSettingsRemoteConfig(
        getSettingsRemoteConfigImpl: GetSettingRemoteConfigImpl
    ): GetSettingRemoteConfig

    @Binds
    fun provideGetAboutItemRemoteConfig(
        getAboutItemRemoteConfigImpl: GetAboutItemRemoteConfigImpl
    ): GetAboutItemRemoteConfig


}