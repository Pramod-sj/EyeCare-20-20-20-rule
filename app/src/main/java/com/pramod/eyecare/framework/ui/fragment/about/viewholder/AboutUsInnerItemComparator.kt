package com.pramod.eyecare.framework.ui.fragment.about.viewholder

import androidx.recyclerview.widget.DiffUtil
import com.pramod.eyecare.framework.ui.fragment.about.AboutInnerItem

val AboutUsInnerItemComparator = object : DiffUtil.ItemCallback<AboutInnerItem>() {
    override fun areItemsTheSame(oldItem: AboutInnerItem, newItem: AboutInnerItem): Boolean {
        return oldItem.title == newItem.title
    }

    override fun areContentsTheSame(oldItem: AboutInnerItem, newItem: AboutInnerItem): Boolean {
        return oldItem == newItem
    }

}