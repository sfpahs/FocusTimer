package com.example.focustimer.test

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.rememberNavController
import androidx.compose.runtime.CompositionLocalProvider
import com.example.focustimer.LocalNavController
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.WeekFields
import java.util.Locale

enum class ScheduleViewType {
    DAILY, WEEKLY, MONTHLY
}

@Preview
@Composable
fun ScheduleContainerPagePreview() {
    // Preview에서 NavController 제공
    val navController = rememberNavController()
    CompositionLocalProvider(LocalNavController provides navController) {
        ScheduleContainerPage()
    }
}

@Composable
fun ScheduleContainerPage() {
    val navController = LocalNavController.current
    var hasTakenSurvey by remember { mutableStateOf(false) }
    var selectedViewType by remember { mutableStateOf(ScheduleViewType.WEEKLY) }

    // 날짜 관련 상태
    var currentDate by remember { mutableStateOf(LocalDate.now()) }
    var showDatePicker by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // 테스트를 완료하지 않았을 경우 테스트 버튼 표시
        if (!hasTakenSurvey) {
            Button(
                onClick = { navController.navigate("survey") },
                modifier = Modifier
                    .fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF90CAF9)
                )
            ) {
                Text(
                    text = "성향 테스트 하러가기",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }

        // 연도 및 월 선택 UI
        DateNavigationBar(
            currentDate = currentDate,
            onYearMonthClick = { showDatePicker = true },
            onPreviousWeek = { currentDate = currentDate.minusWeeks(1) },
            onNextWeek = { currentDate = currentDate.plusWeeks(1) }
        )

        // 스케줄 뷰 타입 선택 탭 (월간/주간/일간)
        TabRow(
            selectedTabIndex = selectedViewType.ordinal,
            modifier = Modifier.fillMaxWidth()

        ) {
            Tab(
                selected = selectedViewType == ScheduleViewType.DAILY,
                onClick = { selectedViewType = ScheduleViewType.DAILY },
                text = { Text("일간") }
            )
            Tab(
                selected = selectedViewType == ScheduleViewType.WEEKLY,
                onClick = { selectedViewType = ScheduleViewType.WEEKLY },
                text = { Text("주간") }
            )
            Tab(
                selected = selectedViewType == ScheduleViewType.MONTHLY,
                onClick = { selectedViewType = ScheduleViewType.MONTHLY },
                text = { Text("월간") }
            )
        }

        // 선택된 스케줄 뷰 표시
        Box(modifier = Modifier.weight(1f)) {
            when (selectedViewType) {
                ScheduleViewType.DAILY -> {
                    // 일간 스케줄 페이지 (구현 예정)
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("일간 스케줄 - 구현 예정")
                    }
                }
                ScheduleViewType.WEEKLY -> {
                    // 주간 스케줄 페이지 (이미 구현됨)
                    // currentDate를 기준으로 주의 시작일을 계산하여 전달
                    val weekStart = currentDate.with(WeekFields.of(Locale.getDefault()).dayOfWeek(), 1)
                    WeeklyScheduleSampleScreen(startDate = weekStart)
                }
                ScheduleViewType.MONTHLY -> {
                    // 월간 스케줄 페이지 (구현 예정)
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("월간 스케줄 - 구현 예정")
                    }
                }
            }
        }
    }

    // 월/연도 선택 다이얼로그
    if (showDatePicker) {
        MonthYearPickerDialog(
            initialDate = currentDate,
            onDateSelected = {
                currentDate = it
                showDatePicker = false
            },
            onDismiss = { showDatePicker = false }
        )
    }
}

@Composable
fun DateNavigationBar(
    currentDate: LocalDate,
    onYearMonthClick: () -> Unit,
    onPreviousWeek: () -> Unit,
    onNextWeek: () -> Unit
) {
    val monthYearFormatter = DateTimeFormatter.ofPattern("yyyy년 MM월")

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp, horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onPreviousWeek) {
            Icon(Icons.Default.KeyboardArrowLeft, contentDescription = "이전 주")
        }

        Button(
            onClick = onYearMonthClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        ) {
            Text(
                text = currentDate.format(monthYearFormatter),
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }

        IconButton(onClick = onNextWeek) {
            Icon(Icons.Default.KeyboardArrowRight, contentDescription = "다음 주")
        }
    }
}

@Composable
fun MonthYearPickerDialog(
    initialDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedYear by remember { mutableStateOf(initialDate.year) }
    var selectedMonth by remember { mutableStateOf(initialDate.monthValue) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("날짜 선택") },
        text = {
            Column(
                modifier = Modifier.padding(8.dp)
            ) {
                // 연도 선택
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { selectedYear-- }) {
                        Icon(Icons.Default.KeyboardArrowLeft, "이전 연도")
                    }

                    Text(
                        text = "${selectedYear}년",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    IconButton(onClick = { selectedYear++ }) {
                        Icon(Icons.Default.KeyboardArrowRight, "다음 연도")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 월 선택 그리드
                val months = listOf("1월", "2월", "3월", "4월", "5월", "6월", "7월", "8월", "9월", "10월", "11월", "12월")
                Column {
                    for (i in 0..3) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            for (j in 0..2) {
                                val monthIndex = i * 3 + j
                                val monthNumber = monthIndex + 1

                                Button(
                                    onClick = { selectedMonth = monthNumber },
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(4.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (selectedMonth == monthNumber)
                                            MaterialTheme.colorScheme.primary
                                        else
                                            MaterialTheme.colorScheme.surfaceVariant,
                                        contentColor = if (selectedMonth == monthNumber)
                                            MaterialTheme.colorScheme.onPrimary
                                        else
                                            MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                ) {
                                    Text(
                                        text = months[monthIndex],
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val newDate = LocalDate.of(selectedYear, selectedMonth, 1)
                    onDateSelected(newDate)
                }
            ) {
                Text("확인")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("취소")
            }
        }
    )
}

// WeeklyScheduleSampleScreen 수정 - startDate를 파라미터로 받도록 변경
@Composable
fun WeeklyScheduleSampleScreen(startDate: LocalDate = LocalDate.now().with(java.time.DayOfWeek.MONDAY)) {
    val sampleEvents = listOf(
        Event(1, "아침 운동", startDate, 6, 30, 60),
        Event(2, "회의", startDate, 10, 0, 30),
        Event(3, "점심 식사", startDate, 12, 0, 60),
        Event(4, "스터디", startDate.plusDays(1), 20, 0, 90),
        Event(5, "저녁 약속", startDate.plusDays(3), 18, 30, 120)
    )

    Box(modifier = Modifier
        .background(Color.White)) {
        WeeklySchedule(
            startDate = startDate,
            events = sampleEvents
        )
    }
}
