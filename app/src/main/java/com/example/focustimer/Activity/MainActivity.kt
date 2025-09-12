package com.example.focustimer.Activity

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.preference.PreferenceManager
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.view.WindowCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.focustimer.navigation.MyBottomNavi
import com.example.focustimer.Page.timer.TimerStoppedReceiver
import com.example.focustimer.service.TimerService
import com.example.focustimer.ui.theme.FocusTimerTheme

class MainActivity : ComponentActivity() {
    private lateinit var navController: NavHostController
    companion object{
        lateinit var timerStoppedReceiver : TimerStoppedReceiver
        private const val OVERLAY_PERMISSION_REQUEST_CODE = 1001
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

        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        prefs.edit().putBoolean("stop_accessibility_service", false).apply()
        WindowCompat.setDecorFitsSystemWindows(window, false)

        checkOverlayPermission()
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
            val prefs = PreferenceManager.getDefaultSharedPreferences(this)
            prefs.edit().putBoolean("stop_accessibility_service", true).apply()
        }catch (e : IllegalArgumentException){
            Log.e("MainActivity", "Receiver not registered", e)
        }
    }

    private fun checkOverlayPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                val intent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:$packageName")
                )
                startActivityForResult(intent, OVERLAY_PERMISSION_REQUEST_CODE)
            }
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == OVERLAY_PERMISSION_REQUEST_CODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!Settings.canDrawOverlays(this)) {
                    // 권한이 거부된 경우 처리
                    Toast.makeText(this, "오버레이 권한이 필요합니다", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
