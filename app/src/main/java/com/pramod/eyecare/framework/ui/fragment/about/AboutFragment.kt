package com.pramod.eyecare.framework.ui.fragment.about

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.pramod.eyecare.R
import com.pramod.eyecare.databinding.FragmentAboutBinding
import com.pramod.eyecare.framework.ui.fragment.donate.DonateBottomDialogFragment
import com.pramod.eyecare.framework.ui.openWebsite
import com.pramod.eyecare.framework.ui.utils.applyMaterialAxisTransition
import com.pramod.eyecare.framework.ui.utils.doWithInset
import com.pramod.eyecare.framework.ui.utils.viewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AboutFragment : Fragment(R.layout.fragment_about), AboutAdapter.AboutItemListener {

    private val binding by viewBinding<FragmentAboutBinding>()

    private val viewModel by viewModels<AboutViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        applyMaterialAxisTransition()
    }

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
            binding.rvAboutUsItems.updatePadding(bottom = bottom)
        }
    }

    private fun setUpToolbar() {
        binding.inclAppBar.toolbar.title = "About"
        binding.inclAppBar.toolbar.navigationIcon =
            ContextCompat.getDrawable(requireContext(), R.drawable.ic_round_keyboard_backspace_24)
        binding.inclAppBar.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun onItemClick(id: AboutInnerItemIdEnum, aboutInnerItem: AboutInnerItem) {
        when (id) {
            AboutInnerItemIdEnum.FORK_ON_GITHUB -> {}
            AboutInnerItemIdEnum.DONATE -> {
                DonateBottomDialogFragment.show(childFragmentManager)
            }
            AboutInnerItemIdEnum.SHARE_APP -> {}
            AboutInnerItemIdEnum.RATE_US -> {}
            AboutInnerItemIdEnum.APP_LOGO_CREDIT -> {}
            AboutInnerItemIdEnum.OPEN_SOURCE_LIBRARIES -> {}
            AboutInnerItemIdEnum.TERM_AND_SERVICES -> {}
            AboutInnerItemIdEnum.PRIVACY_POLICY -> {}
        }
    }

    override fun onDevFacebookClick(url: String) {
        openWebsite(url)
    }

    override fun onDevInstagramClick(url: String) {
        openWebsite(url)
    }

    override fun onDevGithubClick(url: String) {
        openWebsite(url)
    }

    override fun onDevGmailClick(email: String) {

    }

    override fun onChangelogClick() {

    }

    override fun onContactClick() {

    }

}