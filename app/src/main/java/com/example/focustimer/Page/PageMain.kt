package com.example.focustimer.Page

import android.content.Context
import android.util.Log
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.focustimer.LocalNavController
import com.example.focustimer.R
import com.example.shared.Myfirebase.loadUserName
import com.example.shared.Myfirebase.logOut
import com.example.shared.model.Timer
import com.example.shared.model.TimerOptions
import com.example.shared.model.subject
import com.example.shared.model.TimerViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Preview
@Composable
fun MainPage() {
    val viewModel : TimerViewModel by lazy { TimerViewModel.getInstance() }
    val context = LocalContext.current
    val navHostController = LocalNavController.current
    val user = FirebaseAuth.getInstance().currentUser

    val timerSettings by viewModel.subjects.collectAsState()
    var isLoading by remember { mutableStateOf(true) }
    var userName by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    //firebase 정보받기
    user?.let {
        scope.launch {
            viewModel.loadSubjects()
            loadUserName{ name -> userName = name ?: ""}
            delay(1000)
            isLoading = false
        }

    }

    SelectTimer(subjects = timerSettings)

    MainAppBar(
        context = context,
        userName = userName,
        navHostController = navHostController
    )

    LoadingScreen(isLoading = isLoading)
}
@Composable
fun SelectTimer(subjects: List<subject>){
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 6.dp)
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {


        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(10.dp),
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center

        ) {
            items(subjects.size) { index ->
                val setting = subjects[index]
                TimerBox(
                    modifier = Modifier.aspectRatio(1f),
                    subject = setting
                )
            }
            //todo나중에 편집아이콘 만들고 넣기 - 최대개수랑 편집아이콘이랑 합쳐야함
//            item {
//                addBox(
//                    modifier = Modifier.aspectRatio(1f),
//                    color = Color.DarkGray,
//                    text = "+",
//                    category = timerSettings.size + 1,
//                    navHostController = navHostController
//                )
//            }
        }

    }


}

@Composable
fun MainAppBar(context : Context, userName : String, navHostController: NavHostController){
    Row (modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween){
        Box{
            if(!userName.equals("")) Text(text = userName + " 님", color = colorResource(R.color.myBlack))

        }
        // 여기에 도움말 아이콘 추가
        IconButton(
            onClick = {
                navHostController.navigate("explanation")
            }
        ) {
            Icon(
                Icons.Default.Info,
                contentDescription = "도움말",
                tint = colorResource(R.color.myBlack)
            )
        }

        Log.i("main", "MainPage: ${userName}")
        Button(
            onClick = { logOut(context = context)
                navHostController.navigate("signin")}
        ) { Text(text = "Log out")}
    }

}

@Composable
fun TimerBox(modifier: Modifier, subject: subject){
    val navController = LocalNavController.current
    val timerViewModel : TimerViewModel by lazy { TimerViewModel.getInstance() }
    Box(
        modifier = modifier
            .fillMaxHeight(1f)
            .padding(10.dp)
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(20.dp),
                spotColor = Color.Black.copy(alpha = 0.25f)
            )
            .background(
                color = Color(subject.backgroundColor),
                shape = RoundedCornerShape(20.dp)
            )
            .clickable {
                //유저가 저장한 것이 있는지
                if (subject.selectedTimer != -1) {
                    timerViewModel.setTimer(
                        newData = Timer(subject = subject, activeTimer = 1, time = 0),
                    )
                    timerViewModel.setOption(TimerOptions.list.get(subject.selectedTimer))
                }
                //없을 시에 추천 타이머로 실행
                else {
                    timerViewModel.setTimer(
                        newData = Timer(subject = subject, activeTimer = 1, time = 0)
                    )
                    timerViewModel.setOption(TimerOptions.list.get(subject.recomendTimer))
                }


                navController.navigate("timer")
            },
    ){
        Icon(
            imageVector = Icons.Default.Settings,
            contentDescription = "Edit Timer",
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(8.dp)
                .size(24.dp)
                .clickable(
                    // 아이콘 클릭 시 이벤트 전파 방지
                    indication = rememberRipple(bounded = false),
                    interactionSource = remember { MutableInteractionSource() }
                ) {
                    // edit 화면으로 이동하는 로직
                    timerViewModel.setTimer(newData = Timer(subject = subject))
                    navController.navigate("edit")

                },
            tint = colorResource(R.color.myBlack)
        )

        Column (
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            val timerOption =
                if(subject.selectedTimer != -1)
                    TimerOptions.list.get(subject.selectedTimer)
                else TimerOptions.list.get(subject.recomendTimer)

            Text(text = subject.name,  fontSize = 25.sp, color = colorResource(R.color.myBlack))
            Text(text = timerOption.name, color = colorResource(R.color.myBlack))
            Text(text = "work: ${timerOption.workTime/60}분", color = colorResource(R.color.myBlack))
            Text(text = "rest: ${timerOption.restTime/60}분", color = colorResource(R.color.myBlack))
        }
    }
}
@Composable
fun AddBox(modifier: Modifier, color : Color, text : String, category : Int, navHostController: NavHostController){
    Column (modifier = modifier
        .fillMaxHeight(1f)
        .padding(10.dp)
        .background(
            color = color,
            shape = RoundedCornerShape(20.dp)
        )
        .clickable {
            //todo 편집창으로 연결할 것
            navHostController.navigate("timer${category}")
        },
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){ Text(text = text, color = Color.White, fontSize = 25.sp)}
}

