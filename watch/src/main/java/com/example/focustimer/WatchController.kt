package com.example.focustimer

import android.util.Log
import androidx.annotation.ColorRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.Text
import com.airbnb.lottie.compose.*
import com.example.shared.model.TimerOptions
import com.example.shared.model.TimerViewModel
import com.google.android.gms.tasks.Task
import com.google.android.gms.wearable.MessageClient
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@Preview(widthDp = 200, heightDp = 200)
@Composable
fun WatchTimerControl(navController: NavHostController = rememberNavController()) {
    val context = LocalContext.current
    var isConnected by remember { mutableStateOf(false) }

    val viewModel by lazy { TimerViewModel.getInstance() }
    val timerSetting by viewModel.currentMySubject.collectAsState()
    val activeTimer by viewModel.activeTimer.collectAsState()
    val time by viewModel.time.collectAsState()
    val statusText = if(activeTimer == 1)"작업중" else "쉬는중"
    // 데이터 레이어 클라이언트 초기화
    val messageClient = Wearable.getMessageClient(context)
    val nodeClient = Wearable.getNodeClient(context)

    val currentTime: Int = time
    val timerOption =
        if (timerSetting.selectedTimer != -1) TimerOptions.list.get(timerSetting.selectedTimer)
        else if (timerSetting.recomendTimer != -1)
            TimerOptions.list.get(timerSetting.recomendTimer)
        else TimerOptions.list.get(1)

    val maxTime = if (activeTimer == 1) timerOption.workTime else timerOption.restTime

    // 컴포저블이 처음 생성될 때 연결 상태 확인
    LaunchedEffect(Unit) {
        checkConnectionStatus(nodeClient) { connected ->
            isConnected = connected
        }
    }

    if (timerSetting.name.isNotEmpty()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box{
                TimerFireLottieAnimation()
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally, // Column 내부 텍스트를 중앙 정렬
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxSize()
                ){

                    Text(
                        text = "${timerSetting.name}-${statusText}",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isConnected) Color(timerSetting.backgroundColor) else Color.Gray
                    )
                    Text(
                        text = formatTime(currentTime) + "/${maxTime / 60}",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isConnected) Color.White else Color.Gray
                    )
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


        }
    } else {
        // 상태 메시지 및 Lottie 애니메이션 적용
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            if (isConnected) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "✅ 스마트폰과 연결 완료!",
                        fontSize = 16.sp,
                        color = Color(0xFF4CAF50)
                    )
                    Spacer(modifier = Modifier.height(8.dp)) // 간격 추가 (원하는 만큼 조절)
                    Text(
                        text = "타이머를 시작해보세요",
                        fontSize = 14.sp,
                        color = Color(0xFF4CAF50)
                    )
                }
            } else {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Lottie 로딩 애니메이션
                    ConnectingLottieAnimation(modifier = Modifier.size(80.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "스마트폰을 찾는 중...",
                        fontSize = 16.sp,
                        color = Color(0xFFFFC107)
                    )
                }
            }
        }
    }
}

// 연결 중 애니메이션 (Lottie)
@Composable
fun ConnectingLottieAnimation(modifier: Modifier = Modifier) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(com.example.shared.R.raw.loding_fire))
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever
    )
    LottieAnimation(
        composition = composition,
        progress = { progress },
        modifier = modifier
    )
}

@Composable
fun TimerFireLottieAnimation(modifier: Modifier = Modifier) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(com.example.shared.R.raw.timer_fire))
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever // 무한 반복
    )
    LottieAnimation(
        composition = composition,
        progress = { progress },
        modifier = modifier
    )
}

// 연결 상태를 확인하는 함수
private fun checkConnectionStatus(
    nodeClient: com.google.android.gms.wearable.NodeClient,
    callback: (Boolean) -> Unit
) {
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
fun formatTime(seconds: Int): String {
    val min = seconds / 60
    val sec = seconds % 60
    return if (min > 0) {
        "%d:%02d".format(min, sec)
    } else {
        "0:%02d".format(sec)
    }
}