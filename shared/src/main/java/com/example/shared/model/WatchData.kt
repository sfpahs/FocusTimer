package com.example.shared.model

data class WatchData(
    val timerSetting : TimerSetting = TimerSetting(),
    var activeTimer : Int = 0,
    var time : Int = 0
)
