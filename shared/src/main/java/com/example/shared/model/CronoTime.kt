package com.example.shared.model

data class CronoTime(
    val name : String = "",
    val productive1 : time = time(),
    val productive2 : time? = null,
    val creative : time? = null,
    val workout1 : time = time(),
    val workout2 : time? = null,
    val wakeup : time = time(),
    val sleep : time = time(),
    val description : String = ""
)
data class time(
    val start : Float = -1f,
    val end : Float = -1f
)