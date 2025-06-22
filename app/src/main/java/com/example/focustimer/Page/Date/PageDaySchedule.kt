package com.example.focustimer.Page.Date

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.runtime.*
import androidx.compose.ui.text.style.TextAlign
import com.example.focustimer.HistoryViewModel
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.ui.*
import androidx.compose.ui.text.font.FontWeight
import java.time.*


@Composable
fun DaySelector(
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit
) {
    val weekStart = selectedDate.with(DayOfWeek.MONDAY)
    val koreanDays = listOf("월", "화", "수", "목", "금", "토", "일")
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        for (i in 0..6) {
            val date = weekStart.plusDays(i.toLong())
            val isSelected = date == selectedDate
            Button(
                onClick = { onDateSelected(date) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isSelected) Color.Blue else Color.LightGray
                ),
                modifier = Modifier.width(50.dp)

            ) {
                Text(text = koreanDays[i],
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center)
            }
        }
    }
}


@Composable
fun DailySchedule(
    events: List<Event>,
    selectedDate: LocalDate
) {
    val dailyEvents = events.filter { it.date == selectedDate }
        .sortedWith(compareBy({ it.startHour }, { it.startMinute }))

    Column(modifier = Modifier.padding(16.dp)) {
        if (dailyEvents.isEmpty()) {
            Text("일정이 없습니다.", color = Color.Gray)
        } else {
            dailyEvents.forEach { event ->
                EventRow(event)
            }
        }
    }
}
@Composable
fun EventRow(event: Event) {
    var expanded by remember { mutableStateOf(false) }
    val startTime = String.format("%02d:%02d", event.startHour, event.startMinute)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .background(Color(0xFFE3F2FD), shape = RoundedCornerShape(8.dp))
            .padding(12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = startTime,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.width(60.dp)
            )
            Spacer(Modifier.width(12.dp))
            Text(
                text = event.title,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f)
            )
            Spacer(Modifier.width(12.dp))
            Text(
                text = "${event.durationMinutes}분",
                color = Color.DarkGray
            )
            Spacer(Modifier.width(12.dp))
            // 상세보기/닫기 버튼
            Icon(
                imageVector = if (expanded) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowUp,
                contentDescription = if (expanded) "닫기" else "상세",
                modifier = Modifier
                    .clickable { expanded = !expanded }
                    .size(32.dp)
            )
        }
        if (expanded) {
            Spacer(Modifier.height(8.dp))
            Text(
                text = event.description,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Black,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}
@Composable
fun DailyScheduleScreen(
    startDate : LocalDate
) {
    val startDay = startDate.with(DayOfWeek.MONDAY)
    var selectedDate by remember { mutableStateOf(startDate) }
    val historyViewModel : HistoryViewModel by lazy { HistoryViewModel.getInstance() }
    val events by historyViewModel.events.collectAsState()
    val days = (0..6).map { startDay.plusDays(it.toLong()) }
    LaunchedEffect(Unit) {
        historyViewModel.loadEventsForDates(days)
        Log.i("TAG", "DailyScheduleScreen: $events")
    }
    Column {
        DaySelector(selectedDate = selectedDate, onDateSelected = { selectedDate = it })
        Spacer(Modifier.height(10.dp))
        DailySchedule(events = events, selectedDate = selectedDate)

    }
}