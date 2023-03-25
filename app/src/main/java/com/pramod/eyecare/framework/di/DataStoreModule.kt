package com.pramod.eyecare.framework.di

import com.pramod.eyecare.business.*
import com.pramod.eyecare.business.domain.data.network.CopyHelper
import com.pramod.eyecare.business.domain.data.network.RemoteConfig
import com.pramod.eyecare.business.domain.data.preference.ScheduledAlarmCache
import com.pramod.eyecare.business.domain.data.preference.SettingPreference
import com.pramod.eyecare.framework.datasource.local.datastore.ScheduledAlarmCacheImpl
import com.pramod.eyecare.framework.datasource.remote.impl.CopyHelperImpl
import com.pramod.eyecare.framework.datasource.remote.firebase.RemoteConfigImpl
import com.pramod.eyecare.framework.datasource.local.datastore.SettingPreferenceImpl
import com.pramod.eyecare.framework.ui.EyeCareUiCountDownTimerImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface DataStoreModule {

    @Binds
    fun provideAlarmPref(alarmPrefImpl: ScheduledAlarmCacheImpl): ScheduledAlarmCache

    @Binds
    fun provideEyeCareUiCountDownTimer(EyeCareUiCountDownTimerImpl: EyeCareUiCountDownTimerImpl): EyeCareUiCountDownTimer

    @Binds
    fun provideRemoteConfig(remoteConfigImpl: RemoteConfigImpl): RemoteConfig

    @Binds
    fun provideCopyHelper(copyHelperImpl: CopyHelperImpl): CopyHelper

    @Binds
    fun provideSettingPreference(settingPreferenceImpl: SettingPreferenceImpl): SettingPreference

}