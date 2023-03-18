package com.pramod.eyecare.framework.ui.settings

import android.os.Bundle
import android.view.View
import androidx.core.view.marginTop
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import com.pramod.eyecare.R
import com.pramod.eyecare.databinding.FragmentSettingsBinding
import com.pramod.eyecare.framework.ui.utils.doWithInset
import com.pramod.eyecare.framework.ui.utils.viewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsFragment : Fragment(R.layout.fragment_settings) {

    private val binding by viewBinding<FragmentSettingsBinding>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.root.doWithInset { view, top, bottom ->
            binding.clAppbar.updatePadding(top= binding.clAppbar.paddingTop + top)
        }
    }

}