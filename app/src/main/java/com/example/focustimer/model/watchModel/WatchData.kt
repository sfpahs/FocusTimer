package com.example.focustimer.watchModel

import com.example.focustimer.model.TimerSetting
import kotlinx.coroutines.flow.MutableStateFlow

data class WatchData(
    val timerSetting : TimerSetting = TimerSetting(),
    var activeTimer : Int = 0,
    var time : Int = 0
)
