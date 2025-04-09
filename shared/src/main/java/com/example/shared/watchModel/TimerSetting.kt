package com.example.shared.watchModel

data class TimerSetting(
    val category: Int = 0,
    val name: String = "",
    val backgroundColor: Long = 0xFFFFFFFF,
    val workTime: Int = 0,
    val restTime: Int = 0
)
