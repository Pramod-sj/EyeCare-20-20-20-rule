package com.pramod.eyecare.framework.ui.fragment.about.viewholder

import com.pramod.eyecare.BuildConfig
import com.pramod.eyecare.R
import com.pramod.eyecare.databinding.LayoutAboutAppInfoBinding
import com.pramod.eyecare.framework.datasource.model.about.AboutCardItem

class AboutAppViewHolder(
    private val binding: LayoutAboutAppInfoBinding,
) : AboutBaseViewHolder<AboutCardItem>(binding) {

    init {

    }

    override fun load(data: AboutCardItem) {
        data as AboutCardItem.AboutApp
        binding.tvAppName.text = binding.root.resources.getString(R.string.app_name)
        binding.tvAppVersion.text = BuildConfig.VERSION_NAME + "(${BuildConfig.VERSION_CODE})"
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