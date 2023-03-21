package com.pramod.eyecare.framework.ui.settings

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.pramod.eyecare.business.domain.SettingGroup
import com.pramod.eyecare.business.domain.SettingItem
import com.pramod.eyecare.databinding.ItemSettingsLayoutBinding
import com.pramod.eyecare.databinding.LayoutCardSettingGroupBinding
import com.pramod.eyecare.framework.view.setUpListAdapter

private val SettingGroupItemComparator = object : DiffUtil.ItemCallback<SettingGroup>() {
    override fun areItemsTheSame(oldItem: SettingGroup, newItem: SettingGroup): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: SettingGroup, newItem: SettingGroup): Boolean {
        return oldItem == newItem
    }
}

private val SettingItemComparator = object : DiffUtil.ItemCallback<SettingItem>() {
    override fun areItemsTheSame(oldItem: SettingItem, newItem: SettingItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: SettingItem, newItem: SettingItem): Boolean {
        return oldItem == newItem
    }
}

class SettingsAdapter(private val onSettingItemClickListener: OnSettingItemClickListener) :
    ListAdapter<SettingGroup, SettingsAdapter.SettingItemViewHolder>(SettingGroupItemComparator) {

    interface OnSettingItemClickListener {
        fun onItemClick(position: Int, settingItem: SettingItem)
    }

    inner class SettingItemViewHolder(private val binding: LayoutCardSettingGroupBinding) :
        ViewHolder(binding.root) {

        fun load(settingGroup: SettingGroup) {

            binding.tvTitle.text = settingGroup.title
            val adapter = binding.rvSettingsInnerItems.setUpListAdapter(
                inflate = ItemSettingsLayoutBinding::inflate,
                itemComparator = SettingItemComparator,
                onBindCallback = { pos, rowBinding, settingItem ->
                    rowBinding.item.showIconBackground = true
                    rowBinding.item.title = settingItem.title
                    rowBinding.item.subtitle = settingItem.subTitle
                    rowBinding.item.isSubtitleVisible = settingItem.subTitle != null
                    rowBinding.item.setOnClickListener {
                        onSettingItemClickListener.onItemClick(adapterPosition, settingItem)
                    }
                },
                itemInitCallback = {}
            )
            adapter.submitList(settingGroup.items)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SettingItemViewHolder {
        return SettingItemViewHolder(
            binding = LayoutCardSettingGroupBinding.inflate(
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