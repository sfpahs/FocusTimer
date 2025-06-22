package com.example.focustimer.Page.Date

import android.util.Log
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.zIndex
import com.example.focustimer.HistoryViewModel
import com.example.shared.model.CronoTimeViewModel
import com.example.shared.model.TimerViewModel
import com.example.shared.model.time

data class Event(
    val id: Int,
    val title: String,
    val date: LocalDate,
    val startHour: Int,        // 0~23
    val startMinute: Int,      // 0~59
    val durationMinutes: Int,   // 분 단위
    val category : Int,
    val description : String = ""
)


fun formatHour12h(hour: Int): String =
    when {
        hour == 0 -> "12am"
        hour in 1..11 -> "${hour}am"
        hour == 12 -> "12pm"
        else -> "${hour - 12}pm"
    }

@Composable
fun WeeklySchedule(startDate : LocalDate) {
    val startDay = startDate.with(java.time.DayOfWeek.MONDAY)
    val days = (0..6).map { startDay.plusDays(it.toLong()) }
    val hours = (0..23).toList()
    val historyViewModel : HistoryViewModel by lazy { HistoryViewModel.getInstance() }
    val cronoTimeViewModel : CronoTimeViewModel by lazy { CronoTimeViewModel.getInstance() }
    val viewModel : TimerViewModel by lazy { TimerViewModel.getInstance() }
    val timerSettings = viewModel.subjects.collectAsState()
    val timersetting = timerSettings.value


    val cronotype = cronoTimeViewModel.surveyData.collectAsState()
    val cronoType = cronotype.value
    val events by historyViewModel.events.collectAsState()

    Log.i("TAG", "WeeklySchedule: $cronoType")
    val productiveColor = Color(0xffc4c6fe) // 반투명 파란색
    val creativeColor = Color(0xffffedcc)  // 반투명 주황색
    val workoutColor = Color(0xffccffcc)   // 반투명 초록색
    val sleepColor = Color(0xffe6cce6)     // 반투명 보라색
    val horizontalScrollState = rememberScrollState()
    val verticalScrollState = rememberScrollState()  // 수직 스크롤 상태 추가
    val dayHeight = 32
    val dayWidth = 55

    LaunchedEffect(Unit) {
        historyViewModel.loadEventsForDates(days)
        verticalScrollState.scrollTo(600)
    }

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

                            // 크로노타입 배경 그리기
                            // 크로노타입이 널이 아닌 경우에만 그리기
                            cronoType?.let { cronoTime ->
                                // Productive 시간대 표시
                                DrawCronoTimeBox(
                                    timeData = cronoTime.productive1,
                                    color = productiveColor,
                                    dayHeight = dayHeight
                                )

                                // Productive 2 시간대 (있는 경우)
                                cronoTime.productive2?.let {
                                    DrawCronoTimeBox(
                                        timeData = it,
                                        color = productiveColor,
                                        dayHeight = dayHeight
                                    )
                                }

                                // Creative 시간대 (있는 경우)
                                cronoTime.creative?.let {
                                    DrawCronoTimeBox(
                                        timeData = it,
                                        color = creativeColor,
                                        dayHeight = dayHeight
                                    )
                                }

                                // Workout 시간대
                                DrawCronoTimeBox(
                                    timeData = cronoTime.workout1,
                                    color = workoutColor,
                                    dayHeight = dayHeight
                                )

                                // Workout 2 시간대 (있는 경우)
                                cronoTime.workout2?.let {
                                    DrawCronoTimeBox(
                                        timeData = it,
                                        color = workoutColor,
                                        dayHeight = dayHeight
                                    )
                                }
                                // 수면 시간대 표시 (저녁~아침)
                                if (cronoTime.sleep.start > cronoTime.wakeup.end) {
                                    // 취침 시간이 자정을 넘어가는 경우
                                    DrawCronoTimeBox(
                                        timeData = time(cronoTime.sleep.start, 24f),
                                        color = sleepColor,
                                        dayHeight = dayHeight
                                    )
                                    DrawCronoTimeBox(
                                        timeData = time(0f, cronoTime.wakeup.end),
                                        color = sleepColor,
                                        dayHeight = dayHeight
                                    )
                                } else {
                                    // 취침시간이 같은 날에 있는 경우
                                    DrawCronoTimeBox(
                                        timeData = time(cronoTime.sleep.start, cronoTime.wakeup.end),
                                        color = sleepColor,
                                        dayHeight = dayHeight
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
                                        .background(
                                            Color(viewModel.subjects.value.get(event.category).backgroundColor),
                                            shape = MaterialTheme.shapes.small
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if((eventHeight>10))
                                    Text(
                                        text = event.title,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.Black
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .background(Color.White),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            LegendItem("생산적", productiveColor)
            LegendItem("창의적", creativeColor)
            LegendItem("운동", workoutColor)
            LegendItem("수면", sleepColor)
            //LegendItem("기상", wakeupColor)
        }
    }
}


@Composable
private fun LegendItem(text: String, color: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .background(color, shape = MaterialTheme.shapes.small)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(text, style = MaterialTheme.typography.bodySmall)
    }
}

// 크로노타입 시간대 박스를 그리는 함수
@Composable
fun DrawCronoTimeBox(timeData: time, color: Color, dayHeight: Int) {
    if(timeData.start >= 0 && timeData.end >= 0) {
        val topOffset = (timeData.start * dayHeight)
        val boxHeight = ((timeData.end - timeData.start) * dayHeight)

        Box(
            modifier = Modifier
                .offset(y = topOffset.dp)
                .height(boxHeight.dp)
                .fillMaxWidth()
                .background(color)
                .zIndex(-1f) // 이벤트보다 아래에 그려지도록 설정
        )
    }
}

@Preview
@Composable
fun WeeklyScheduleSampleScreen() {
    Box(modifier = Modifier
        .background(Color.White)){
        WeeklySchedule(startDate = LocalDate.now())
    }
}