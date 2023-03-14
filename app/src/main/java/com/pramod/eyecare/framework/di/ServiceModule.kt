package com.pramod.eyecare.framework.di

import com.pramod.eyecare.business.PersistentAlarmScheduler
import com.pramod.eyecare.framework.impl.PersistentAlarmSchedulerImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent

@Module
@InstallIn(ServiceComponent::class)
interface ServiceModule {

    @Binds
    fun providePersistentAlarmScheduler(
        persistentAlarmSchedulerImpl: PersistentAlarmSchedulerImpl,
    ): PersistentAlarmScheduler

}