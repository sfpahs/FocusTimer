package com.example.shared.model

import android.annotation.SuppressLint
import java.time.LocalDateTime

data class HistoryData @SuppressLint("NewApi") constructor(
    var startTime : DateTimeWrapper = DateTimeWrapper(LocalDateTime.MIN),
    var category : Int = -1,
    var totalMinute : Int = -1,
    var workingMinute : Int = -1,
    var restMinute : Int = -1,
    var averageWorkingMinute : Int = -1,
    var description : String = "",
)
