package com.pramod.eyecare.framework.di

import com.pramod.eyecare.business.domain.data.network.GetSettingRemoteConfig
import com.pramod.eyecare.framework.data.GetSettingRemoteConfigImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface RemoteDataModule {

    @Binds
    fun provideGetSettingsRemoteConfig(
        getSettingsRemoteConfigImpl: GetSettingRemoteConfigImpl
    ): GetSettingRemoteConfig


}