package com.example.focustimer.watchModel

import com.example.focustimer.model.TimerSetting
import kotlinx.coroutines.flow.MutableStateFlow

data class WatchData(
    val timerSetting : TimerSetting = TimerSetting(),
    val activeTimer : Int = 0,
    val time : Int = 0
)
