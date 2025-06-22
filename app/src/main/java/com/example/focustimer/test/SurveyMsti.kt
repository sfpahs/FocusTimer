package com.example.focustimer.test

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import com.example.focustimer.survery.ScoreSelector
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// QuestionCategory.kt
enum class QuestionCategory {
    SUBJECTIVE,    // S
    OBJECTIVE,     // O
    ACTION,        // A
    TACTIC,        // T
    DEPENDENT,     // D
    CONFIDENT,     // C
    PRINCIPLE,     // P
    FLEXIBLE       // E
}
// Question.kt
data class Question(
    val id: Int,
    val text: String,
    val category: QuestionCategory
)
object SurveyMstiData {
    val questions = listOf(
        Question(0, "새로운 공부 방법을 시도하기보다 익숙한 방식을 고수하는 편이다.", QuestionCategory.PRINCIPLE),
        Question(1, "공부 계획을 세우기 전에 목표부터 명확히 정한다.", QuestionCategory.TACTIC),
        Question(2, "친구나 선생님의 조언을 듣고 공부 방법을 바꾸는 일이 많다.", QuestionCategory.DEPENDENT),
        Question(3, "공부할 때 스스로 동기를 부여하는 편이다.", QuestionCategory.CONFIDENT),
        Question(4, "공부할 때 원리와 개념을 이해하려고 노력한다.", QuestionCategory.PRINCIPLE),
        Question(5, "상황에 따라 공부 방법을 융통성 있게 바꾼다.", QuestionCategory.FLEXIBLE),
        Question(6, "주관적으로 생각하고 판단하는 것을 선호한다.", QuestionCategory.SUBJECTIVE),
        Question(7, "객관적인 자료와 근거를 중요하게 생각한다.", QuestionCategory.OBJECTIVE),
        Question(8, "계획을 세우기보다는 바로 실천에 옮기는 편이다.", QuestionCategory.ACTION),
        Question(9, "계획을 세우고 그에 따라 공부하는 것이 효율적이라고 생각한다.", QuestionCategory.TACTIC),
        Question(10, "주변 사람들의 의견을 자주 참고한다.", QuestionCategory.DEPENDENT),
        Question(11, "자신의 결정을 신뢰하고 밀고 나가는 편이다.", QuestionCategory.CONFIDENT),
        Question(12, "공부할 때 원칙을 중시한다.", QuestionCategory.PRINCIPLE),
        Question(13, "상황에 따라 목표를 유연하게 조정한다.", QuestionCategory.FLEXIBLE),
        Question(14, "자신만의 기준으로 공부 방법을 선택한다.", QuestionCategory.SUBJECTIVE),
        Question(15, "사실과 데이터를 바탕으로 공부한다.", QuestionCategory.OBJECTIVE),
        Question(16, "계획 없이 바로 시작하는 경우가 많다.", QuestionCategory.ACTION),
        Question(17, "목표를 세우고 단계별로 실천한다.", QuestionCategory.TACTIC),
        Question(18, "다른 사람의 피드백을 중요하게 여긴다.", QuestionCategory.DEPENDENT),
        Question(19, "스스로 문제를 해결하려고 한다.", QuestionCategory.CONFIDENT),
        Question(20, "공부할 때 항상 일정한 방식을 유지한다.", QuestionCategory.PRINCIPLE),
        Question(21, "새로운 방법을 시도하는 데 거부감이 없다.", QuestionCategory.FLEXIBLE),
        Question(22, "자신의 생각을 믿고 행동한다.", QuestionCategory.SUBJECTIVE),
        Question(23, "객관적인 평가를 중시한다.", QuestionCategory.OBJECTIVE),
        Question(24, "계획보다는 행동이 먼저다.", QuestionCategory.ACTION),
        Question(25, "계획적으로 시간을 관리한다.", QuestionCategory.TACTIC),
        Question(26, "주변의 조언을 잘 받아들인다.", QuestionCategory.DEPENDENT),
        Question(27, "자신의 능력을 믿고 도전한다.", QuestionCategory.CONFIDENT),
        Question(28, "규칙과 원칙을 지키는 것이 중요하다.", QuestionCategory.PRINCIPLE),
        Question(29, "상황에 따라 유연하게 대처한다.", QuestionCategory.FLEXIBLE)
    )
}
data class MstiResult(
    val sScore: Int,
    val oScore: Int,
    val aScore: Int,
    val tScore: Int,
    val dScore: Int,
    val cScore: Int,
    val pScore: Int,
    val eScore: Int,
    val type: String // 예: "STCP"
)
object SurveyMstiCalculator {
    fun calculateResult(answers: Map<Int, Int>, questions: List<Question>): MstiResult {
        // 카테고리별 점수 합산
        val scores = mutableMapOf(
            QuestionCategory.SUBJECTIVE to 0,
            QuestionCategory.OBJECTIVE to 0,
            QuestionCategory.ACTION to 0,
            QuestionCategory.TACTIC to 0,
            QuestionCategory.DEPENDENT to 0,
            QuestionCategory.CONFIDENT to 0,
            QuestionCategory.PRINCIPLE to 0,
            QuestionCategory.FLEXIBLE to 0
        )
        answers.forEach { (index, score) ->
            val category = questions[index].category
            scores[category] = scores.getOrDefault(category, 0) + score
        }
        // 각 쌍별로 높은 점수의 알파벳 선택
        val sOrO = if (scores[QuestionCategory.SUBJECTIVE]!! >= scores[QuestionCategory.OBJECTIVE]!!) "S" else "O"
        val aOrT = if (scores[QuestionCategory.ACTION]!! >= scores[QuestionCategory.TACTIC]!!) "A" else "T"
        val dOrC = if (scores[QuestionCategory.DEPENDENT]!! >= scores[QuestionCategory.CONFIDENT]!!) "D" else "C"
        val pOrE = if (scores[QuestionCategory.PRINCIPLE]!! >= scores[QuestionCategory.FLEXIBLE]!!) "P" else "E"
        val type = sOrO + aOrT + dOrC + pOrE

        return MstiResult(
            sScore = scores[QuestionCategory.SUBJECTIVE]!!,
            oScore = scores[QuestionCategory.OBJECTIVE]!!,
            aScore = scores[QuestionCategory.ACTION]!!,
            tScore = scores[QuestionCategory.TACTIC]!!,
            dScore = scores[QuestionCategory.DEPENDENT]!!,
            cScore = scores[QuestionCategory.CONFIDENT]!!,
            pScore = scores[QuestionCategory.PRINCIPLE]!!,
            eScore = scores[QuestionCategory.FLEXIBLE]!!,
            type = type
        )
    }
}

