package com.pramod.eyecare.framework.ui.fragment.about.viewholder

import com.pramod.eyecare.databinding.ItemAboutInnerLayoutBinding
import com.pramod.eyecare.databinding.LayoutSupportInfoBinding
import com.pramod.eyecare.framework.ui.fragment.about.AboutUiItem
import com.pramod.eyecare.framework.view.setUpListAdapter


class SupportViewHolderAbout(
    private val binding: LayoutSupportInfoBinding,
) : AboutBaseViewHolder<AboutUiItem>(binding) {


    init {

    }

    override fun load(data: AboutUiItem) {
        data as AboutUiItem.Support
        binding.tvTitle.text = data.cardTitle
        val adapter = binding.rvAboutUsInnerItems.setUpListAdapter(
            inflate = ItemAboutInnerLayoutBinding::inflate,
            itemComparator = AboutUsInnerItemComparator,
            onBindCallback = { pos, rowBinding, innerItem ->
                innerItem.iconRes?.let {
                    rowBinding.item.setIcon(innerItem.iconRes)
                }
                rowBinding.item.setTitle(innerItem.title)
                rowBinding.item.setSubTitle(innerItem.subtitle)
                rowBinding.root.setOnClickListener {
                    listener?.onItemClick(innerItem.id, innerItem)
                }
            },
        )
        adapter.submitList(data.innerItems)
    }


}