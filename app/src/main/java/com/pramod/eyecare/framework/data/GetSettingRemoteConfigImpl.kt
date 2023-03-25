package com.pramod.eyecare.framework.data

import com.pramod.eyecare.business.CopyHelper
import com.pramod.eyecare.business.RemoteConfig
import com.pramod.eyecare.business.domain.data.network.GetSettingRemoteConfig
import com.pramod.eyecare.business.domain.model.SettingGroup
import com.pramod.eyecare.business.domain.model.SettingItem
import com.pramod.eyecare.business.domain.model.toSettingItemEnum
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class GetSettingRemoteConfigImpl @Inject constructor(
    private val remoteConfig: RemoteConfig,
    private val copyHelper: CopyHelper
) : GetSettingRemoteConfig {
    override suspend fun invoke(): List<SettingGroup> {
        return withContext(Dispatchers.IO) {
            remoteConfig.getSettings().map { entity ->
                Timber.d("Data:$entity")
                SettingGroup(
                    id = entity.id,
                    title = copyHelper.getString(entity.titleKey),
                    items = entity.items.map { settingItemEntity ->
                        SettingItem(
                            id = settingItemEntity.id.toSettingItemEnum(),
                            title = copyHelper.getString(settingItemEntity.titleKey),
                            subTitle = settingItemEntity.subtitleKey?.let { copyHelper.getString(it) },
                            showSwitch = settingItemEntity.showSwitch
                        )
                    }
                )
            }
        }
    }
}