class SurveyMstiViewModel : ViewModel() {
    private val _answers = MutableStateFlow<Map<Int, Int>>(mutableMapOf())
    val answers: StateFlow<Map<Int, Int>> = _answers.asStateFlow()

    private val _surveyCompleted = MutableStateFlow(false)
    val surveyCompleted: StateFlow<Boolean> = _surveyCompleted.asStateFlow()

    private val _surveyResult = MutableStateFlow<MstiResult?>(null)
    val surveyResult: StateFlow<MstiResult?> = _surveyResult.asStateFlow()

    fun updateAnswer(questionIndex: Int, score: Int) {
        val updatedAnswers = _answers.value.toMutableMap()
        updatedAnswers[questionIndex] = score
        _answers.value = updatedAnswers
        if (_answers.value.size == SurveyMstiData.questions.size) {
            _surveyResult.value = SurveyMstiCalculator.calculateResult(_answers.value, SurveyMstiData.questions)
            _surveyCompleted.value = true
        }
    }

    fun resetAnswers() {
        _answers.value = mutableMapOf()
        _surveyCompleted.value = false
        _surveyResult.value = null
    }

    companion object {
        // 싱글톤 인스턴스
        private var instance: SurveyMstiViewModel? = null
        // 인스턴스 가져오기
        fun getInstance(): SurveyMstiViewModel {
            if (instance == null) {
                instance = SurveyMstiViewModel()
            }
            return instance!!
        }
    }
}

