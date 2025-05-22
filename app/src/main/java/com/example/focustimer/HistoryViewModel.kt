package com.example.focustimer

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.focustimer.Page.Event
import com.example.shared.Myfirebase.loadDayHistoryData
import com.example.shared.model.TimerViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate


class HistoryViewModel : ViewModel() {
    private val _events = MutableStateFlow<List<Event>>(emptyList())
    val events: StateFlow<List<Event>> = _events
    val viewModel : TimerViewModel by lazy { TimerViewModel.getInstance() }

    companion object {
        // 싱글톤 인스턴스
        private var instance: HistoryViewModel? = null
        // 인스턴스 가져오기
        fun getInstance(): HistoryViewModel {
            if (instance == null) {
                instance = HistoryViewModel()
            }
            return instance!!
        }
    }

    // 이벤트 로드 함수
    fun loadEventsForDates(dates: List<LocalDate>) {
        viewModelScope.launch {
            try {
                val loadedEvents = loadEvent(dates)
                _events.value = loadedEvents
            } catch (e: Exception) {
                Log.e("MyViewModel", "이벤트 로드 중 오류 발생", e)
                _events.value = emptyList()
            }
        }
    }


    suspend fun loadEvent(dates : List<LocalDate>) : List<Event>{
        viewModel.loadTimerSettings()
        val timerSettings = viewModel.timerSettings.value
        var list = mutableListOf<Event>()

        var id = 1
        dates.forEach { date ->
            val historys = loadDayHistoryData(date = date)
            historys.forEach { data ->
                list.add(
                    Event(
                        id = id++,
                        title = timerSettings.get(data.category).name,
                        date = date,
                        startHour = data.startTime.toLocalDateTime().hour,
                        startMinute = data.startTime.toLocalDateTime().minute,
                        durationMinutes = data.totalMinute,
                        category = data.category
                    )
                )
            }
        }
        return list
    }
}