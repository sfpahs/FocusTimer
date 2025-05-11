package com.example.focustimer.Activity

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.focustimer.MyBottomNavi
import com.example.focustimer.Page.TimerStoppedReceiver
import com.example.focustimer.TimerService
import com.example.focustimer.ui.theme.FocusTimerTheme

class MainActivity : ComponentActivity() {
    private lateinit var navController: NavHostController
    companion object{
        lateinit var timerStoppedReceiver : TimerStoppedReceiver
    }
    private val timerStopReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            Log.d("MainActivity", "onReceive: 타이머 스탑 리시버 받음")
            if (intent.action == TimerService.ACTION_STOP) {
                // ANR 방지를 위해 goAsync() 사용
                val pendingResult = goAsync()
                Handler(Looper.getMainLooper()).post {
                    navController.popBackStack()
                    navController.navigate("main")
                    pendingResult.finish()
                }
            }
        }
    }
    private val navigationReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == "com.example.pre_capstone.NAVIGATION_ACTION") {
                val destination = intent.getStringExtra("destination") ?: return
                runOnUiThread {
                    when (destination) {
                        "main" -> navController.navigate("main")
                        // 다른 화면으로의 이동도 추가 가능
                    }
                }
            }
        }
    }

    @SuppressLint("NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)


        registerReceiver(timerStopReceiver, IntentFilter(TimerService.ACTION_STOP),
            RECEIVER_NOT_EXPORTED
        )
        registerReceiver(navigationReceiver, IntentFilter("com.example.pre_capstone.NAVIGATION_ACTION"),
            RECEIVER_NOT_EXPORTED
        )
        enableEdgeToEdge()
        setContent {
            FocusTimerTheme {
                navController = rememberNavController()
                MyBottomNavi(navController = navController)
            }
        }

    }


    override fun onDestroy() {
        super.onDestroy()
        try {
            unregisterReceiver(navigationReceiver)
            unregisterReceiver(timerStopReceiver)
        }catch (e : IllegalArgumentException){
            Log.e("MainActivity", "Receiver not registered", e)
        }
    }
}
