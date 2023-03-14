package com.pramod.eyecare.framework.ui.fragment.home

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.transition.TransitionManager
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.view.marginBottom
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.pramod.eyecare.*
import com.pramod.eyecare.business.EyeCareUiCountDownTimer
import com.pramod.eyecare.databinding.FragmentHomeBinding
import com.pramod.eyecare.framework.helper.VibrationHelper
import com.pramod.eyecare.framework.ui.utils.doWithInset
import com.pramod.eyecare.framework.ui.utils.isServiceRunning
import com.pramod.eyecare.framework.ui.utils.updateMargin
import com.pramod.eyecare.framework.ui.utils.viewBinding
import com.pramod.eyecare.framework.service.EyeCarePersistentForegroundService
import com.pramod.eyecare.framework.service.NotificationForegroundService
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateViewInset()
        checkServiceRunningState()
        bindServiceActiveState()
        handleStartStopFabClick()
        bindTimerData()
        viewModel.gazePercentage.observe(viewLifecycleOwner) { progress ->
            Timber.i("onViewCreated: " + progress)
            binding.progressGazePercentage.setProgressCompat(progress, true)
        }
    }

    private fun bindTimerData() {
        viewModel.uiAlarmStateTimer.observe(viewLifecycleOwner) { state ->
            Timber.d("bindTimerData: $state")
            TransitionManager.beginDelayedTransition(binding.root)
            when (state) {
                EyeCareUiCountDownTimer.AlarmState.NotStarted -> {
                    binding.progressTimeRemaining.hide()
                    binding.lottie.isVisible = false
                    binding.tvTimeRemaining.isVisible = false
                    binding.lottieWorkingGuy.isVisible = true
                }
                is EyeCareUiCountDownTimer.AlarmState.InProgress -> {
                    binding.progressTimeRemaining.show()
                    binding.lottie.isVisible = false
                    binding.tvTimeRemaining.isVisible = true
                    binding.lottieWorkingGuy.isVisible = false
                    binding.tvTimeRemaining.text = state.remainingTimeString
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        binding.progressTimeRemaining.setProgress(100 - state.percentage, true)
                    } else {
                        binding.progressTimeRemaining.progress = 100 - state.percentage
                    }
                }
                EyeCareUiCountDownTimer.AlarmState.Completed -> {
                    binding.progressTimeRemaining.hide()
                    binding.lottie.isVisible = true
                    binding.tvTimeRemaining.isVisible = false
                    binding.lottieWorkingGuy.isVisible = false
                }
                EyeCareUiCountDownTimer.AlarmState.Cancelled -> {
                    binding.progressTimeRemaining.hide()
                    binding.lottie.isVisible = false
                    binding.tvTimeRemaining.isVisible = false
                    binding.lottieWorkingGuy.isVisible = true
                }
            }
        }
    }

    private fun updateViewInset() {
        binding.root.doWithInset { view, top, bottom ->
            binding.fabStartService.updateMargin(bottom = binding.fabStartService.marginBottom + bottom)
            binding.fabStopService.updateMargin(bottom = binding.fabStopService.marginBottom + bottom)
            binding.tvGreeting.updatePadding(top = binding.tvGreeting.paddingTop + top)
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
                    requireContext(),
                    Manifest.permission.POST_NOTIFICATIONS
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