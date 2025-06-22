package com.example.focustimer.Page.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.example.focustimer.LocalNavController
import com.example.focustimer.R
import kotlinx.coroutines.launch
@Preview
@Composable
fun ExplanationPager(
) {
    val navHostController = LocalNavController.current
    val coroutineScope = rememberCoroutineScope()
    val pages = listOf(
        ExplanationPage(R.drawable.img1, "타이머 설명", "작업 시간과 휴식 시간을 설정하여 효율적인 작업 사이클을 만들 수 있습니다.\n" +
                "시간이 끝나면 자동으로 다음 단계로 넘어가며, 알림으로 알려드립니다.\n" +
                "집중력을 높이고 번아웃을 방지하는 최적의 작업 패턴을 찾아보세요."),
        ExplanationPage(R.drawable.img1, "타이틀2", "설명2"),
        ExplanationPage(R.drawable.img1, "타이틀3", "설명3"),
        ExplanationPage(R.drawable.img1, "타이틀4", "설명4")
    )
    val pagerState = rememberPagerState(pageCount = { pages.size })

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                //todo 테스트용 추후에 지울것
                .background(Color.Gray)
                .padding(bottom = 80.dp), // 인디케이터 공간 확보
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f)
                    .padding(32.dp)
            ) { page ->
                Box(modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White)

                ){
                    Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Text(text = pages[page].title, fontSize = 24.sp)
                        Box(modifier = Modifier){
                            Image(
                                painter = painterResource(id = pages[page].imageRes),
                                contentDescription = null,
                                modifier = Modifier.fillMaxHeight(0.7f)
                            )
                        }
                        Text(text = pages[page].description, fontSize = 16.sp)

                    }
                }

            }
        }

        // 좌/우 화살표
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    if (pagerState.currentPage > 0) {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage - 1)
                        }
                    }
                },
                enabled = pagerState.currentPage > 0
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "이전")
            }
            Spacer(modifier = Modifier.width(12.dp))
            IconButton(
                onClick = {
                    if (pagerState.currentPage < pages.size - 1) {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    }
                },
                enabled = pagerState.currentPage < pages.size - 1
            ) {
                Icon(Icons.Default.ArrowForward, contentDescription = "다음")
            }
        }

        // 인디케이터
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(pages.size) { index ->
                val color = if (pagerState.currentPage == index) Color.DarkGray else Color.LightGray
                Box(
                    modifier = Modifier
                        .padding(4.dp)
                        .size(14.dp)
                        .background(color, CircleShape)
                )
            }
        }
        //버튼
       if (pagerState.currentPage == pages.lastIndex) {

            Button(
                colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.myButtonColor)),
                onClick =
                {
                    navHostController.navigate("main")
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .width(200.dp)
                    .height(110.dp)
                    .padding(bottom = 60.dp)
            ) {
                Text("메인화면으로 가기")
            }
        }
    }
}

// 데이터 클래스 예시
data class ExplanationPage(
    val imageRes: Int, // painterResource로 사용할 이미지 리소스 ID
    val title: String,
    val description: String
    // val lottieRes: Int // Lottie 사용시
)