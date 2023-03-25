package com.pramod.eyecare.framework.datasource.model.about

sealed class AboutCardItem(val viewTypeEnum: Enum) {

    enum class Enum(val id: Int) {
        APP(0), SUPPORT(2), CREDIT(3), OTHERS(4), UNKNOWN(4)
    }

    data class AboutApp(
        val changelogText: String, val contactText: String
    ) : AboutCardItem(Enum.APP)

    data class Support(
        val cardTitle: String,
        val innerItems: List<AboutInnerItem>,
    ) : AboutCardItem(Enum.SUPPORT)

    data class Credit(
        val cardTitle: String,
        val innerItems: List<AboutInnerItem>,
    ) : AboutCardItem(Enum.CREDIT)

    data class Others(
        val cardTitle: String,
        val innerItems: List<AboutInnerItem>,
    ) : AboutCardItem(Enum.OTHERS)

}

fun Int.toAboutUiItemEnum(): AboutCardItem.Enum {
    return AboutCardItem.Enum.values().find { it.id == this } ?: AboutCardItem.Enum.UNKNOWN
}


