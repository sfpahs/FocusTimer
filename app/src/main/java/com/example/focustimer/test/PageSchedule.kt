package com.example.focustimer.test

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.focustimer.Page.LoadingScreen
import java.time.LocalTime

data class Schedule(
    val id: String,
    val title: String,
    val startTime: LocalTime, // 예: 09:30
    val durationMinutes: Int  // 예: 45분
)

@Preview
@Composable
fun ScheduleScreen() {
    // Lottie 로딩 애니메이션 예시
    val schedules = listOf(
        Schedule("1", "아침 운동", LocalTime.of(6, 0), 30),
        Schedule("2", "출근 준비", LocalTime.of(7, 0), 40),
        Schedule("3", "업무 시작", LocalTime.of(9, 0), 120),
        Schedule("4", "점심 식사", LocalTime.of(12, 30), 60),
        Schedule("5", "팀 미팅", LocalTime.of(15, 0), 45),
        Schedule("6", "퇴근", LocalTime.of(18, 0), 30)
    )


    var isLoading by remember { mutableStateOf(false) }
    if (isLoading) {
        LoadingScreen(isLoading)
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text("오늘의 스케줄", style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(16.dp))
            schedules.sortedBy { it.startTime }.forEach { schedule ->
                ScheduleBox(schedule)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun ScheduleBox(schedule: Schedule) {


    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = schedule.title,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = "${schedule.startTime} ~ ${schedule.startTime.plusMinutes(schedule.durationMinutes.toLong())}",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "${schedule.durationMinutes}분",
                style = MaterialTheme.typography.labelMedium,
                color = Color.Gray
            )
        }
    }
}
