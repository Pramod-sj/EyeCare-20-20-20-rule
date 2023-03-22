package com.pramod.eyecare.framework.ui.fragment.about

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.pramod.eyecare.databinding.LayoutAboutDeveloperInfoBinding
import com.pramod.eyecare.databinding.LayoutSupportInfoBinding
import com.pramod.eyecare.framework.ui.fragment.about.viewholder.*

val AboutUiItemComparator = object : DiffUtil.ItemCallback<AboutUiItem>() {
    override fun areItemsTheSame(oldItem: AboutUiItem, newItem: AboutUiItem): Boolean {
        return oldItem.hashCode() == newItem.hashCode()
    }

    override fun areContentsTheSame(oldItem: AboutUiItem, newItem: AboutUiItem): Boolean {
        return oldItem == newItem
    }

}

class AboutAdapter(private val listener: AboutItemListener) :
    ListAdapter<AboutUiItem, AboutBaseViewHolder<AboutUiItem>>(AboutUiItemComparator) {

    interface AboutItemListener {
        fun onItemClick(id: AboutInnerItemIdEnum, aboutInnerItem: AboutInnerItem)
        fun onDevFacebookClick(url: String)
        fun onDevInstagramClick(url: String)
        fun onDevGithubClick(url: String)
        fun onDevGmailClick(email: String)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): AboutBaseViewHolder<AboutUiItem> {
        val layoutInflater = LayoutInflater.from(parent.context)
        return when (viewType.toAboutUiItemEnum()) {
            AboutUiItem.Enum.DEVELOPER -> {
                DeveloperViewHolderAbout(
                    binding = LayoutAboutDeveloperInfoBinding.inflate(
                        /* inflater = */ layoutInflater,
                        /* parent = */ parent,
                        /* attachToParent = */ false
                    )
                )
            }
            AboutUiItem.Enum.SUPPORT -> {
                SupportViewHolderAbout(
                    binding = LayoutSupportInfoBinding.inflate(
                        /* inflater = */ layoutInflater,
                        /* parent = */ parent,
                        /* attachToParent = */ false
                    )
                )
            }
            AboutUiItem.Enum.CREDIT -> {
                CreditViewHolderAbout(
                    binding = LayoutSupportInfoBinding.inflate(
                        /* inflater = */ layoutInflater,
                        /* parent = */ parent,
                        /* attachToParent = */ false
                    )
                )
            }
            AboutUiItem.Enum.OTHERS -> {
                OthersViewHolderAbout(
                    binding = LayoutSupportInfoBinding.inflate(
                        /* inflater = */ layoutInflater,
                        /* parent = */ parent,
                        /* attachToParent = */ false
                    )
                )
            }
            else -> throw java.lang.IllegalStateException("No such view type ${viewType.toAboutUiItemEnum()}")
        }
    }

    override fun onBindViewHolder(holder: AboutBaseViewHolder<AboutUiItem>, position: Int) {
        holder.setListener(listener)
        holder.load(getItem(position))
    }

    override fun getItemViewType(position: Int): Int {
        return getItem(position).viewTypeEnum.id
    }
}

