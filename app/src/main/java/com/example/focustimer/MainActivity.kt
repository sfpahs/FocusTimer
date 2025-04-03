package com.example.focustimer

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.focustimer.ui.theme.FocusTimerTheme

class MainActivity : ComponentActivity() {
    private lateinit var navController: NavHostController
    private lateinit var navigationReceiver: BroadcastReceiver
    companion object{
        lateinit var timerStoppedReceiver : TimerStoppedReceiver
    }

    @SuppressLint("NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        navigationReceiver = object : BroadcastReceiver() {
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
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    FocusTimerTheme {
        Greeting("Android")
    }
}