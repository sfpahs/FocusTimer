package com.example.focustimer.test


import android.util.Log
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.focustimer.LocalNavController
import com.example.shared.model.subject
import com.example.shared.model.TimerViewModel
import kotlinx.coroutines.launch




@Composable
fun EditBoxScreen(
) {
    val viewModel : TimerViewModel by lazy { TimerViewModel.getInstance() }
    val currentSetting = viewModel.subjects.value.find { it.id == viewModel.currentSubject.value.id } ?: return
    var newName by remember { mutableStateOf(currentSetting.name) }
    var newColor by remember { mutableStateOf(currentSetting.backgroundColor) }

    val scope = rememberCoroutineScope()
    val navHostController = LocalNavController.current
    //FIXME 나중에 오브젝트로 뺄것
    val colorOptions = listOf(
        Color.LightGray,
        Color(0xFFFFCCCC), // 빨강
        Color(0xFFCCFFCC), // 초록
        Color(0xFFCCCCFF), // 파랑
        Color(0xFFFFEECC), // 주황
        Color(0xFFE6CCFF), // 보라
        Color(0xFFCCFFFF)  // 하늘
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "박스 수정하기",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // 글자 수정 필드
        OutlinedTextField(
            value = newName,
            onValueChange = { newName = it },
            label = { Text("표시할 텍스트") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        // 색상 선택 제목
        Text(
            text = "박스 색상 선택",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .align(Alignment.Start)
                .padding(vertical = 8.dp)
        )

        // 색상 선택 옵션들
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),


        ) {
            items(colorOptions){ colorOption ->
                Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(colorOption)
                    .border(
                        width = 2.dp,
                        color = if (colorOption == Color(newColor)) Color.Black else Color.Transparent,
                        shape = CircleShape
                    )
                    .clickable {
                        Log.d("edit", "EditBoxScreen: color : $newColor change : $colorOption")
                        newColor = colorOption.toArgb().toLong()
                        Log.d("edit", "EditBoxScreen: newcolor : $newColor")
                    }
            )}
        }

        // 미리보기
        Text(
            text = "미리보기",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .align(Alignment.Start)
                .padding(vertical = 8.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .background(Color(newColor), RoundedCornerShape(8.dp))
                .border(1.dp, Color.Gray, RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = newName,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // 버튼 영역
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = {navHostController.navigate("main")},
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
            ) {
                Text("취소")
            }

            Button(
                onClick = {
                    scope.launch {

                        viewModel.editSubject(
                            newSetting = subject(
                                name = newName,
                                id = currentSetting.id,
                                backgroundColor = newColor,
                                restTime = currentSetting.restTime,
                                workTime = currentSetting.workTime))
                        navHostController.navigate("main")
                    }

                },
                modifier = Modifier.weight(1f)
            ) {
                Text("저장")
            }
        }

    }
}
