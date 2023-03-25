package com.pramod.eyecare.framework.ui.fragment.about

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.pramod.eyecare.databinding.LayoutAboutAppInfoBinding
import com.pramod.eyecare.databinding.LayoutAboutDeveloperInfoBinding
import com.pramod.eyecare.databinding.LayoutSupportInfoBinding
import com.pramod.eyecare.framework.datasource.model.about.AboutCardItem
import com.pramod.eyecare.framework.datasource.model.about.AboutInnerItem
import com.pramod.eyecare.framework.datasource.model.about.toAboutUiItemEnum
import com.pramod.eyecare.framework.ui.fragment.about.viewholder.*

val AboutUiItemComparator = object : DiffUtil.ItemCallback<AboutCardItem>() {
    override fun areItemsTheSame(oldItem: AboutCardItem, newItem: AboutCardItem): Boolean {
        return oldItem.hashCode() == newItem.hashCode()
    }

    override fun areContentsTheSame(oldItem: AboutCardItem, newItem: AboutCardItem): Boolean {
        return oldItem == newItem
    }

}

class AboutAdapter(private val listener: AboutItemListener) :
    ListAdapter<AboutCardItem, AboutBaseViewHolder<AboutCardItem>>(AboutUiItemComparator) {

    interface AboutItemListener {
        fun onItemClick(id: AboutInnerItem.Enum, aboutInnerItem: AboutInnerItem)
        fun onDevFacebookClick(url: String)
        fun onDevInstagramClick(url: String)
        fun onDevGithubClick(url: String)
        fun onDevGmailClick(email: String)
        fun onChangelogClick()
        fun onContactClick()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): AboutBaseViewHolder<AboutCardItem> {
        val layoutInflater = LayoutInflater.from(parent.context)
        return when (viewType.toAboutUiItemEnum()) {
            AboutCardItem.Enum.APP -> {
                AboutAppViewHolder(
                    binding = LayoutAboutAppInfoBinding.inflate(
                        /* inflater = */ layoutInflater,
                        /* parent = */ parent,
                        /* attachToParent = */ false
                    )
                )
            }
            AboutCardItem.Enum.SUPPORT -> {
                SupportViewHolderAbout(
                    binding = LayoutSupportInfoBinding.inflate(
                        /* inflater = */ layoutInflater,
                        /* parent = */ parent,
                        /* attachToParent = */ false
                    )
                )
            }
            AboutCardItem.Enum.CREDIT -> {
                CreditViewHolderAbout(
                    binding = LayoutSupportInfoBinding.inflate(
                        /* inflater = */ layoutInflater,
                        /* parent = */ parent,
                        /* attachToParent = */ false
                    )
                )
            }
            AboutCardItem.Enum.OTHERS -> {
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

    override fun onBindViewHolder(holder: AboutBaseViewHolder<AboutCardItem>, position: Int) {
        holder.setListener(listener)
        holder.load(getItem(position))
    }

    override fun getItemViewType(position: Int): Int {
        return getItem(position).viewTypeEnum.id
    }
}

