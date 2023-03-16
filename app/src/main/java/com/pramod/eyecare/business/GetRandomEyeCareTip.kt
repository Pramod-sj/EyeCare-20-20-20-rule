package com.pramod.eyecare.business

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.isActive
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class GetRandomEyeCareTip @Inject constructor(private val remoteConfig: RemoteConfig) {

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