package com.example.shared.model

data class Timer(
    val timerSetting : TimerSetting = TimerSetting(),
    var activeTimer : Int = 0,
    var time : Int = 0
)
