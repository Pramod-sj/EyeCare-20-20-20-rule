package com.pramod.eyecare.framework.ui.fragment.about.viewholder

import com.pramod.eyecare.databinding.ItemAboutInnerLayoutBinding
import com.pramod.eyecare.databinding.LayoutSupportInfoBinding
import com.pramod.eyecare.framework.datasource.model.about.AboutCardItem
import com.pramod.eyecare.framework.view.setUpListAdapter


class SupportViewHolderAbout(
    private val binding: LayoutSupportInfoBinding,
) : AboutBaseViewHolder<AboutCardItem>(binding) {


    init {

    }

    override fun load(data: AboutCardItem) {
        data as AboutCardItem.Support
        binding.tvTitle.text = data.cardTitle
        val adapter = binding.rvAboutUsInnerItems.setUpListAdapter(
            inflate = ItemAboutInnerLayoutBinding::inflate,
            itemComparator = AboutUsInnerItemComparator,
            onBindCallback = { pos, rowBinding, innerItem ->
                innerItem.iconRes?.let {
                    rowBinding.item.icon = (innerItem.iconRes)
                }
                rowBinding.item.title = (innerItem.title)
                rowBinding.item.subtitle = (innerItem.subtitle)
                rowBinding.item.isSubtitleVisible = innerItem.subtitle!=null
                rowBinding.root.setOnClickListener {
                    listener?.onItemClick(innerItem.id, innerItem)
                }
            },
        )
        adapter.submitList(data.innerItems)
    }


}