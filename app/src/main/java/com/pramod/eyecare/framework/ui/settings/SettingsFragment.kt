package com.pramod.eyecare.framework.ui.settings

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.marginTop
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.pramod.eyecare.R
import com.pramod.eyecare.business.domain.SettingItem
import com.pramod.eyecare.databinding.FragmentSettingsBinding
import com.pramod.eyecare.framework.ui.utils.doWithInset
import com.pramod.eyecare.framework.ui.utils.viewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsFragment : Fragment(R.layout.fragment_settings),
    SettingsAdapter.OnSettingItemClickListener {

    private val binding by viewBinding<FragmentSettingsBinding>()

    private val viewModel by viewModels<SettingsViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpToolbar()
        handleInset()
        bindSettingItems()
    }

    private fun setUpToolbar() {
        binding.inclAppBar.toolbar.title = "Settings"
        binding.inclAppBar.toolbar.navigationIcon =
            ContextCompat.getDrawable(requireContext(), R.drawable.ic_round_keyboard_backspace_24)
        binding.inclAppBar.toolbar.setNavigationOnClickListener {
        }
    }

    private fun handleInset() {
        binding.root.doWithInset { view, top, bottom ->
            binding.inclAppBar.appBar.updatePadding(top = top)
        }
    }


    private fun bindSettingItems() {
        val adapter = SettingsAdapter(this)
        binding.rvSettingItems.adapter = adapter
        viewModel.settingItems.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
    }

    override fun onItemClick(position: Int, settingItem: SettingItem) {
        when (settingItem.id.toSettingItemEnum()) {
            SettingItemEnum.RATE_APP -> {

            }
            SettingItemEnum.SHARE_APP -> {

            }
            SettingItemEnum.ABOUT_US -> {

            }
            SettingItemEnum.UNKNOWN -> Unit
        }
    }

}