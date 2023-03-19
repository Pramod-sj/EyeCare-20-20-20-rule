package com.pramod.eyecare.framework.ui.fragment.about.viewholder

import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.viewbinding.ViewBinding

abstract class BaseViewHolder<T>(private val binding: ViewBinding) :
    ViewHolder(binding.root) {

    abstract fun load(data: T)

}