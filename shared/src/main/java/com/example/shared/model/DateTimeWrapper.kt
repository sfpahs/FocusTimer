package com.example.shared.model

import android.annotation.SuppressLint
import java.time.LocalDateTime

class DateTimeWrapper {
    var dateTime: String? = null

    // Firebase를 위한 인자 없는 생성자
    constructor()

    constructor(dateTime: LocalDateTime) {
        this.dateTime = dateTime.toString()
    }

    @SuppressLint("NewApi")
    fun toLocalDateTime(): LocalDateTime {
        return LocalDateTime.parse(dateTime)
    }
}