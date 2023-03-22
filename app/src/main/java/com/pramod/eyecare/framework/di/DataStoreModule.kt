package com.pramod.eyecare.framework.di

import com.pramod.eyecare.business.*
import com.pramod.eyecare.framework.impl.ScheduledAlarmCacheImpl
import com.pramod.eyecare.framework.data.CopyHelperImpl
import com.pramod.eyecare.framework.data.RemoteConfigImpl
import com.pramod.eyecare.framework.data.SettingPreferenceImpl
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