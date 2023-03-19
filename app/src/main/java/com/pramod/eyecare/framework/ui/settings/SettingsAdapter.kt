package com.pramod.eyecare.framework.ui.settings

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.pramod.eyecare.business.domain.SettingItem
import com.pramod.eyecare.databinding.ItemSettingsLayoutBinding

private val SettingItemComparator = object : DiffUtil.ItemCallback<SettingItem>() {
    override fun areItemsTheSame(oldItem: SettingItem, newItem: SettingItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: SettingItem, newItem: SettingItem): Boolean {
        return oldItem == newItem
    }
}

class SettingsAdapter(private val onSettingItemClickListener: OnSettingItemClickListener) :
    ListAdapter<SettingItem, SettingsAdapter.SettingItemViewHolder>(SettingItemComparator) {

    interface OnSettingItemClickListener {
        fun onItemClick(position: Int, settingItem: SettingItem)
    }

    inner class SettingItemViewHolder(private val binding: ItemSettingsLayoutBinding) :
        ViewHolder(binding.root) {

        fun load(settingItem: SettingItem) {
            binding.item.setTitle(settingItem.title)
            binding.item.setSubTitle(settingItem.subTitle)
            binding.item.setIsSubtitleVisible(settingItem.subTitle != null)
            binding.root.setOnClickListener {
                onSettingItemClickListener.onItemClick(adapterPosition, settingItem)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SettingItemViewHolder {
        return SettingItemViewHolder(
            binding = ItemSettingsLayoutBinding.inflate(
                /* inflater = */ LayoutInflater.from(parent.context),
                /* parent = */ parent,
                /* attachToParent = */ false
            )
        )
    }

    override fun onBindViewHolder(holder: SettingItemViewHolder, position: Int) {
        holder.load(getItem(position))
    }

}