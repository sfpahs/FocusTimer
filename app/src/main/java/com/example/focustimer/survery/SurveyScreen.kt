package com.example.focustimer.survery

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.focustimer.navigation.LocalNavController
import com.example.shared.Myfirebase.saveSurveyData
import kotlinx.coroutines.launch

// SurveyScreen.kt
@Preview()
@Composable
fun SurveyCronotypeScreen() {
    val viewModel : SurveyCronoViewModel by lazy { SurveyCronoViewModel.getInstance() }
    val answers by viewModel.answers.collectAsState()
    val surveyCompleted by viewModel.surveyCompleted.collectAsState()
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val questions = SurveyCronotypeData.questions
    Text(
        text = "1=전혀 그렇지 않다, 5=매우 그렇다",
        style = MaterialTheme.typography.labelMedium,
        modifier = Modifier.padding(bottom = 8.dp)
    )
    LazyColumn (state = listState){
        itemsIndexed(questions){
                index, question ->

                QuestionItem(
                    question = question,
                    selectedScore = answers[index],
                    onScoreSelected = { score ->
                        viewModel.updateAnswer(questionIndex = index, score = score)
                        viewModel.completeSurvey()
                        coroutineScope.launch {
                            val visibleItems = listState.layoutInfo.visibleItemsInfo.size
                            val centerOffset = (visibleItems / 2)
                            val targetIndex = (index - centerOffset).coerceAtLeast(0)
                            listState.animateScrollToItem(targetIndex) }

                    }
                )


        }
    }
    if (surveyCompleted) {
        Box(modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background)
        ){

            //todo firebase 크로노타입 업데이트
            ResultScreen()
        }
    }



}




//결과창
// ResultScreen.kt
@Preview
@Composable
fun ResultScreen(
) {
    val viewModel : SurveyCronoViewModel by lazy { SurveyCronoViewModel.getInstance() }
    val result by viewModel.surveyResult.collectAsState()
    val navHostController = LocalNavController.current

    result?.let { surveyResult ->
        saveSurveyData(surveyResult.chronoType)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "수면-각성 패턴 분석 결과",
                fontSize = 13.sp,
                style = MaterialTheme.typography.labelMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(vertical = 24.dp)
            )

            ResultCard(
                title = "크로노타입(생체시계 유형)",
                content = surveyResult.chronoType.name,
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ResultCard(
                    title = "아침형 점수",
                    content = "${surveyResult.morningScore}/75",
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(8.dp))

                ResultCard(
                    title = "저녁형 점수",
                    content = "${surveyResult.eveningScore}/45",
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            ResultCard(
                title = "생체 리듬 민감도",
                content = SurveyCronotypeCalculator.getSensitivityLevel(surveyResult.sensitivityScore),
            )

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "상세 점수",
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        text = "크로노타입 지수: ${String.format("%.2f", surveyResult.chronotypeIndex)}",
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )

                    Text(
                        text = "아침형 표준화 점수: ${String.format("%.2f", surveyResult.morningNormalizedScore)}",
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )

                    Text(
                        text = "저녁형 표준화 점수: ${String.format("%.2f", surveyResult.eveningNormalizedScore)}",
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )

                    Text(
                        text = "생체 리듬 민감도 점수: ${surveyResult.sensitivityScore}/30",
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row {

                Button(
                    onClick = {
                        viewModel.resetAnswers()
                              },
                    modifier = Modifier.fillMaxWidth(0.5f)
                ) {
                    Text("다시 검사하기")
                }

                Button(
                    onClick = { navHostController.navigate("main") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("메인화면")
                }
            }
        }
    }
}

@Composable
fun ResultCard(
    title: String,
    content: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = content,
                style = MaterialTheme.typography.labelMedium,
                textAlign = TextAlign.Center
            )
        }
    }
}