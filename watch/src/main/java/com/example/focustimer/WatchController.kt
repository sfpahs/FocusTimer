package com.example.focustimer

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.Text
import com.example.focustimer.model.TimerViewModel
import com.google.android.gms.wearable.DataEvent
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.MessageClient
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.concurrent.timer
import kotlin.coroutines.resumeWithException
@Preview(widthDp = 200, heightDp = 200)
@Composable
fun WatchTimerControl(navController: NavHostController = rememberNavController()) {
    val context = LocalContext.current
    var isConnected by remember { mutableStateOf(false) }
    var currentTimerName by remember { mutableStateOf("") }
    var activeStopwatch by remember { mutableStateOf(1) }

    val viewModel: TimerViewModel by lazy { TimerViewModel.getInstance() }
    val timerInfo by viewModel.timerInfo.collectAsState()
    val activeTimer by viewModel.activateTimer.collectAsState()
    val timerSetting by viewModel.currentTimerSetting.collectAsState()
    // 데이터 레이어 클라이언트 초기화
    val messageClient = Wearable.getMessageClient(context)
    val currentTime : Int =
        if (activeTimer.equals(1) == true) timerInfo.workingMinute
        else timerInfo.restMinute
    val currentMaxTime =
        if(activeTimer.equals(1)) timerSetting.workTime
        else timerSetting.restTime

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally

    ) {
        if(currentTimerName.isNotEmpty()){

            Text(
                text = "$currentTimerName",
                fontSize = 16.sp,
                color = if (isConnected) Color.White else Color.Gray
            )

        }
        else{
            Text(
                text = "연결 중...",
                fontSize = 16.sp,
                color = if (isConnected) Color.White else Color.Gray
            )
            Text(
                text = "$currentTime / $currentMaxTime",
                fontSize = 16.sp,
                color = if (isConnected) Color.White else Color.Gray
            )

        }

        Row {

            Button(
                onClick = {
                    sendMessageToPhone(messageClient, "/timer_action", "switch")
                },
            ) {
                Text("전환")
            }

            Spacer(modifier = Modifier.fillMaxWidth(0.4f))

            Button(
                onClick = {
                    sendMessageToPhone(messageClient, "/timer_action", "stop")
                },
            ) {
                Text("종료")
            }
        }



    }

    // 데이터 수신 리스너 설정
    LaunchedEffect(Unit) {
        val dataClient = Wearable.getDataClient(context)

        dataClient.addListener { dataEvents ->
            dataEvents.forEach { event ->
                if (event.type == DataEvent.TYPE_CHANGED) {
                    val dataItem = event.dataItem
                    if (dataItem.uri.path == "/timer_status") {
                        val dataMap = DataMapItem.fromDataItem(dataItem).dataMap
                        isConnected = true
                        currentTimerName = dataMap.getString("timer_name", "")
                        activeStopwatch = dataMap.getInt("active_stopwatch", 1)
                    }
                }
            }
        }
    }
}

// 메시지 전송 함수
private fun sendMessageToPhone(messageClient: MessageClient, path: String, data: String) {
    CoroutineScope(Dispatchers.IO).launch {
        try {
            val nodes = Wearable.getNodeClient(messageClient.applicationContext).connectedNodes.await()
            nodes.forEach { node ->
                messageClient.sendMessage(node.id, path, data.toByteArray()).await()
            }
        } catch (e: Exception) {
            Log.e("WatchApp", "메시지 전송 실패: ${e.message}")
        }
    }
}

suspend fun <T> Task<T>.await(): T = suspendCancellableCoroutine { continuation ->
    addOnSuccessListener { result ->
        continuation.resume(result)
    }
    addOnFailureListener { exception ->
        continuation.resumeWithException(exception)
    }
}
