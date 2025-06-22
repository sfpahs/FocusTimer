package com.example.focustimer.Page.main

import android.content.Context
import android.util.Log
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
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
import com.example.focustimer.Page.Date.ScheduleViewType
import com.example.focustimer.Page.LoadingScreen
import com.example.focustimer.R
import com.example.focustimer.utils.AppRoute
import com.example.shared.Myfirebase.loadUserName
import com.example.shared.Myfirebase.logOut
import com.example.shared.model.CronoTimeViewModel
import com.example.shared.model.Timer
import com.example.shared.model.TimerOptions
import com.example.shared.model.MySubject
import com.example.shared.model.TimerViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalTime

// 크로노타임 색상 정의
val productiveTime = 9..11 // 생산적 시간대 예시 (오전 9시~11시 59분)
val creativeTime = 14..16  // 창의적 시간대 예시 (오후 2시~4시 59분)
val workoutTime = 7..8     // 운동 시간대 예시 (오전 7시~8시 59분)
val sleepTime = 0..6       // 수면 시간대 예시 (오전 0시~6시 59분)

val productiveColor = Color(0xffc4c6fe) // 생산적
val creativeColor = Color(0xffffedcc)   // 창의적
val workoutColor = Color(0xffccffcc)    // 운동
val sleepColor = Color(0xffe6cce6)      // 수면


@Preview
@Composable
fun MainPage() {
    val viewModel : TimerViewModel by lazy { TimerViewModel.getInstance() }
    val cronoViewmodel : CronoTimeViewModel by lazy{ CronoTimeViewModel.getInstance()}
    var selectedViewType by remember { mutableStateOf(ScheduleViewType.WEEKLY) }
    val cronoType by cronoViewmodel.surveyData.collectAsState()

    cronoViewmodel.loadSurveyData()

    val context = LocalContext.current
    val navHostController = LocalNavController.current
    val user = FirebaseAuth.getInstance().currentUser

    val timerSettings by viewModel.subjects.collectAsState()
    var isLoading by remember { mutableStateOf(true) }
    var userName by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    // 현재 시간 및 배경색 상태
    var currentHour by remember { mutableStateOf(LocalTime.now().hour) }
    //var currentHour by remember { mutableStateOf(6) }
    val chronoTimeName = getChronoTimeName(currentHour)
    val backgroundColor by animateColorAsState(
        getChronoColor(currentHour),
        label = "background color"
    )

    // 1분마다 시간 갱신
    LaunchedEffect(Unit) {
        while (true) {
            delay(60 * 1000)
            currentHour = LocalTime.now().hour
        }
    }

    user?.let {
        scope.launch {
            viewModel.loadSubjects()
            loadUserName{ name -> userName = name ?: ""}
            delay(1000)
            isLoading = false
        }
    }

    // 배경색 적용
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = backgroundColor)
    ) {
        SelectTimer(
            MySubjects = timerSettings,
            modifier = Modifier.fillMaxSize()
        )
        MainAppBar(
            context = context,
            userName = userName,
            chronoTimeName = chronoTimeName,
            navHostController = navHostController
        )
        LoadingScreen(isLoading = isLoading)
    }
}

fun getChronoTimeName(currentHour: Int): String {
    return when (currentHour) {
        in productiveTime -> "생산적 시간"
        in creativeTime -> "창의적 시간"
        in workoutTime -> "운동 시간"
        in sleepTime -> "수면 시간"
        else -> "일반 시간"
    }
}

// 현재 시간에 따라 색상 반환
@Composable
fun getChronoColor(currentHour: Int): Color {
    return when (currentHour) {
        in productiveTime -> productiveColor
        in creativeTime -> creativeColor
        in workoutTime -> workoutColor
        in sleepTime -> sleepColor
        else -> Color.White // 기본값
    }
}

@Composable
fun SelectTimer(
    MySubjects: List<MySubject>,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .padding(horizontal = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(10.dp),
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center
        ) {
            items(MySubjects.size) { index ->
                val setting = MySubjects[index]
                TimerBox(
                    modifier = Modifier.aspectRatio(1f),
                    MySubject = setting
                )
            }
            if(MySubjects.size < 10){
                item{
                    AddBox(modifier = modifier)
                }
            }
        }
    }
}

@Composable
fun MainAppBar(context : Context, userName : String,chronoTimeName : String,  navHostController: NavHostController) {
    Row (
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Box {
            if(!userName.equals("")) Text(text = userName + " 님", color = colorResource(R.color.myBlack))
        }
        Text(
            text = chronoTimeName,
            fontSize = 24.sp,
            modifier = Modifier.padding(16.dp)
        )
        IconButton(
            onClick = { navHostController.navigate("explanation") }
        ) {
            Icon(
                Icons.Default.Info,
                contentDescription = "도움말",
                tint = colorResource(R.color.myBlack)
            )
        }
        Log.i("main", "MainPage: ${userName}")
        Button(
            onClick = {
                logOut(context = context)
                navHostController.navigate("signin")
            }
        ) { Text(text = "Log out") }
    }
}

@Composable
fun TimerBox(modifier: Modifier, MySubject: MySubject) {
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
                color = Color(MySubject.backgroundColor),
                shape = RoundedCornerShape(20.dp)
            )
            .clickable {
                if (MySubject.selectedTimer != -1) {
                    timerViewModel.setOption(TimerOptions.list.get(MySubject.selectedTimer))
                } else {
                    timerViewModel.setOption(TimerOptions.list.get(MySubject.recomendTimer))
                }
                timerViewModel.setTimer(newData =
                Timer(MySubject = MySubject, activeTimer = 1, time = 0)
                )
                navController.navigate("timer")
            }
    ) {
        Icon(
            imageVector = Icons.Default.Settings,
            contentDescription = "Edit Timer",
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(8.dp)
                .size(24.dp)
                .clickable(
                    indication = rememberRipple(bounded = false),
                    interactionSource = remember { MutableInteractionSource() }
                ) {
                    timerViewModel.setTimer(newData = Timer(MySubject = MySubject))
                    navController.navigate(AppRoute.EDIT.route)
                },
            tint = colorResource(R.color.myBlack)
        )
        Column (
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val timerOption =
                if(MySubject.selectedTimer != -1)
                    TimerOptions.list.get(MySubject.selectedTimer)
                else TimerOptions.list.get(MySubject.recomendTimer)
            Text(text = MySubject.name, fontSize = 25.sp, color = colorResource(R.color.myBlack))
            Text(text = timerOption.name, color = colorResource(R.color.myBlack))
            Text(text = "work: ${timerOption.workTime/60}분", color = colorResource(R.color.myBlack))
            Text(text = "rest: ${timerOption.restTime/60}분", color = colorResource(R.color.myBlack))
        }
    }
}

@Composable
fun AddBox(modifier: Modifier) {
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
                color = Color(0xFFE0E0E0), // 연회색, 원하는 색으로 바꿔도 됨
                shape = RoundedCornerShape(20.dp)
            )
            .clickable {
                timerViewModel.setTimer(newData = Timer())
                navController.navigate(AppRoute.EDIT.route) // 새 타이머 추가시 에디트 화면으로 이동
            }
    ) {
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add Timer",
                modifier = Modifier.size(40.dp),
                tint = Color.Black
            )
            Text(
                text = "새 타이머 추가",
                fontSize = 20.sp,
                color = Color.Black
            )
        }
    }
}