package com.example.focustimer.Activity

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import com.example.focustimer.Page.timer.DualStopwatchApp
import com.example.focustimer.ui.theme.FocusTimerTheme

@SuppressLint("RestrictedApi")
class TimerActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FocusTimerTheme {
                DualStopwatchApp(
                )
            }
        }
    }
}
