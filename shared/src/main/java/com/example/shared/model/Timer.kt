package com.example.shared.model

data class Timer(
    val subject : subject = subject(),
    var activeTimer : Int = 0,
    var time : Int = 0
)
