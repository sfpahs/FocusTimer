package com.example.pre_capstone.model

import java.time.LocalDateTime

data class HistoryData(
    var startTime : LocalDateTime = LocalDateTime.MIN,
    var category : Int = -1,
    var totalMinute : Int = -1,
    var workingMinute : Int = -1,
    var restMinute : Int = -1,
    var averageWorkingMinute : Int = -1,
)
