package com.pramod.eyecare.framework.ui.fragment.about.viewholder

import com.pramod.eyecare.databinding.ItemAboutInnerLayoutBinding
import com.pramod.eyecare.databinding.LayoutSupportInfoBinding
import com.pramod.eyecare.framework.ui.fragment.about.AboutUiItem
import com.pramod.eyecare.framework.view.setUpListAdapter

class CreditViewHolderAbout(
    private val binding: LayoutSupportInfoBinding,
) : AboutBaseViewHolder<AboutUiItem>(binding) {

    init {

    }

    override fun load(data: AboutUiItem) {
        data as AboutUiItem.Credit
        binding.tvTitle.text = data.cardTitle
        val adapter = binding.rvAboutUsInnerItems.setUpListAdapter(
            inflate = ItemAboutInnerLayoutBinding::inflate,
            itemComparator = AboutUsInnerItemComparator,
            onBindCallback = { pos, rowBinding, innerItem ->
                innerItem.iconRes?.let {
                    rowBinding.item.icon = innerItem.iconRes
                }
                rowBinding.item.title = (innerItem.title)
                rowBinding.item.subtitle = (innerItem.subtitle)
                rowBinding.item.isSubtitleVisible = innerItem.subtitle != null
                rowBinding.root.setOnClickListener {
                    listener?.onItemClick(innerItem.id, innerItem)
                }
            },
        )
        adapter.submitList(data.innerItems)
    }


}