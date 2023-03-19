package com.pramod.eyecare.framework.ui.fragment.about

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.pramod.eyecare.databinding.LayoutAboutDeveloperInfoBinding
import com.pramod.eyecare.framework.ui.fragment.about.viewholder.BaseViewHolder
import com.pramod.eyecare.framework.ui.fragment.about.viewholder.DeveloperViewHolder

val AboutUiItemComparator = object : DiffUtil.ItemCallback<AboutUiItem>() {
    override fun areItemsTheSame(oldItem: AboutUiItem, newItem: AboutUiItem): Boolean {
        return oldItem.hashCode() == newItem.hashCode()
    }

    override fun areContentsTheSame(oldItem: AboutUiItem, newItem: AboutUiItem): Boolean {
        return oldItem == newItem
    }

}

class AboutAdapter : ListAdapter<AboutUiItem, BaseViewHolder<AboutUiItem>>(AboutUiItemComparator) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<AboutUiItem> {
        val layoutInflater = LayoutInflater.from(parent.context)
        return DeveloperViewHolder(
            binding = LayoutAboutDeveloperInfoBinding.inflate(
                /* inflater = */ layoutInflater,
                /* parent = */ parent,
                /* attachToParent = */ false
            )
        )
    }

    override fun onBindViewHolder(holder: BaseViewHolder<AboutUiItem>, position: Int) {
        holder.load(getItem(position))
    }
}

