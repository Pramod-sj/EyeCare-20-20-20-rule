package com.pramod.eyecare.business.interactor

import com.pramod.eyecare.business.domain.data.network.RemoteConfig
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class GetRandomEyeCareTipUseCase @Inject constructor(private val remoteConfig: RemoteConfig) {

    operator fun invoke(): Flow<String> {
        return flow {
            while (true) {
                emit(remoteConfig.getEyeCareTips().let {
                    if (it.isNotEmpty()) it.random()
                    else ""
                })
                delay(60 * 1000)
            }
        }
    }

}