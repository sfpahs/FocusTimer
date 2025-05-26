package com.example.shared.model

data class TimerOption(
    val id : Int = -1,
    val name : String = "",
    val category: String = "",
    val description: String = "",
    val workTime : Int = -1,
    val restTime : Int = -1,
)