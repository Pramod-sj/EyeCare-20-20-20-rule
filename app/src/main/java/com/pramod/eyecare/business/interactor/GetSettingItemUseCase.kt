package com.pramod.eyecare.business.interactor

import com.pramod.eyecare.business.domain.SettingGroup
import com.pramod.eyecare.business.domain.SettingItem
import javax.inject.Inject

class GetSettingItemUseCase @Inject constructor() {

    suspend operator fun invoke(): List<SettingGroup> {
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
                        id = "id_play_work_sound",
                        title = "Play work reminder",
                        subTitle = "Turn on or off the work reminder notification sound",
                    )
                )
            ),
            SettingGroup(
                id = "id_others",
                title = "Others",
                items = listOf(
                    SettingItem(
                        id = "id_about",
                        title = "About",
                        subTitle = "Developer, support, license & others",
                    )
                )
            )
        )
    }

}