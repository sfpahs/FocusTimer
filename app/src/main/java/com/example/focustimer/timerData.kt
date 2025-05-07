package com.example.focustimer

object timerData {
    val fomodoro = scheduleTime("fo", workTime = 25, restTime = 5)
    val ten_twoRule = scheduleTime("fo", workTime = 10, restTime = 2)

}

data class scheduleTime(
    val category: String,
    val workTime : Int,
    val restTime : Int
)