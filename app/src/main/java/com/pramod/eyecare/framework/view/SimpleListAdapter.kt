package com.pramod.eyecare.framework.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

typealias Inflate<T> = (LayoutInflater, ViewGroup?, Boolean) -> T

class SimpleListAdapter<VB : ViewBinding, Data : Any>(
    private val inflate: Inflate<VB>,
    itemComparator: DiffUtil.ItemCallback<Data>,
    private val onBind: (position: Int, VB, Data) -> Unit,
    private val itemInit: ((SimpleListAdapter<VB, Data>.SimpleViewHolder) -> Unit)? = null,
) : ListAdapter<Data, SimpleListAdapter<VB, Data>.SimpleViewHolder>(itemComparator) {

    inner class SimpleViewHolder(private val binding: VB) : RecyclerView.ViewHolder(binding.root) {

        init {
            itemInit?.invoke(this)
        }

        fun getBinding(): VB {
            return binding
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SimpleViewHolder {
        return SimpleViewHolder(
            inflate.invoke(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: SimpleViewHolder, position: Int) {
        onBind.invoke(position, holder.getBinding(), getItem(position))
    }

}

fun <VB : ViewBinding, Data : Any> RecyclerView.setUpListAdapter(
    inflate: Inflate<VB>,
    itemComparator: DiffUtil.ItemCallback<Data>,
    onBindCallback: (position: Int, VB, Data) -> Unit,
    itemInitCallback: ((SimpleListAdapter<VB, Data>.SimpleViewHolder) -> Unit) = {},
): SimpleListAdapter<VB, Data> {
    val simpleAdapter = SimpleListAdapter(inflate, itemComparator, onBindCallback, itemInitCallback)
    adapter = simpleAdapter
    return simpleAdapter
}