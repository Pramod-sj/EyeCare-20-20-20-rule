package com.pramod.eyecare.framework.datasource.model.about

data class AboutInnerItem(
    val id: Enum,
    val title: String,
    val subtitle: String? = null,
    val iconRes: Int? = null,
    val iconUrl: String? = null,
) {
    enum class Enum(val id: String) {
        FORK_ON_GITHUB("fork_on_github"),
        DONATE("donate"),
        SHARE_APP("share"),
        RATE_US("rate_us"),
        OPEN_SOURCE_LIBRARIES("open_source_libraries"),
        TERM_AND_SERVICES("term_and_services"),
        PRIVACY_POLICY("privacy_policy"),
        UNKNOWN("unknown")
    }

}

fun String.toAboutInnerItemIdEnum(): AboutInnerItem.Enum {
    return AboutInnerItem.Enum.values().find { it.id == this } ?: AboutInnerItem.Enum.UNKNOWN
}