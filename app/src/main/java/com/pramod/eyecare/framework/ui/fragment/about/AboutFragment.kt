package com.pramod.eyecare.framework.ui.fragment.about

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.pramod.eyecare.R
import com.pramod.eyecare.databinding.FragmentAboutBinding
import com.pramod.eyecare.framework.ui.utils.doWithInset
import com.pramod.eyecare.framework.ui.utils.viewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AboutFragment : Fragment(R.layout.fragment_about), AboutAdapter.AboutItemListener {

    private val binding by viewBinding<FragmentAboutBinding>()

    private val viewModel by viewModels<AboutViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        handleInset()
        setUpToolbar()
        bindAboutAdapter()
    }

    private fun bindAboutAdapter() {
        val adapter = AboutAdapter(this)
        binding.rvAboutUsItems.adapter = adapter
        viewModel.aboutUiItems.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
    }

    private fun handleInset() {
        binding.root.doWithInset { view, top, bottom ->
            binding.inclAppBar.appBar.updatePadding(top = top)
        }
    }

    private fun setUpToolbar() {
        binding.inclAppBar.toolbar.title = "About us"
        binding.inclAppBar.toolbar.navigationIcon =
            ContextCompat.getDrawable(requireContext(), R.drawable.ic_round_keyboard_backspace_24)
        binding.inclAppBar.toolbar.setNavigationOnClickListener {
        }
    }

    override fun onItemClick(id: AboutInnerItemIdEnum, aboutInnerItem: AboutInnerItem) {
        when (id) {
            AboutInnerItemIdEnum.FORK_ON_GITHUB -> {}
            AboutInnerItemIdEnum.DONATE -> {}
            AboutInnerItemIdEnum.SHARE_APP -> {}
            AboutInnerItemIdEnum.RATE_US -> {}
            AboutInnerItemIdEnum.APP_LOGO_CREDIT -> {}
            AboutInnerItemIdEnum.OPEN_SOURCE_LIBRARIES -> {}
            AboutInnerItemIdEnum.TERM_AND_SERVICES -> {}
            AboutInnerItemIdEnum.PRIVACY_POLICY -> {}
        }
    }

}