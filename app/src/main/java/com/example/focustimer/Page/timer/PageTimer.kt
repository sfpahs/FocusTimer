package com.example.focustimer.Page.timer

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieAnimatable
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.focustimer.LocalNavController
import com.example.focustimer.R
import com.example.focustimer.TimerService
import com.example.shared.model.TimerViewModel

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun DualStopwatchApp(
) {

    val navController = LocalNavController.current
    val context = LocalContext.current

    var isChecked by remember { mutableStateOf(true) }
    var showRecordDialog by remember { mutableStateOf(false) }

    val viewModel : TimerViewModel by lazy { TimerViewModel.getInstance() }
    val subject by viewModel.currentMySubject.collectAsState()
    val mul by viewModel.mul.collectAsState()
    val stopwatchRunning by viewModel.stopwatchRunning.collectAsState()
    val isStopped by viewModel.isStopped.collectAsState()


    fun startStopwatch() {

        if (!stopwatchRunning && isStopped) {
            val intent = Intent(context, TimerService::class.java).apply {
                action = TimerService.ACTION_START
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }

            //context.bindService(intent, serviceConnection!!, Context.BIND_AUTO_CREATE)

            viewModel.startTimer()
        }
    }

    fun switchStopwatch() {
        if (stopwatchRunning) {
            val intent = Intent(context, TimerService::class.java).apply {
                action = TimerService.ACTION_SWITCH
            }
            context.startService(intent)

        }
    }

    fun stopStopwatch() {
        val intent = Intent(context, TimerService::class.java).apply {
            action = TimerService.ACTION_STOP
        }
        context.startService(intent)

        viewModel.stopTimer()

        navController.navigate("main")
    }
    BackHandler {
        // 종료 버튼과 동일한 동작 실행
        if (!stopwatchRunning && isStopped) {
            navController.navigate("main")
        } else {
            // 종료 다이얼로그를 띄우는 로직
            showRecordDialog = true
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        StopwatchUI(
            modifier = Modifier.size(300.dp),
            viewModel = viewModel
        )

        Spacer(modifier = Modifier.height(32.dp))
        Button(onClick = {
            navController.navigate("changeOption")
        }) {
            Text("타이머 바꾸기")
        }
        Row(horizontalArrangement = Arrangement.SpaceEvenly) {
            Button(onClick = {
                if (!stopwatchRunning && isStopped) {
                    startStopwatch()

                } else if (stopwatchRunning) {
                    switchStopwatch()
                }
            }) {
                Text(if (!stopwatchRunning && isStopped) "시작" else "체인지")
            }

            Spacer(modifier = Modifier.width(16.dp))

            Button(onClick = {
                if (!stopwatchRunning && isStopped) navController.navigate("main")
                else showRecordDialog = true
            }) {
                Text("종료")
            }
        }
        if (showRecordDialog) {
            RecordDialog(
                onDismiss = { showRecordDialog = false },
                onSave = { recordText ->
                    // 1. 기록 저장 (예: ViewModel에 저장)
                    viewModel.description = (recordText)
                    showRecordDialog = false
                    // 2. 기존 저장 로직 이어서 실행
                    stopStopwatch() // 기존 종료/저장 함수 호출
                }
            )
        }
    }

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = subject.name, fontSize = 20.sp)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "숫자 표시", fontSize = 20.sp)
                Switch(
                    modifier = Modifier,
                    checked = isChecked,
                    onCheckedChange = { viewModel.setTextVisible(it)},
                    colors = SwitchDefaults.colors(
                        checkedTrackColor = Color.Red
                    )
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "분단위", fontSize = 20.sp)
                Switch(
                    modifier = Modifier,
                    checked = (mul == 60),
                    onCheckedChange = {
                        viewModel.updateMul()

                    },
                    colors = SwitchDefaults.colors(
                        checkedTrackColor = Color.Red
                    )
                )
            }
        }
    }
}

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun StopwatchUI(
    modifier: Modifier = Modifier,
    viewModel: TimerViewModel
) {
    val activeTimer by viewModel.activeTimer.collectAsState()
    val time by viewModel.time.collectAsState()
    val timerOption by viewModel.timerOption.collectAsState()
    val maxTime =if( activeTimer == 1 ) timerOption.workTime else timerOption.restTime
    val isChecked by viewModel.isTextVisible.collectAsState()

    Box(contentAlignment = Alignment.Center, modifier = modifier) {

        var sweepAngle = if (maxTime > 0) {
            Math.min(360f,(360f/3 * (time*3 / maxTime)))
        } else {
            0f
        }

        val step = sweepAngle/120
        val animatedStep by animateFloatAsState(targetValue = step)

// fireSize도 animatedStep을 사용
        val fireSize = 1f + 0.8f * animatedStep

        Canvas(modifier = modifier) {
            val strokeWidth = 50f
            val radius = size.minDimension / 2 - strokeWidth / 5

            // 배경 원 그리기 (전체 시간)
            drawCircle(
                color = Color.LightGray,
                radius = radius,
                style = Stroke(width = strokeWidth)
            )

            // 진행 원 그리기 (경과 시간)


            drawArc(
                color = if (activeTimer == 1){
                    when(step){
                        1f-> Color.Yellow
                        2f->Color.Red
                        else->Color.Black
                    }
                }
                else Color.Black,
                startAngle = -90f,
                sweepAngle = sweepAngle,
                useCenter = false,
                style = Stroke(width = strokeWidth)
            )
        }

        Column (
            modifier = Modifier
                .size(180.dp) // 최대 크기 기준으로 고정
                .graphicsLayer(
                    scaleX = fireSize,
                    scaleY = fireSize
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        )
        {
            Spacer(modifier = Modifier.height(24.dp))
            SmoothLottieAnimation(resId = R.raw.timer_fire)
        }

        if (isChecked) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "${time / 60}:${(time % 60).toString().padStart(2, '0')}",
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (activeTimer == 1) Color.Black else Color.Gray,
                )

                Text(
                    text = "/ ${maxTime / 60}:${(maxTime % 60).toString().padStart(2, '0')}",
                    fontSize = 20.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
fun SmoothLottieAnimation(
    modifier: Modifier = Modifier,
    resId: Int,

) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(resId))
    val animatable = rememberLottieAnimatable()

    // 애니메이션이 composition이 준비되면 자동으로 반복 재생
    LaunchedEffect(composition) {
        if (composition != null) {
            animatable.animate(
                composition = composition,
                iterations = LottieConstants.IterateForever
            )
        }
    }

    LottieAnimation(
        composition = composition,
        progress = { animatable.progress },
        modifier = modifier.size(100.dp)
    )
}

@Composable
fun RecordDialog(
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    var inputText by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("기록 남기기") },
        text = {
            Column {
                Text("오늘의 기록을 입력하세요.")
                TextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    placeholder = { Text("기록 입력") }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(inputText) // 저장 및 기존 로직 실행
                }
            ) {
                Text("저장")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("취소")
            }
        }
    )
}


class TimerStoppedReceiver(callback : () -> Unit = {}) : BroadcastReceiver() {
    val callBack = callback

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "com.example.pre_capstone.TIMER_STOPPED") {
            Log.d("TAG", "onReceive: 리시버받음")
            callBack()
        }
    }
}