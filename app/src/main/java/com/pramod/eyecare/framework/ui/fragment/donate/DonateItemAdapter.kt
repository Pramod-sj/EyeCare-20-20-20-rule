package com.pramod.eyecare.framework.ui.fragment.donate

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.pramod.eyecare.business.Constant
import com.pramod.eyecare.databinding.ItemDonateLayoutBinding

class DonateItemAdapter(val itemClickCallback: ((Int, DonateItem) -> Unit)? = null) :
    ListAdapter<DonateItem, DonateItemAdapter.DonateItemViewHolder>(
        DonateItemComparator
    ) {

    inner class DonateItemViewHolder(val binding: ItemDonateLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun load(donateItem: DonateItem) {
            val color = ContextCompat.getColor(binding.root.context, donateItem.color)
            val alphaAppliedColor =
                ColorUtils.setAlphaComponent(color, Constant.COLOR_ALPHA_DONATE_ITEM)
            val strokeColor = ColorUtils.setAlphaComponent(color, 90)
            binding.tvItemDesc.text = donateItem.title
            binding.ivDonateItem.setImageResource(donateItem.drawableId)
            binding.ivDonateItem.imageTintList = ColorStateList.valueOf(color)
            binding.tvDonateItem.text = donateItem.amount
            binding.card.strokeColor = strokeColor
            binding.card.setCardBackgroundColor(alphaAppliedColor)
            binding.tvDonateItem.setTextColor(color)
            binding.tvItemDesc.setTextColor(color)
            binding.root.setOnClickListener {
                itemClickCallback?.invoke(adapterPosition, donateItem)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DonateItemViewHolder {
        val itemDonateBinding =
            ItemDonateLayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent, false
            )
        return DonateItemViewHolder(itemDonateBinding)
    }

    override fun onBindViewHolder(holder: DonateItemViewHolder, position: Int) {
        val donateItem = getItem(position)
        holder.load(donateItem)
    }

}

object DonateItemComparator : DiffUtil.ItemCallback<DonateItem>() {
    override fun areItemsTheSame(oldItem: DonateItem, newItem: DonateItem): Boolean {
        return oldItem.title == newItem.title
    }

    override fun areContentsTheSame(oldItem: DonateItem, newItem: DonateItem): Boolean {
        return oldItem == newItem
    }

    override fun getChangePayload(oldItem: DonateItem, newItem: DonateItem): Bundle {
        return bundleOf(
            "amount" to newItem.amount,
            "color" to newItem.color,
            "donateItemState" to newItem.donateItemState,
        )
    }

}
