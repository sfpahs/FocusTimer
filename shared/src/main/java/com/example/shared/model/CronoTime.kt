package com.example.shared.model

data class CronoTime(
    val name : String,
    val productive1 : time,
    val productive2 : time? = null,
    val creative : time? = null,
    val workout1 : time,
    val workout2 : time? = null,
    val wakeup : time,
    val sleep : time,
    val description : String = ""
)
data class time(
    val start : Float,
    val end : Float
)