// SurveyScreen.kt
@Preview()
@Composable
fun SurveyMstiScreen() {
    val viewModel : SurveyMstiViewModel by lazy { SurveyMstiViewModel.getInstance() }
    val answers by viewModel.answers.collectAsState()
    val surveyCompleted by viewModel.surveyCompleted.collectAsState()
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val questions = SurveyMstiData.questions
    Text(
        text = "1=전혀 그렇지 않다, 5=매우 그렇다",
        style = MaterialTheme.typography.labelMedium,
        modifier = Modifier.padding(bottom = 8.dp)
    )
    if(!surveyCompleted){
    LazyColumn (state = listState){
        itemsIndexed(questions){
                index, question ->

            QuestionItem(
                question = question,
                selectedScore = answers[index],
                onScoreSelected = { score ->
                    viewModel.updateAnswer(questionIndex = index, score = score)
                    //todo
                    coroutineScope.launch {
                        val visibleItems = listState.layoutInfo.visibleItemsInfo.lastIndex
                        val centerOffset = (visibleItems / 2)
                        val targetIndex = (index - centerOffset).coerceAtLeast(0)
                        listState.animateScrollToItem(targetIndex) }

                }
            )


        }
    }
        }
    else {
        Box(modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background)
        ){


            ResultScreen(onRestart = {viewModel.resetAnswers()})
        }
    }



}

@Preview
@Composable
fun QuestionItem(
    question: Question = Question(
        text = "testQuestion",
        category = QuestionCategory.TACTIC,
        id = -1
    ),
    selectedScore: Int? = null,
    onScoreSelected: (Int) -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "${question.id}. ${question.text}",
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        ScoreSelector(
            selectedScore = selectedScore,
            onScoreSelected = onScoreSelected
        )
    }
}
@Composable
fun ResultScreen(
    viewModel: SurveyMstiViewModel = SurveyMstiViewModel.getInstance(),
    onRestart: () -> Unit = {}
) {
    val result by viewModel.surveyResult.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        if (result != null) {
            val typeDesc = mstiTypeDescription(result!!.type)
            val studyTips = mstiTypeStudyTips(result!!.type)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "당신의 학습 유형은",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(Modifier.height(16.dp))
                Text(
                    text = result!!.type,
                    style = MaterialTheme.typography.displayMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.height(24.dp))
                Text(
                    text = typeDesc,
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(Modifier.height(32.dp))
                Text(
                    text = "추천 학습법",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.secondary
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    text = studyTips,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(32.dp))
                Button(onClick = onRestart) {
                    Text("다시 검사하기")
                }
            }
        } else {
            CircularProgressIndicator()
        }
    }
}

