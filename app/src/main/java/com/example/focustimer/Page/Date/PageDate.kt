package com.example.focustimer.Page.Date

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shared.model.CronoTimeViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar

enum class ScheduleViewType {
    DAILY, WEEKLY
}


@Composable
fun ScheduleContainerPage() {
    val cronoViewmodel : CronoTimeViewModel by lazy{ CronoTimeViewModel.getInstance()}

    var selectedViewType by remember { mutableStateOf(ScheduleViewType.WEEKLY) }
    val cronoType by cronoViewmodel.surveyData.collectAsState()
    cronoViewmodel.loadSurveyData()
    // 날짜 관련 상태
    var currentDate by remember { mutableStateOf(LocalDate.now()) }
    var showDatePicker by remember { mutableStateOf(false) }
    val name = cronoType.name
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {


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

        }

        // 선택된 스케줄 뷰 표시
        Box(modifier = Modifier.weight(1f)) {
            when (selectedViewType) {
                ScheduleViewType.DAILY -> {
                    // 일간 스케줄 페이지 (구현 예정)
                    Box(
                        modifier = Modifier.fillMaxSize(),
                    ) {
                        DailyScheduleScreen(startDate = currentDate)
                    }
                }
                ScheduleViewType.WEEKLY -> {
                    // 주간 스케줄 페이지 (이미 구현됨)
                    // currentDate를 기준으로 주의 시작일을 계산하여 전달
                    val weekStart = currentDate
                    WeeklySchedule(startDate = weekStart)
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
    val weekOfMonth = getWeekOfMonth(currentDate)

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
                text = "${currentDate.format(monthYearFormatter)}  ${weekOfMonth}번째 주",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }

        IconButton(onClick = onNextWeek) {
            Icon(Icons.Default.KeyboardArrowRight, contentDescription = "다음 주")
        }
    }
}

fun getWeekOfMonth(date: LocalDate): Int {
    val calendar = Calendar.getInstance()
    calendar.set(date.year, date.monthValue - 1, date.dayOfMonth)
    return calendar.get(Calendar.WEEK_OF_MONTH)
}

