package com.example.focustimer.survery

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

// SurveyViewModel.kt
class SurveyViewModel : ViewModel() {
    private val _answers = MutableStateFlow<MutableMap<Int, Int>>(mutableMapOf())
    val answers: StateFlow<Map<Int, Int>> = _answers.asStateFlow()

    private val _surveyCompleted = MutableStateFlow(false)
    val surveyCompleted: StateFlow<Boolean> = _surveyCompleted.asStateFlow()

    private val _surveyResult = MutableStateFlow<SurveyResult?>(null)
    val surveyResult: StateFlow<SurveyResult?> = _surveyResult.asStateFlow()

    fun updateAnswer(questionIndex: Int, score: Int) {
        val updatedAnswers = _answers.value.toMutableMap()
        updatedAnswers[questionIndex] = score
        _answers.value = updatedAnswers
    }

    fun completeSurvey() {
        if (_answers.value.size == SurveyData.questions.size) {
            _surveyResult.value = SurveyCalculator.calculateResult(_answers.value)
            _surveyCompleted.value = true
        }
    }

    fun isSurveyComplete(): Boolean {
        return _answers.value.size == SurveyData.questions.size
    }
}
