package com.example.shared.model

import android.util.Log
import com.google.firebase.database.DatabaseReference

data class TimerSetting(
    val category: Int = 0,
    val name: String = "",
    val backgroundColor: Long = 0xFFFFFFFF,
    val workTime: Int = 0,
    val restTime: Int = 0
)

fun TimerSetting.toMap(): Map<String, Any> {
    return mapOf(
        "index" to category,
        "name" to name,
        "backgroundColor" to backgroundColor,
        "workTime" to workTime,
        "restTime" to restTime
    )
}