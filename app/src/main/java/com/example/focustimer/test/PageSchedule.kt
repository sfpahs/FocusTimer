package com.example.focustimer.test

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview

data class Event(
    val id: Int,
    val title: String,
    val date: LocalDate,
    val startHour: Int,        // 0~23
    val startMinute: Int,      // 0~59
    val durationMinutes: Int   // 분 단위
)
fun formatHour12h(hour: Int): String =
    when {
        hour == 0 -> "12am"
        hour in 1..11 -> "${hour}am"
        hour == 12 -> "12pm"
        else -> "${hour - 12}pm"
    }

@Composable
fun WeeklySchedule(
    startDate: LocalDate,
    //history
    //events: List<Event>
) {
    val today = LocalDate.now()
    val events = listOf(
        Event(1, "아침 운동", today, 6, 30, 60),
        Event(2, "회의", today, 10, 0, 30),
        Event(3, "점심 식사", today, 12, 0, 60),
        Event(4, "스터디", today.plusDays(1), 20, 0, 90),
        Event(5, "저녁 약속", today.plusDays(3), 18, 30, 120)
    )


    val hours = (0..23).toList()
    val days = (0..6).map { startDate.plusDays(it.toLong()) }
    val horizontalScrollState = rememberScrollState()
    val verticalScrollState = rememberScrollState()  // 수직 스크롤 상태 추가
    val dayHeight = 32
    val dayWidth = 55
    // 외부 Column을 추가하고 verticalScroll 수정자 적용
    Column(
        modifier = Modifier.verticalScroll(verticalScrollState)
    ) {
        Row {
            Column(modifier = Modifier.width(48.dp)) {
                Spacer(modifier = Modifier.height(32.dp)) // 헤더 공간
                hours.forEach { hour ->
                    Box(
                        modifier = Modifier
                            .height(dayHeight.dp)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(formatHour12h(hour), style = MaterialTheme.typography.bodySmall)
                    }
                }
            }

            Row(modifier = Modifier.horizontalScroll(horizontalScrollState)) {
                // 날짜별 스케줄 (오른쪽)
                days.forEach { date ->
                    Column(
                        modifier = Modifier
                            .width(dayWidth.dp)
                            .border(1.dp, Color.LightGray)
                    ) {
                        // 날짜 헤더
                        Text(
                            text = "${date.monthValue}/${date.dayOfMonth}",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(32.dp)
                                .background(Color(0xFFE3F2FD)),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(dayHeight.dp * 24)
                        ) {
                            // 시간별 그리드
                            Column {
                                hours.forEach { _ ->
                                    Box(
                                        modifier = Modifier
                                            .height(dayHeight.dp)
                                            .fillMaxWidth()
                                            .border(0.5.dp, Color.LightGray)
                                    )
                                }
                            }
                            // 이벤트 표시
                            events.filter { it.date == date }.forEach { event ->
                                val topOffset = (event.startHour * dayHeight) + (event.startMinute * dayHeight / 60)
                                val eventHeight = (event.durationMinutes * dayHeight) / 60
                                Box(
                                    modifier = Modifier
                                        .offset(y = topOffset.dp)
                                        .height(eventHeight.dp)
                                        .fillMaxWidth()
                                        .padding(horizontal = 2.dp)
                                        .background(Color(0xFF90CAF9), shape = MaterialTheme.shapes.small),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = event.title,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun WeeklyScheduleSampleScreen() {
    val today = LocalDate.now()
    val sampleEvents = listOf(
        Event(1, "아침 운동", today, 6, 30, 60),
        Event(2, "회의", today, 10, 0, 30),
        Event(3, "점심 식사", today, 12, 0, 60),
        Event(4, "스터디", today.plusDays(1), 20, 0, 90),
        Event(5, "저녁 약속", today.plusDays(3), 18, 30, 120)
    )
    Box(modifier = Modifier
        .background(Color.White)){
        WeeklySchedule(
            startDate = today.with(java.time.DayOfWeek.MONDAY), // 이번주 월요일로 시작

        )
    }
}