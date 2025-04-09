package com.example.shared

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDateTime

data class HistoryData @RequiresApi(Build.VERSION_CODES.O) constructor(
    var startTime : LocalDateTime = LocalDateTime.MIN,
    var category : Int = -1,
    var totalMinute : Int = -1,
    var workingMinute : Int = -1,
    var restMinute : Int = -1,
    var averageWorkingMinute : Int = -1,
)
