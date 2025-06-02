package com.example.focustimer.Page

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.focustimer.LocalNavController
import com.example.shared.model.subject
import com.example.shared.model.TimerViewModel
import com.example.shared.model.TimerOptions
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.remember
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditBoxScreen() {
    val viewModel: TimerViewModel by lazy { TimerViewModel.getInstance() }
    val currentSetting = viewModel.subjects.value.find { it.id == viewModel.currentSubject.value.id } ?: return

    var newName by remember { mutableStateOf(currentSetting.name) }
    var newColor by remember { mutableStateOf(currentSetting.backgroundColor) }
    var selectedTimerId by remember { mutableStateOf(currentSetting.selectedTimer) }
    var recomendTimerId by remember { mutableStateOf(currentSetting.recomendTimer) }
    var selectedTabIndex by remember { mutableStateOf(0) }
    var shouldRequestFocus by remember { mutableStateOf(false) }

    val bottomSheetState = rememberBottomSheetScaffoldState()
    val scope = rememberCoroutineScope()
    val navHostController = LocalNavController.current
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current


    // 탭 정보
    val tabTitles = listOf("암기", "이해", "논리")
    val tabEmojis = listOf("📚", "🧠", "🔍")
    val colorOptions = listOf(
        Color.LightGray,
        Color(0xFFFFCCCC), // 빨강
        Color(0xFFCCFFCC), // 초록
        Color(0xFFCCCCFF), // 파랑
        Color(0xFFFFEECC), // 주황
        Color(0xFFE6CCFF), // 보라
        Color(0xFFCCFFFF)  // 하늘
    )





    // 선택된 타이머 옵션 찾기
    val selectedTimer = if (selectedTimerId == -1) {
        // -1이면 추천 타이머를 보여줌
        TimerOptions.list.find { it.id == recomendTimerId }
    } else {
        // 일반적인 경우 선택된 타이머를 보여줌
        TimerOptions.list.find { it.id == selectedTimerId }
    }

    // 카테고리별로 그룹화
    val groupedTimers = TimerOptions.list.groupBy { it.category }
    var nameError by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }

    // BottomSheet State

// LaunchedEffect 추가
    LaunchedEffect(shouldRequestFocus) {
        if (shouldRequestFocus) {
            delay(200) // 키보드가 올라올 시간을 기다림
            focusRequester.requestFocus()
            shouldRequestFocus = false
        }
    }



    BottomSheetScaffold(
        scaffoldState = bottomSheetState,
        sheetContent = {
            // 스와이프 가능한 하단 시트 내용
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                // 미리보기 제목
                Text(
                    text = "미리보기",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(bottom = 12.dp)
                )

                // 미리보기 박스
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .background(Color(newColor), RoundedCornerShape(12.dp))
                        .border(2.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically) {

                            Text(
                                text = newName,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                        if (selectedTimer != null) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text(
                                    text = if (selectedTimerId == -1) "추천 타이머" else "선택된 타이머",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = selectedTimer.name,
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "집중: ${selectedTimer.workTime / 60}분 | 휴식: ${selectedTimer.restTime / 60}분",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }


                // 버튼 영역
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Button(
                        onClick = { navHostController.navigate("main") },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                    ) {
                        Text("취소", fontWeight = FontWeight.Medium)
                    }

                    Button(
                        onClick = {
                            if(newName.isBlank()){
                                nameError = true
                                shouldRequestFocus = true
                                return@Button
                            }

                            scope.launch {
                                if(selectedTimerId == recomendTimerId)
                                    selectedTimerId = -1
                                viewModel.editSubject(
                                    newSetting = subject(
                                        name = newName,
                                        id = currentSetting.id,
                                        backgroundColor = newColor,
                                        selectedTimer = selectedTimerId,
                                        recomendTimer = recomendTimerId
                                    )
                                )
                                navHostController.navigate("main")
                            }

                                  },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("저장", fontWeight = FontWeight.Medium)
                    }
                }
            }
        },
        sheetPeekHeight = 250.dp, // 기본적으로 보이는 높이
        modifier = Modifier.fillMaxSize(),
    ) { paddingValues ->
        // 메인 콘텐츠 영역
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .offset(y = (10).dp)
                .pointerInput(Unit) { // 추가
                detectTapGestures(onTap = {
                    focusManager.clearFocus()
                    keyboardController?.hide()
                })
            },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Text(
                    text = "박스 수정하기",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                // 글자 수정 필드
                OutlinedTextField(
                    value = newName,
                    onValueChange = {
                        newName = it
                        // 사용자가 입력하면 에러 상태 초기화
                        if (it.isNotBlank()) {
                            nameError = false
                        }
                        else nameError = true
                    },
                    singleLine = true,
                    label = {
                        Text(
                            text = "과목 이름",
                            color = if (nameError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    isError = nameError,
                    supportingText = if (nameError) {
                        { Text("필수 입력 항목입니다", color = MaterialTheme.colorScheme.error) }
                    } else null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                        .focusRequester(focusRequester = focusRequester),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            focusManager.clearFocus() // 포커스 해제
                            keyboardController?.hide() // 키보드 숨기기
                        }
                    ),
                )

                // 색상 선택 제목
                Text(
                    text = "박스 색상 선택",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )
            }

            // 색상 선택 옵션들
            item {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(colorOptions) { colorOption ->
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(colorOption)
                                .border(
                                    width = 3.dp,
                                    color = if (colorOption == Color(newColor)) Color.Black else Color.Transparent,
                                    shape = CircleShape
                                )
                                .clickable {
                                    newColor = colorOption.toArgb().toLong()
                                }
                        )
                    }
                }
            }

            // 선택된 타이머 제목 표시
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        val recomendTimer = TimerOptions.list.get(recomendTimerId)
                        Text(
                            text = "추천 타이머",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = recomendTimer.name,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = recomendTimer.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
                Text(
                    text = "추천 타이머 선택",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )
            }

            // 탭 영역
            item {
                TabRow(
                    selectedTabIndex = selectedTabIndex,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    tabTitles.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTabIndex == index,
                            onClick = { selectedTabIndex = index },
                            text = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Text(text = tabEmojis[index])
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(text = title)
                                }
                            }
                        )
                    }
                }
            }

            // 선택된 탭의 타이머 옵션들
            item {
                val currentCategory = tabTitles[selectedTabIndex]
                val timersInCategory = groupedTimers[currentCategory] ?: emptyList()

                Column {
                    timersInCategory.forEach { timer ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable {  selectedTimerId = if (selectedTimerId == timer.id) -1 else timer.id },
                            colors = CardDefaults.cardColors(
                                containerColor = if (selectedTimerId == timer.id)
                                    MaterialTheme.colorScheme.primaryContainer
                                else
                                    MaterialTheme.colorScheme.surface
                            ),
                            elevation = CardDefaults.cardElevation(
                                defaultElevation = if (selectedTimerId == timer.id) 8.dp else 2.dp
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = selectedTimerId == timer.id,
                                    onClick = null
                                )

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = timer.name,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = timer.description,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Row {
                                        Card(
                                            colors = CardDefaults.cardColors(
                                                containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                                            )
                                        ) {
                                            Text(
                                                text = "집중 ${timer.workTime / 60}분",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Card(
                                            colors = CardDefaults.cardColors(
                                                containerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f)
                                            )
                                        ) {
                                            Text(
                                                text = "휴식 ${timer.restTime / 60}분",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.secondary,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
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
    }
}
