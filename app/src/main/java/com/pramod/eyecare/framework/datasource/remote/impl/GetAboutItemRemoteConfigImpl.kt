package com.pramod.eyecare.framework.datasource.remote.impl

import com.pramod.eyecare.R
import com.pramod.eyecare.business.domain.data.network.CopyHelper
import com.pramod.eyecare.business.domain.data.network.GetAboutItemRemoteConfig
import com.pramod.eyecare.framework.datasource.model.about.AboutCardItem
import com.pramod.eyecare.framework.datasource.model.about.AboutInnerItem
import javax.inject.Inject

class GetAboutItemRemoteConfigImpl @Inject constructor(
    private val copyHelper: CopyHelper
) : GetAboutItemRemoteConfig {
    override fun invoke(): List<AboutCardItem> {
        return listOf(
            AboutCardItem.AboutApp(
                changelogText = copyHelper.getString(CopyHelper.CHANGELOG_TEXT) { "Changelog" },
                contactText = copyHelper.getString(CopyHelper.CONTACT_TEXT) { "Contact" }
            ),
            AboutCardItem.Support(
                cardTitle = copyHelper.getString(CopyHelper.SUPPORT_TEXT) { "Support" },
                innerItems = listOf(
                    AboutInnerItem(
                        id = AboutInnerItem.Enum.FORK_ON_GITHUB,
                        title = copyHelper.getString(CopyHelper.ABOUT_FORK_ON_GITHUB_TITLE) { "Fork on github" },
                        iconRes = R.drawable.ic_source_fork,
                        subtitle = copyHelper.getString(CopyHelper.ABOUT_FORK_ON_GITHUB_DESC) { "Let grow together by forking us on github" }
                    ),
                    AboutInnerItem(
                        id = AboutInnerItem.Enum.DONATE,
                        title = copyHelper.getString(CopyHelper.ABOUT_DONATE_TITLE) { "Donate" },
                        subtitle = copyHelper.getString(CopyHelper.ABOUT_DONATE_DESC) { "Like my work?" },
                        iconRes = R.drawable.ic_donate,
                    ),
                    AboutInnerItem(
                        id = AboutInnerItem.Enum.RATE_US,
                        title = copyHelper.getString(CopyHelper.ABOUT_RATE_US_TITLE) { "Rate us" },
                        iconRes = R.drawable.ic_rate_review,
                        subtitle = copyHelper.getString(CopyHelper.ABOUT_RATE_US_DESC) { "Take some time to rate and review me on play store" }
                    ),
                )
            ), AboutCardItem.Others(
                cardTitle = copyHelper.getString(CopyHelper.OTHERS_TEXT) { "Others" },
                innerItems = listOf(
                    AboutInnerItem(
                        AboutInnerItem.Enum.OPEN_SOURCE_LIBRARIES,
                        copyHelper.getString(CopyHelper.ABOUT_OPEN_SOURCE_LIB_TITLE) { "Open source libraries" },
                        iconRes = R.drawable.ic_license
                    ),
                    AboutInnerItem(
                        AboutInnerItem.Enum.TERM_AND_SERVICES,
                        copyHelper.getString(CopyHelper.ABOUT_TERM_AND_SERVICES) { "Terms & services" },
                        iconRes = R.drawable.ic_terms_and_conditions
                    ),
                    AboutInnerItem(
                        AboutInnerItem.Enum.PRIVACY_POLICY,
                        copyHelper.getString(CopyHelper.ABOUT_PRIVACY_POLICY) { "Privacy policy" }),
                )
            )
        )
    }
}