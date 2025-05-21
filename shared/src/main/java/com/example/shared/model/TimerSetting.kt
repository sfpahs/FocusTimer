package com.example.shared.model

data class TimerSetting(
    val category: Int = -1,
    val name: String = "",
    val backgroundColor: Long = 0xFFFFFFFF,
    val workTime: Int = -1,
    val restTime: Int = -1
)

fun TimerSetting.toMap(): Map<String, Any> {
    return mapOf(
        "category" to category,
        "name" to name,
        "backgroundColor" to backgroundColor,
        "workTime" to workTime,
        "restTime" to restTime
    )
}