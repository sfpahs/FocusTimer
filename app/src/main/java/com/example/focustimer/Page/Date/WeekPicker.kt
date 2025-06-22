package com.example.focustimer.Page.Date

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate

@Composable
fun MonthYearPickerDialog(
    initialDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    onDismiss: () -> Unit
) {
    val today = LocalDate.now()
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
                    IconButton(
                        onClick = { selectedYear-- },
                        enabled = selectedYear > 1900 // 필요시 최소 연도 조정
                    ) {
                        Icon(Icons.Default.KeyboardArrowLeft, contentDescription = "이전 연도")
                    }

                    Text(
                        text = "${selectedYear}년",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    val isMaxYear = selectedYear >= today.year
                    IconButton(
                        onClick = { if (!isMaxYear) selectedYear++ },
                        enabled = !isMaxYear
                    ) {
                        Icon(Icons.Default.KeyboardArrowRight, contentDescription = "다음 연도")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 월 선택 그리드
                val months = listOf(
                    "1월",
                    "2월",
                    "3월",
                    "4월",
                    "5월",
                    "6월",
                    "7월",
                    "8월",
                    "9월",
                    "10월",
                    "11월",
                    "12월"
                )
                Column {
                    for (i in 0..3) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            for (j in 0..2) {
                                val monthIndex = i * 3 + j
                                val monthNumber = monthIndex + 1
                                val isFutureMonth = (selectedYear > today.year) ||
                                        (selectedYear == today.year && monthNumber > today.monthValue)

                                Button(
                                    onClick = { if (!isFutureMonth) selectedMonth = monthNumber },
                                    enabled = !isFutureMonth,
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
            val isFutureDate = (selectedYear > today.year) ||
                    (selectedYear == today.year && selectedMonth > today.monthValue)
            Button(
                onClick = {
                    val newDate = LocalDate.of(selectedYear, selectedMonth, 1)
                    onDateSelected(newDate)
                },
                enabled = !isFutureDate
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