// 유형별 설명
fun mstiTypeDescription(type: String): String = when(type) {
    "STCP" -> "계획적이고 원칙을 중시하는 학습자입니다. 목표를 명확히 세우고 체계적으로 실천합니다."
    "STCE" -> "계획적이면서도 유연하게 대처하는 학습자입니다. 원칙을 지키면서도 상황에 따라 적응합니다."
    "STDP" -> "계획적이면서 타인의 의견을 존중하는 학습자입니다. 목표와 원칙을 세우되 협력도 중요하게 생각합니다."
    "STDE" -> "계획적이면서도 유연하고 타인의 의견을 잘 수용하는 학습자입니다. 협력과 적응에 능합니다."
    "SOCP" -> "객관적이고 자신감 있는 학습자입니다. 논리적 사고와 자기 주도성이 뛰어납니다."
    "SOCE" -> "객관적이면서도 유연한 학습자입니다. 논리와 적응력을 겸비했습니다."
    "SODP" -> "객관적이면서 타인의 의견을 존중하는 학습자입니다. 협력과 논리적 사고를 중시합니다."
    "SODE" -> "객관적이고 유연하며 협력적인 학습자입니다. 상황에 맞게 논리적으로 대처합니다."
    "OTCP" -> "실용적이고 원칙을 중시하는 학습자입니다. 행동력이 뛰어나고 체계적으로 실천합니다."
    "OTCE" -> "실용적이면서도 유연한 학습자입니다. 상황에 따라 실용적으로 접근하고 적응합니다."
    "OTDP" -> "실용적이면서 협력을 중시하는 학습자입니다. 행동력과 협동심을 겸비했습니다."
    "OTDE" -> "실용적이고 유연하며 협력적인 학습자입니다. 상황에 따라 실용적으로 협력합니다."
    "OACP" -> "실천적이고 자신감 있는 학습자입니다. 적극적으로 목표를 달성하며 자기 주도적입니다."
    "OACE" -> "유연하고 실천적인 학습자입니다. 다양한 방법을 시도하며 상황에 맞게 대처합니다."
    "OADP" -> "실천적이면서 협력적인 학습자입니다. 적극적으로 행동하며 타인과의 협력도 중요시합니다."
    "OADE" -> "실천적이고 유연하며 협력적인 학습자입니다. 적극적으로 적응하고 협력합니다."
    else -> "아직 설명이 준비되지 않았어요."
}

