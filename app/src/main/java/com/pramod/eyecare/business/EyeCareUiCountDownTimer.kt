package com.pramod.eyecare.business

import kotlinx.coroutines.flow.MutableStateFlow

interface EyeCareUiCountDownTimer {


    sealed class AlarmState {

        object NotStarted : AlarmState()

        data class InProgressWork(
            val remainingTimeString: String,
            val percentage: Int = 0,
        ) : AlarmState()

        data class InProgressRest(
            val remainingTimeString: String,
            val percentage: Int = 0,
        ) : AlarmState()

    }

    fun getCountDownTimerData(): MutableStateFlow<AlarmState>

    fun clear()

}