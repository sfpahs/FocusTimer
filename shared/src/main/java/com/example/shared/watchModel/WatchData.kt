package com.example.shared.watchModel

data class WatchData(
    val timerSetting : TimerSetting = TimerSetting(),
    var activeTimer : Int = 0,
    var time : Int = 0
)
