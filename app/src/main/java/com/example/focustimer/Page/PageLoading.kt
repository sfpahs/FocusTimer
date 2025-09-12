package com.example.focustimer.Page

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.focustimer.R
import com.example.shared.AnimationPart


@Composable
fun LoadingScreen(isLoading: Boolean) {

     // 상태 변화 감지를 위해 강제 재구성
        val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(resId = R.raw.loding_fire))
        var currentPart by remember { mutableStateOf< AnimationPart>(AnimationPart.Start) }
        var isPlaying by remember { mutableStateOf(true) }
        var restartKey by remember { mutableIntStateOf(0) }
//todo 이거 돌아가는것 만들것
        val progress by animateLottieCompositionAsState(
            composition = composition,
            clipSpec = currentPart.clipSpec,
            iterations = currentPart.iterations,
            isPlaying = isPlaying,
            restartOnPlay = false,


            )

        // 로딩 상태 변화 감지
        LaunchedEffect(isLoading) {
            if (!isLoading) {
                currentPart = AnimationPart.End
                isPlaying = false
                restartKey++

            } else {
                currentPart = AnimationPart.Start
                isPlaying = true
                restartKey++
            }
        }
        if(isPlaying){
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                LottieAnimation(
                    composition = composition,
                    progress = { progress },
                    modifier = Modifier
                        .size(200.dp)
                )
            }
        }

}


