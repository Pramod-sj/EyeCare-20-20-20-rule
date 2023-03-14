package com.pramod.eyecare.business

import kotlinx.coroutines.flow.MutableStateFlow

interface EyeCareUiCountDownTimer {


    sealed class AlarmState {

        object NotStarted : AlarmState()

        data class InProgress(
            val remainingTimeString: String = "",
            val percentage: Int = 0,
        ) : AlarmState()

        object Completed : AlarmState()

        object Cancelled : AlarmState()
    }

    fun getCountDownTimerData(): MutableStateFlow<AlarmState>

    fun get20SecondsGazePercentageValue(): MutableStateFlow<Int>

    fun clear()

}