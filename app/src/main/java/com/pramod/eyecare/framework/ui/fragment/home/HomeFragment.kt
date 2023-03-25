package com.pramod.eyecare.framework.ui.fragment.home

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.view.marginBottom
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.fragment.findNavController
import coil.load
import com.pramod.eyecare.*
import com.pramod.eyecare.business.EyeCareUiCountDownTimer
import com.pramod.eyecare.databinding.FragmentHomeBinding
import com.pramod.eyecare.framework.helper.VibrationHelper
import com.pramod.eyecare.framework.service.EyeCarePersistentForegroundService
import com.pramod.eyecare.framework.ui.utils.*
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home) {

    private val binding by viewBinding<FragmentHomeBinding>()

    private val viewModel by viewModels<HomeViewModel>()

    @Inject
    lateinit var vibrationHelper: VibrationHelper

    @Inject
    lateinit var localBroadcastManager: LocalBroadcastManager

    private val activityResultLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { map ->
        if (map.all { it.value }) {
            binding.fabStartService.callOnClick()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        applyMaterialAxisTransition()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        applyInsets()
        setUpToolbar()
        checkServiceRunningState()
        bindServiceActiveState()
        handleStartStopFabClick()
        bindTimerData()
        bindTips()
    }

    private fun applyInsets() {
        binding.fabStartService.doWithInset { view, top, bottom ->
            binding.fabStartService.updateMargin(bottom = bottom + binding.fabStartService.marginBottom)
        }
        binding.fabStopService.doWithInset { view, top, bottom ->
            binding.fabStopService.updateMargin(bottom = bottom + binding.fabStopService.marginBottom)
        }
    }

    private fun setUpToolbar() {
        binding.clAppbar.toolbar.title = resources.getString(R.string.app_name)
        binding.clAppbar.btnOptionMenu.isVisible = true
        binding.clAppbar.btnOptionMenu.load(R.drawable.ic_outline_settings_24)
        binding.clAppbar.btnOptionMenu.setOnClickListener {
            vibrationHelper.vibrate(100)
            findNavController().navigate(R.id.action_homeFragment_to_settingsFragment)
        }
    }

    private fun bindTips() {
        viewModel.tip.observe(viewLifecycleOwner) {
            binding.tvTips.text = it
        }
    }

    @SuppressLint("SetTextI18n")
    private fun bindTimerData() {
        viewModel.uiAlarmStateTimer.observe(viewLifecycleOwner) { state ->
            Timber.d("bindTimerData: $state")
            when (state) {
                EyeCareUiCountDownTimer.AlarmState.NotStarted -> {
                    binding.inclResting.root.isVisible = false
                    binding.inclWorking.root.isVisible = true
                    binding.inclWorking.tvMinRemaining.text = "20"
                    binding.inclWorking.tvSecRemaining.text = "00"
                    binding.inclWorking.progressTimeRemaining.setProgressCompat(100, true)
                }
                is EyeCareUiCountDownTimer.AlarmState.InProgressWork -> {
                    binding.inclWorking.root.isVisible = true
                    binding.inclResting.root.isVisible = false
                    val time = state.remainingTimeString.split(":")
                    binding.inclWorking.tvMinRemaining.text = time.firstOrNull()
                    binding.inclWorking.tvSecRemaining.text = time.lastOrNull()
                    binding.inclWorking.progressTimeRemaining.setProgressCompat(
                        100 - state.percentage, true
                    )
                }
                is EyeCareUiCountDownTimer.AlarmState.InProgressRest -> {
                    binding.inclWorking.root.isVisible = false
                    binding.inclResting.root.isVisible = true
                    binding.inclResting.lottie.isVisible = true
                    binding.inclResting.progressGazePercentage.setProgressCompat(
                        state.percentage, true
                    )
                }
            }
        }
    }

    private fun checkServiceRunningState() {
        viewModel.uiAlarmStateTimer.observe(viewLifecycleOwner) {
            viewModel.setIsServiceRunning(
                requireContext().isServiceRunning(
                    EyeCarePersistentForegroundService::class.java
                )
            )
        }
    }

    private fun handleStartStopFabClick() {
        binding.fabStartService.setOnClickListener {
            if (checkNotificationPermissionAndRequest()) {
                EyeCarePersistentForegroundService.startService(requireContext())
                vibrationHelper.vibrate(100)
            }
        }
        binding.fabStopService.setOnClickListener {
            EyeCarePersistentForegroundService.stopService(requireContext())
            vibrationHelper.vibrate(100)
        }
    }

    //check and ask for permission
    //return true if permission already granted otherwise false
    private fun checkNotificationPermissionAndRequest(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    requireContext(), Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                activityResultLauncher.launch(arrayOf(Manifest.permission.POST_NOTIFICATIONS))
                false
            } else true
        } else true
    }

    private fun bindServiceActiveState() {
        viewModel.isServiceRunning.observe(viewLifecycleOwner) { isActive ->
            Timber.d("bindServiceActiveState: $isActive")
            if (isActive) {
                binding.fabStartService.hide()
                binding.fabStopService.show()
            } else {
                binding.fabStartService.show()
                binding.fabStopService.hide()
            }
        }
    }

}