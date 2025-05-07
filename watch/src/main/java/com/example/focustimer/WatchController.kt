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
import com.example.shared.watchModel.WatchViewModel
import com.google.android.gms.wearable.MessageClient
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import com.google.android.gms.tasks.Task
import com.google.android.gms.wearable.CapabilityClient
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resumeWithException

@Preview(widthDp = 200, heightDp = 200)
@Composable
fun WatchTimerControl(navController: NavHostController = rememberNavController()) {
    val context = LocalContext.current
    var isConnected by remember { mutableStateOf(false) }

    val viewModel by lazy { WatchViewModel.getInstance() }
    val timerSetting by viewModel.setting.collectAsState()
    val activeTimer by viewModel.activeTimer.collectAsState()
    val time by viewModel.time.collectAsState()

    // 데이터 레이어 클라이언트 초기화
    val messageClient = Wearable.getMessageClient(context)
    val nodeClient = Wearable.getNodeClient(context)

    val currentTime : Int = time
    val currentMaxTime =
        if(activeTimer.equals(1)) timerSetting.workTime
        else timerSetting.restTime

    // 컴포저블이 처음 생성될 때 연결 상태 확인
    LaunchedEffect(Unit) {
        checkConnectionStatus(nodeClient) { connected ->
            isConnected = connected
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if(timerSetting.name.isNotEmpty()){
            Text(
                text = "${timerSetting.name}:${currentMaxTime/60}",
                fontSize = 16.sp,
                color = if (isConnected) Color.White else Color.Gray
            )
            Text(
                text = "$currentTime",
                fontSize = 16.sp,
                color = if (isConnected) Color.White else Color.Gray
            )
        }
        else{
            Text(
                text = if (isConnected) "연결됨" else "연결 중...",
                fontSize = 16.sp,
                color = if (isConnected) Color.White else Color.Gray
            )
        }

        Row {
            Button(
                onClick = {
                    if (isConnected) {
                        sendMessageToPhone(messageClient, "/timer_action", "switch")
                    }
                },
                enabled = isConnected
            ) {
                Text("전환")
            }

            Spacer(modifier = Modifier.fillMaxWidth(0.4f))

            Button(
                onClick = {
                    if (isConnected) {
                        sendMessageToPhone(messageClient, "/timer_action", "stop")
                    }
                },
                enabled = isConnected
            ) {
                Text("종료")
            }
        }
    }
}

// 연결 상태를 확인하는 함수
private fun checkConnectionStatus(nodeClient: com.google.android.gms.wearable.NodeClient, callback: (Boolean) -> Unit) {
    CoroutineScope(Dispatchers.IO).launch {
        try {
            val nodes = nodeClient.connectedNodes.await()
            val connected = nodes.isNotEmpty()
            withContext(Dispatchers.Main) {
                callback(connected)
            }
        } catch (e: Exception) {
            Log.e("WatchApp", "연결 상태 확인 실패: ${e.message}")
            withContext(Dispatchers.Main) {
                callback(false)
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
