package com.pramod.eyecare.framework.ui.settings

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.pramod.eyecare.business.domain.model.SettingItem
import com.pramod.eyecare.business.domain.model.SettingItemEnum
import com.pramod.eyecare.databinding.ItemSettingsLayoutBinding
import com.pramod.eyecare.databinding.LayoutCardSettingGroupBinding
import com.pramod.eyecare.framework.helper.HapticTouchListener
import com.pramod.eyecare.framework.view.setUpListAdapter

private val SettingGroupItemComparator = object : DiffUtil.ItemCallback<SettingGroupUiState>() {
    override fun areItemsTheSame(
        oldItem: SettingGroupUiState, newItem: SettingGroupUiState
    ): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(
        oldItem: SettingGroupUiState, newItem: SettingGroupUiState
    ): Boolean {
        return oldItem == newItem
    }

    override fun getChangePayload(
        oldItem: SettingGroupUiState, newItem: SettingGroupUiState
    ): Any? {
        return bundleOf(
            "id" to newItem.id, "title" to newItem.title, "settingItem" to newItem.settingItem
        )
    }
}

private val SettingItemComparator = object : DiffUtil.ItemCallback<SettingItemUiState>() {
    override fun areItemsTheSame(
        oldItem: SettingItemUiState, newItem: SettingItemUiState
    ): Boolean {
        return oldItem.settingItem.id == newItem.settingItem.id
    }

    override fun areContentsTheSame(
        oldItem: SettingItemUiState, newItem: SettingItemUiState
    ): Boolean {
        return oldItem == newItem
    }
}

class SettingsAdapter(
    private val onSettingItemClickListener: OnSettingItemClickListener
) : ListAdapter<SettingGroupUiState, SettingsAdapter.SettingItemViewHolder>(
    SettingGroupItemComparator
) {

    interface OnSettingItemClickListener {

        fun onItemClick(position: Int, settingItem: SettingItem)

        fun onSwitchClick(enum: SettingItemEnum, isChecked: Boolean)
    }

    inner class SettingItemViewHolder(private val binding: LayoutCardSettingGroupBinding) :
        ViewHolder(binding.root) {

        @SuppressLint("ClickableViewAccessibility")
        fun load(settingGroup: SettingGroupUiState) {

            binding.tvTitle.text = settingGroup.title
            val adapter =
                binding.rvSettingsInnerItems.setUpListAdapter(inflate = ItemSettingsLayoutBinding::inflate,
                    itemComparator = SettingItemComparator,
                    onBindCallback = { pos, rowBinding, settingItemUiState ->
                        val settingItem = settingItemUiState.settingItem
                        rowBinding.item.showIconBackground = true
                        rowBinding.item.title = settingItem.title
                        rowBinding.item.subtitle = settingItem.subTitle
                        rowBinding.item.isSubtitleVisible = settingItem.subTitle != null
                        rowBinding.item.switchVisibility =
                            if (settingItem.showSwitch) View.VISIBLE else View.GONE
                        rowBinding.item.switch.isChecked = settingItemUiState.isSwitchChecked
                        rowBinding.item.switch.setOnTouchListener(HapticTouchListener())
                        rowBinding.item.switch.setOnClickListener {
                            onSettingItemClickListener.onSwitchClick(
                                settingItem.id, rowBinding.item.switch.isChecked
                            )
                        }
                        rowBinding.item.setOnClickListener {
                            onSettingItemClickListener.onItemClick(adapterPosition, settingItem)
                        }
                    },
                    itemInitCallback = {})
            adapter.submitList(settingGroup.settingItem)
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