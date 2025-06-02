package com.example.focustimer.Page

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.focustimer.LocalNavController
import com.example.shared.model.TimerViewModel
import com.example.shared.model.TimerOptions
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.remember
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import com.example.shared.model.TimerOption

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangeTimerOption() {
    val viewModel: TimerViewModel by lazy { TimerViewModel.getInstance() }
    val currentSetting = viewModel.subjects.value.find { it.id == viewModel.currentSubject.value.id } ?: return

    val option = viewModel.timerOption.collectAsState()
    var currentTimerId by remember {
        mutableIntStateOf(
            TimerOptions.list.indexOf<TimerOption>(option.value).takeIf { it >= 0 } ?: -1
        )
    }
        //현재 값받아와서 해야함
    // 사용자가 선택한거
    var selectedTimerId by remember { mutableStateOf(currentSetting.selectedTimer) }
    //추천
    var recommendedTimerId by remember { mutableStateOf(currentSetting.recomendTimer) }
    //초기화일때
    var lastTimerId by remember {
        mutableStateOf(
        if (selectedTimerId != -1) {
            selectedTimerId
        } else {
            recommendedTimerId
        }
        )
    }
    val newTimer = TimerOptions.list.get(currentTimerId)

    var selectedTabIndex by remember { mutableStateOf(0) }

    val bottomSheetState = rememberBottomSheetScaffoldState()
    val navHostController = LocalNavController.current
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    // 탭 정보
    val tabTitles = listOf("암기", "이해", "논리")
    val tabEmojis = listOf("📚", "🧠", "🔍")

    // 카테고리별로 그룹화
    val groupedTimers = TimerOptions.list.groupBy { it.category }

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
                        .background(
                            color = MaterialTheme.colorScheme.primaryContainer,
                            RoundedCornerShape(12.dp)
                        )
                        .border(
                            2.dp,
                            MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                            RoundedCornerShape(12.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically) {


                        if (newTimer != null) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text(
                                    text = if (selectedTimerId == -1) "추천 타이머" else "선택된 타이머",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = newTimer.name,
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "집중: ${newTimer.workTime / 60}분 | 휴식: ${newTimer.restTime / 60}분",
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
                        onClick = { navHostController.popBackStack() },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                    ) {
                        Text("취소", fontWeight = FontWeight.Medium)
                    }

                    Button(
                        onClick = {
                            //todo
                                viewModel.setOption(
                                    TimerOptions.list.get(
                                        currentTimerId
                                    )
                                )

                            //뒤로가기
                                    navHostController.popBackStack();
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

                // 색상 선택 제목
                Text(
                    text = "박스 색상 선택",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )
            }

            // 선택된 타이머 제목 표시
            //유저 기존 선택, 추천타이머 표시
            item {
                Row {
                    if (recommendedTimerId != -1) {
                        val recomendtimer = TimerOptions.list.get(recommendedTimerId)
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .padding(bottom = 16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text(
                                    text = "추천 타이머",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Text(
                                    text = recomendtimer.name,
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Text(
                                    text = recomendtimer.description,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                    }
                    if (selectedTimerId != -1) {
                        val userTimer = TimerOptions.list.get(selectedTimerId)
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .padding(bottom = 16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text(
                                    text = "기존 선택 타이머",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Text(
                                    text = userTimer.name,
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Text(
                                    text = userTimer.description,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
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
                                .clickable {
                                    currentTimerId =
                                        if (currentTimerId == timer.id) lastTimerId else timer.id
                                },
                            colors = CardDefaults.cardColors(
                                containerColor = if (currentTimerId == timer.id)
                                    MaterialTheme.colorScheme.primaryContainer
                                else
                                    MaterialTheme.colorScheme.surface
                            ),
                            elevation = CardDefaults.cardElevation(
                                defaultElevation = if (currentTimerId == timer.id) 8.dp else 2.dp
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = currentTimerId == timer.id,
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
