package com.example.focustimer.survery

data class Question(
    val id: Int,
    val text: String,
    val category: QuestionCategory
)

enum class QuestionCategory {
    MORNING_TYPE,
    EVENING_TYPE,
    RHYTHM_SENSITIVITY
}

// 결과 데이터 클래스
data class SurveyResult(
    val morningScore: Int,
    val eveningScore: Int,
    val sensitivityScore: Int,
    val morningNormalizedScore: Double,
    val eveningNormalizedScore: Double,
    val chronotypeIndex: Double,
    val chronotypeType: String
)