// 유형별 상세 학습법
fun mstiTypeStudyTips(type: String): String = when(type) {
    "STCP" -> """
        1. 장기·단기 목표를 구체적으로 세우고, 체크리스트와 타임테이블을 활용해 실천하세요.
        2. 개념과 원리를 반복적으로 정리하고, 마인드맵이나 요약노트를 만들어 체계적으로 정리하세요.
        3. 정보는 논리적으로 분류하고, 학습 후 반드시 복습 일정을 세우세요.
        4. 스스로 피드백하며, 오답노트를 적극 활용하세요.
    """.trimIndent()
    "STCE" -> """
        1. 큰 틀의 계획은 세우되, 상황에 따라 학습 순서나 방식은 유연하게 조정하세요.
        2. 여러 학습법(플래너, 플래시카드, 토론 등)을 번갈아 활용하세요.
        3. 예상치 못한 일정 변화에도 스트레스 받지 않도록 여유를 두고 계획하세요.
        4. 복습은 필수지만, 복습 방식은 매번 다르게 시도해보세요.
    """.trimIndent()
    "STDP" -> """
        1. 스터디 그룹을 결성해 역할을 분담하고, 서로의 진도를 체크하세요.
        2. 토론을 통해 자신의 논리와 원칙을 검증받으세요.
        3. 계획표를 공유하고, 동료와 함께 목표를 점검하세요.
        4. 그룹 과제나 프로젝트에 적극 참여하세요.
    """.trimIndent()
    "STDE" -> """
        1. 계획은 세우되, 팀원들과의 협의를 통해 유연하게 조정하세요.
        2. 다양한 사람들과 스터디를 하며, 서로 다른 학습법을 경험해보세요.
        3. 상황에 따라 학습 목표나 방법을 바꿀 수 있도록 여지를 남기세요.
        4. 피드백을 주고받는 환경을 조성하세요.
    """.trimIndent()
    "SOCP" -> """
        1. 객관적 자료(통계, 논문, 데이터 등)를 적극 활용하세요.
        2. 자기주도적으로 목표를 세우고, 논리적 근거를 바탕으로 학습 계획을 세우세요.
        3. 학습 내용을 표, 그래프, 도식 등으로 시각화하세요.
        4. 스스로 문제를 만들고, 그 해답을 논리적으로 검증하세요.
    """.trimIndent()
    "SOCE" -> """
        1. 다양한 참고자료와 학습 도구(앱, 온라인 강의 등)를 적극 활용하세요.
        2. 논리적 근거를 기반으로 하되, 새로운 학습법을 시도하세요.
        3. 문제풀이 방식이나 학습 순서를 자주 바꿔보세요.
        4. 정보의 신뢰도를 스스로 평가하는 습관을 들이세요.
    """.trimIndent()
    "SODP" -> """
        1. 객관적 자료를 바탕으로 그룹 스터디를 하세요.
        2. 토론과 발표를 통해 자신의 논리를 검증받으세요.
        3. 역할 분담을 명확히 하고, 결과를 수치나 데이터로 평가하세요.
        4. 피드백을 논리적으로 주고받으세요.
    """.trimIndent()
    "SODE" -> """
        1. 다양한 학습법(프로젝트, 토론, 실습 등)을 그룹과 함께 시도하세요.
        2. 문제 해결을 위해 여러 사람의 의견을 듣고, 다양한 방법을 시도하세요.
        3. 상황에 따라 역할을 바꾸거나, 학습 전략을 조정하세요.
        4. 그룹 내 피드백 시스템을 구축하세요.
    """.trimIndent()
    "OTCP" -> """
        1. 실제 사례, 실습, 프로젝트 기반 학습을 계획적으로 진행하세요.
        2. 실생활과 연관된 문제를 직접 해결해보세요.
        3. 실습 결과를 기록하고, 반복적으로 점검하세요.
        4. 실용적 목표(자격증, 포트폴리오 등)를 설정하세요.
    """.trimIndent()
    "OTCE" -> """
        1. 다양한 실용적 학습법(실습, 체험, 현장학습 등)을 상황에 따라 조합하세요.
        2. 계획은 세우되, 필요에 따라 방법을 바꿔가며 실천하세요.
        3. 새로운 도구나 자료를 적극적으로 시도하세요.
        4. 실습 후 피드백을 받고, 개선점을 찾아보세요.
    """.trimIndent()
    "OTDP" -> """
        1. 프로젝트형 그룹 과제, 실습 중심의 협동 학습을 하세요.
        2. 역할을 나누어 실질적 결과물을 만들어보세요.
        3. 협동 과정에서 생긴 문제를 함께 해결하세요.
        4. 결과를 공유하고, 서로 피드백을 주고받으세요.
    """.trimIndent()
    "OTDE" -> """
        1. 다양한 실습, 프로젝트, 협동 학습을 자유롭게 시도하세요.
        2. 팀원들과 함께 새로운 방법을 개발하거나 적용해보세요.
        3. 상황에 따라 역할이나 방법을 유연하게 조정하세요.
        4. 협동 과정에서 적극적으로 의견을 제시하세요.
    """.trimIndent()
    "OACP" -> """
        1. 직접 실천해보는 학습(문제풀이, 실습, 체험 등)을 중심으로 하세요.
        2. 계획에 얽매이지 말고, 행동하면서 배우세요.
        3. 실패와 성공의 경험을 기록하고, 자기 피드백을 하세요.
        4. 목표를 세우고, 도전적인 과제를 자주 시도하세요.
    """.trimIndent()
    "OACE" -> """
        1. 다양한 방법을 시도하며, 상황에 따라 학습 전략을 바꿔보세요.
        2. 여러 자료를 수집·분석하고, 새로운 정보를 적극적으로 활용하세요.
        3. 직접 해보는 경험을 중시하세요.
        4. 학습 과정에서 얻은 인사이트를 바로 적용해보세요.
    """.trimIndent()
    "OADP" -> """
        1. 팀 프로젝트, 실습, 현장체험 등 협동적 실천 학습을 하세요.
        2. 역할을 나누어 함께 문제를 해결하세요.
        3. 협동 과정에서 적극적으로 의견을 주고받으세요.
        4. 결과를 공유하고, 함께 피드백을 하세요.
    """.trimIndent()
    "OADE" -> """
        1. 다양한 실습, 협동, 융통적 학습법을 자유롭게 조합하세요.
        2. 팀원들과 함께 새로운 도전과제를 시도하세요.
        3. 상황에 따라 학습 전략, 역할, 방법을 유연하게 바꿔보세요.
        4. 협동 과정에서 생긴 문제를 함께 해결하고, 경험을 공유하세요.
    """.trimIndent()
    else -> "아직 추천 학습법이 준비되지 않았어요."
}
