package com.pramod.eyecare.business.interactor

import com.pramod.eyecare.business.domain.data.network.GetSettingRemoteConfig
import com.pramod.eyecare.business.domain.model.SettingGroup
import javax.inject.Inject

class GetSettingItemUseCase @Inject constructor(
    private val getSettingRemoteConfig: GetSettingRemoteConfig
) {

    suspend operator fun invoke(): List<SettingGroup> {
        return getSettingRemoteConfig()
    }

    /*
            return listOf(
        SettingGroup(
            id = "id_auto",
            title = "Auto",
            items = listOf(
                SettingItem(
                    id = "id_auto_schedule",
                    title = "Auto Schedule",
                    subTitle = "Automatically turn on reminding service for specified time interval",
                )
            )
        ),
        SettingGroup(
            id = "id_notification",
            title = "Notification",
            items = listOf(
                SettingItem(
                    id = "id_notification",
                    title = "Notification",
                    subTitle = "Change eye rest reminder notification settings",
                ),
                SettingItem(
                    id = SettingItemEnum.PLAY_WORK_RINGTONE.id,
                    title = "Play work reminder ringtone",
                    subTitle = "Turn on or off the work reminder notification sound",
                    showSwitch = true
                )
            )
        ),
        SettingGroup(
            id = "id_others",
            title = "Others",
            items = listOf(
                SettingItem(
                    id = SettingItemEnum.APP_NOT_WORKING_PROPERLY.id,
                    title = "App not working properly",
                    subTitle = "Not receiving notification or app not working properly, check how to fix it.",
                ),
                SettingItem(
                    id = "id_about",
                    title = "About",
                    subTitle = "Developer, support, license & others",
                )
            )
        )
    )

     */

}