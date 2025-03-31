package com.example.focustimer

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import com.example.focustimer.ui.theme.FocusTimerTheme
import com.example.pre_capstone.DualStopwatchApp

@SuppressLint("RestrictedApi")
class TimerActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val timerName = intent.getStringExtra("timerName") ?: ""
            val category = intent.getIntExtra("category", 1)
            val workTime = intent.getIntExtra("workTime", 60)
            val restTime = intent.getIntExtra("restTime", 10)
            FocusTimerTheme {
                DualStopwatchApp(
                    timerName = timerName,
                    category = category,
                    workTime = workTime,
                    restTime = restTime
                )
            }
        }
    }
}
