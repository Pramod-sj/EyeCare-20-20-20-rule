package com.pramod.eyecare.framework.ui.settings

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.elevation.ElevationOverlayProvider
import com.google.android.material.shape.MaterialShapeDrawable
import com.pramod.eyecare.R
import com.pramod.eyecare.business.domain.SettingItem
import com.pramod.eyecare.databinding.FragmentSettingsBinding
import com.pramod.eyecare.framework.helper.NotificationHelper
import com.pramod.eyecare.framework.ui.utils.applyMaterialAxisTransition
import com.pramod.eyecare.framework.ui.utils.doWithInset
import com.pramod.eyecare.framework.ui.utils.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import dev.doubledot.doki.ui.DokiActivity
import timber.log.Timber

@AndroidEntryPoint
class SettingsFragment : Fragment(R.layout.fragment_settings),
    SettingsAdapter.OnSettingItemClickListener {

    private val binding by viewBinding<FragmentSettingsBinding>()

    private val viewModel by viewModels<SettingsViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        applyMaterialAxisTransition()
    }

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
            findNavController().popBackStack()
        }
        ViewCompat.setBackground(
            binding.root,
            MaterialShapeDrawable.createWithElevationOverlay(requireContext(), 4.0f)
        )
    }

    private fun handleInset() {
        binding.root.doWithInset { view, top, bottom ->
            //binding.inclAppBar.appBar.updatePadding(top = top)
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
                findNavController().navigate(R.id.action_settingsFragment_to_aboutFragment)
            }
            SettingItemEnum.UNKNOWN -> Unit
            SettingItemEnum.NOTIFICATION -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val intent = Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS)
                    intent.putExtra(Settings.EXTRA_APP_PACKAGE, requireContext().packageName)
                    intent.putExtra(
                        Settings.EXTRA_CHANNEL_ID,
                        NotificationHelper.CHANNEL_ID_202020_REMINDER_IMPORTANT_HIGH
                    )
                    startActivity(intent)
                }
            }
            SettingItemEnum.APP_NOT_WORKING_PROPERLY -> DokiActivity.start(context = requireContext())
            else -> Unit
        }
    }

    override fun onSwitchClick(enum: SettingItemEnum, isChecked: Boolean) {
        Timber.d("onSwitchClick: $enum $isChecked")
        when (enum) {
            SettingItemEnum.PLAY_WORK_RINGTONE -> viewModel.togglePlayWorkRingtone()
            else -> Unit
        }
    }

}