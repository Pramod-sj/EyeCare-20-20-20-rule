package com.pramod.eyecare.framework.ui.fragment.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.pramod.eyecare.business.EyeCareUiCountDownTimer
import com.pramod.eyecare.business.GetRandomEyeCareTip
import com.pramod.eyecare.business.RemoteConfig
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val eyeCareUiCountDownTimer: EyeCareUiCountDownTimer,
    private val getRandomEyeCareTip: GetRandomEyeCareTip
) : ViewModel() {

    private val _isServiceRunning = MutableStateFlow(false)
    val isServiceRunning = _isServiceRunning.asLiveData()

    val uiAlarmStateTimer = eyeCareUiCountDownTimer.getCountDownTimerData().asLiveData()

    val gazePercentage = eyeCareUiCountDownTimer.get20SecondsGazePercentageValue().asLiveData()

    val tip = getRandomEyeCareTip().asLiveData()

    private val _startTime = MutableStateFlow<Long?>(null)
    val startTime = _startTime.asLiveData()

    private val _endTime = MutableStateFlow<Long?>(null)
    val endTime = _endTime.asLiveData()

    fun setStartTimeInMillis(timeInMillis: Long) {
        _startTime.value = timeInMillis
    }

    fun setEndTimeInMillis(timeInMillis: Long) {
        _endTime.value = timeInMillis
    }

    fun setIsServiceRunning(isRunning: Boolean) {
        _isServiceRunning.value = isRunning
    }

    override fun onCleared() {
        super.onCleared()
        eyeCareUiCountDownTimer.clear()
    }
}