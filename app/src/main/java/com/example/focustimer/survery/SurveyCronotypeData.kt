package com.example.focustimer.survery

import com.example.shared.model.CronoTimeSchedule

// SurveyData.kt
object SurveyCronotypeData {
    val questions = listOf(
        Question(0, "아침에 알람 없이도 쉽게 일어나는 편이다.", QuestionCategory.MORNING_TYPE),
        Question(1, "주말에도 평일과 비슷한 시간에 일어나는 편이다.", QuestionCategory.MORNING_TYPE),
        Question(2, "아침에 일어나자마자 정신이 맑아진다.", QuestionCategory.MORNING_TYPE),
        Question(3, "밤 10시 이전에 졸음을 느끼는 경우가 많다.", QuestionCategory.MORNING_TYPE),
        Question(4, "밤 12시가 넘어도 정신이 또렷한 편이다.", QuestionCategory.EVENING_TYPE),
        Question(5, "늦은 밤(새벽 1-3시)에 가장 창의적인 아이디어가 떠오른다.", QuestionCategory.EVENING_TYPE),
        Question(6, "오후나 저녁에 낮잠을 자지 않으면 하루를 버티기 힘들다.", QuestionCategory.RHYTHM_SENSITIVITY),
        Question(7, "주말에는 평일보다 2시간 이상 더 늦게 잠자리에 든다.", QuestionCategory.EVENING_TYPE),
        Question(8, "오전 시간대(6-9시)에 운동하는 것이 가장 효과적이라고 느낀다.", QuestionCategory.MORNING_TYPE),
        Question(9, "중요한 결정은 오전 중에 내리는 것이 좋다고 생각한다.", QuestionCategory.MORNING_TYPE),
        Question(10, "오후 2-4시 사이에 졸음이나 에너지 저하를 느끼는 경우가 많다.", QuestionCategory.RHYTHM_SENSITIVITY),
        Question(11, "저녁 시간(7-10시)에 집중력이 가장 높아진다.", QuestionCategory.EVENING_TYPE),
        Question(12, "아침 식사는 하루 중 가장 중요한 식사라고 생각한다.", QuestionCategory.MORNING_TYPE),
        Question(13, "밤에 일하거나 공부할 때 가장 효율적이라고 느낀다.", QuestionCategory.EVENING_TYPE),
        Question(14, "오전 중에 가장 중요한 업무나 과제를 처리하는 것을 선호한다.", QuestionCategory.MORNING_TYPE),
        Question(15, "저녁이나 밤 시간에 사교 활동을 하는 것을 즐긴다.", QuestionCategory.EVENING_TYPE),
        Question(16, "아침에 일어났을 때 기분이 좋고 에너지가 넘친다.", QuestionCategory.MORNING_TYPE),
        Question(17, "저녁으로 갈수록 점점 더 활기차고 에너지가 생긴다.", QuestionCategory.EVENING_TYPE),
        Question(18, "오후 시간대에 가장 기분이 좋고 편안함을 느낀다.", QuestionCategory.RHYTHM_SENSITIVITY),
        Question(19, "일주일 내내 비슷한 시간에 잠자리에 들고 일어나는 것이 기분과 에너지 수준에 도움이 된다.", QuestionCategory.MORNING_TYPE),
        Question(20, "밤늦게까지 깨어 있으면 다음 날 컨디션이 크게 저하된다.", QuestionCategory.MORNING_TYPE),
        Question(21, "일찍 일어나도 하루 종일 에너지를 유지할 수 있다.", QuestionCategory.MORNING_TYPE),
        Question(22, "아침 일찍 시작하는 약속이나 활동을 선호한다.", QuestionCategory.MORNING_TYPE),
        Question(23, "밤늦게 끝나는 행사나 모임에 참석하는 것을 즐긴다.", QuestionCategory.EVENING_TYPE),
        Question(24, "식사 시간이 불규칙해도 큰 불편함을 느끼지 않는다.", QuestionCategory.RHYTHM_SENSITIVITY),
        Question(25, "일정한 시간에 자고 일어나는 규칙적인 생활이 중요하다고 생각한다.", QuestionCategory.MORNING_TYPE),
        Question(26, "여행 시 시차 적응에 어려움을 겪는 편이다.", QuestionCategory.RHYTHM_SENSITIVITY),
        Question(27, "계절 변화(특히 겨울철 일조량 감소)에 민감하게 반응한다.", QuestionCategory.RHYTHM_SENSITIVITY),
        Question(28, "카페인이 수면에 미치는 영향이 크다고 느낀다.", QuestionCategory.RHYTHM_SENSITIVITY),
        Question(29, "자연의 일출과 일몰 시간에 맞춰 생활하는 것이 이상적이라고 생각한다.", QuestionCategory.MORNING_TYPE)
    )
}

// SurveyCalculator.kt
object SurveyCronotypeCalculator {
    fun calculateResult(answers: Map<Int, Int>): SurveyResult {
        val morningTypeQuestions = listOf(0, 1, 2, 3, 8, 9, 12, 14, 16, 19, 20, 21, 22, 25, 29)
        val eveningTypeQuestions = listOf(4, 5, 7, 11, 13, 15, 17, 23, 24)
        val sensitivityQuestions = listOf(6, 10, 18, 26, 27, 28)

        val morningScore = morningTypeQuestions.sumOf { answers[it] ?: 0 }
        val eveningScore = eveningTypeQuestions.sumOf { answers[it] ?: 0 }
        val sensitivityScore = sensitivityQuestions.sumOf { answers[it] ?: 0 }

        val morningNormalizedScore = (morningScore.toDouble() / 75) * 100
        val eveningNormalizedScore = (eveningScore.toDouble() / 45) * 100

        val chronotypeIndex = morningNormalizedScore - eveningNormalizedScore
        val chronotypeType = when {
            chronotypeIndex >= 30 -> CronoTimeSchedule.definiteMorningType
            chronotypeIndex in 10.0..29.99 -> CronoTimeSchedule.moderateMorningType
            chronotypeIndex in -9.99..9.99 -> CronoTimeSchedule.intermediateType
            chronotypeIndex in -29.99..-10.0 -> CronoTimeSchedule.moderateEveningType
            else -> CronoTimeSchedule.definiteEveningType
        }

        return SurveyResult(
            morningScore = morningScore,
            eveningScore = eveningScore,
            sensitivityScore = sensitivityScore,
            morningNormalizedScore = morningNormalizedScore,
            eveningNormalizedScore = eveningNormalizedScore,
            chronotypeIndex = chronotypeIndex,
            chronoType = chronotypeType
        )
    }

    fun getSensitivityLevel(sensitivityScore: Int): String {
        return when {
            sensitivityScore in 6..12 -> "낮은 민감도 - 수면 패턴 변화, 시차, 계절 변화에 적응력이 높음"
            sensitivityScore in 13..19 -> "중간 민감도 - 적당한 수준의 적응력, 큰 변화에는 적응 시간 필요"
            else -> "높은 민감도 - 수면 패턴 변화, 시차, 계절 변화에 민감하게 반응, 규칙적인 생활 패턴 유지가 중요"
        }
    }
}
