package com.pramod.eyecare.framework.ui.fragment.about.viewholder

import com.pramod.eyecare.databinding.LayoutAboutAppInfoBinding
import com.pramod.eyecare.framework.ui.fragment.about.AboutUiItem

class AboutAppViewHolder(
    private val binding: LayoutAboutAppInfoBinding,
) : AboutBaseViewHolder<AboutUiItem>(binding) {

    init {

    }

    override fun load(data: AboutUiItem) {
        data as AboutUiItem.AboutApp
        binding.tvAppName.text = data.appName
        binding.tvAppVersion.text = data.version + "(${data.versionCode})"
        binding.btnChangelog.text = data.changelogText
        binding.btnSupport.text = data.contactText
        binding.btnSupport.setOnClickListener {
            listener?.onContactClick()
        }
        binding.btnChangelog.setOnClickListener {
            listener?.onChangelogClick()
        }
    }


}