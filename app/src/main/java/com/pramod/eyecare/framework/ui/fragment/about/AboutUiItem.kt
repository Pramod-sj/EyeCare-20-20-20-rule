package com.pramod.eyecare.framework.ui.fragment.about

sealed class AboutUiItem(val viewTypeEnum: Enum) {

    enum class Enum(val id: Int) {
        DEVELOPER(1),
        SUPPORT(2),
        CREDIT(3),
        OTHERS(4),
    }

    data class About(
        val cardTitle: String,
        val developerName: String,
        val developerAndDesignLabel: String,
        val developerPicUrl: String,
    ) : AboutUiItem(Enum.DEVELOPER)

    data class Support(
        val cardTitle: String,
        val innerItems: List<AboutInnerItem>,
    ) : AboutUiItem(Enum.SUPPORT)

    data class Credit(
        val cardTitle: String,
        val innerItems: List<AboutInnerItem>,
    ) : AboutUiItem(Enum.CREDIT)

    data class Others(
        val cardTitle: String,
        val innerItems: List<AboutInnerItem>,
    ) : AboutUiItem(Enum.OTHERS)

}

fun Int.toAboutUiItemEnum(): AboutUiItem.Enum? {
    return AboutUiItem.Enum.values().find { it.id == this }
}


data class AboutInnerItem(
    val id: AboutInnerItemIdEnum,
    val title: String,
    val subtitle: String? = null,
    val iconRes: Int? = null,
    val iconUrl: String? = null,
)

enum class AboutInnerItemIdEnum(val id: String) {
    FORK_ON_GITHUB("fork_on_github"),
    DONATE("donate"),
    SHARE_APP("share"),
    RATE_US("rate_us"),

    APP_LOGO_CREDIT("app_logo_credit"),

    OPEN_SOURCE_LIBRARIES("open_source_libraries"),
    TERM_AND_SERVICES("term_and_services"),
    PRIVACY_POLICY("privacy_policy")
}

fun String.toAboutInnerItemIdEnum(): AboutInnerItemIdEnum? {
    return AboutInnerItemIdEnum.values().find { it.id == this }
}

