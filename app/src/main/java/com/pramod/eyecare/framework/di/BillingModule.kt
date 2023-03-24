package com.pramod.eyecare.framework.di

import com.pramod.eyecare.business.*
import com.pramod.eyecare.framework.billing.BillingHelper
import com.pramod.eyecare.framework.billing.BillingHelperImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface BillingModule {

    @Binds
    fun provideBillingHelper(billingHelperImpl: BillingHelperImpl): BillingHelper

}