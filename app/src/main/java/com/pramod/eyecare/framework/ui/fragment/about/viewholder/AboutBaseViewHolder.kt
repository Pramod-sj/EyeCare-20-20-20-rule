package com.pramod.eyecare.framework.ui.fragment.about.viewholder

import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.viewbinding.ViewBinding
import com.pramod.eyecare.framework.ui.fragment.about.AboutAdapter

abstract class AboutBaseViewHolder<T>(private val binding: ViewBinding) : ViewHolder(binding.root) {

    private var aboutItemListener: AboutAdapter.AboutItemListener? = null
    val listener: AboutAdapter.AboutItemListener?
        get() = aboutItemListener

    fun setListener(aboutItemListener: AboutAdapter.AboutItemListener) {
        this.aboutItemListener = aboutItemListener
    }

    abstract fun load(data: T)

}