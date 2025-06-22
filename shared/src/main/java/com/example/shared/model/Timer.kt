package com.example.shared.model

data class Timer(
    val MySubject : MySubject = MySubject(),
    var activeTimer : Int = 0,
    var time : Int = 0
)
