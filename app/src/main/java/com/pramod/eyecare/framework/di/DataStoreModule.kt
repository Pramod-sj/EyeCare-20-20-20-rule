package com.pramod.eyecare.framework.di

import com.pramod.eyecare.business.ScheduledAlarmCache
import com.pramod.eyecare.framework.impl.ScheduledAlarmCacheImpl
import com.pramod.eyecare.business.EyeCareUiCountDownTimer
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


}