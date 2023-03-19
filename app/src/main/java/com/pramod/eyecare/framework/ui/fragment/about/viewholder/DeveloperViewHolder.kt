package com.pramod.eyecare.framework.ui.fragment.about.viewholder

import coil.load
import com.pramod.eyecare.databinding.LayoutAboutDeveloperInfoBinding
import com.pramod.eyecare.framework.ui.fragment.about.AboutUiItem

class DeveloperViewHolder(
    private val binding: LayoutAboutDeveloperInfoBinding,
) : BaseViewHolder<AboutUiItem>(binding) {

    init {

    }

    override fun load(data: AboutUiItem) {
        data as AboutUiItem.About
        binding.tvDesignAndDevelopLabel.text = data.developerAndDesignLabel
        binding.tvDesignAndDevelopName.text = data.developerName
        binding.ivDevPicture.load(data.developerPicUrl)
    }


}