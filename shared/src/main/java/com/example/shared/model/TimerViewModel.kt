package com.example.shared.model

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shared.Myfirebase.loadTimerSettingsFireBase
import com.example.shared.Myfirebase.setNewTimerSetting
import com.example.shared.Myfirebase.updateTimerSetting
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TimerViewModel : ViewModel() {
    private val _subjects = MutableStateFlow<List<MySubject>>(emptyList())
    val subjects: StateFlow<List<MySubject>> = _subjects

    private val _currentMySubject = MutableStateFlow(MySubject())
    val currentMySubject : StateFlow<MySubject> = _currentMySubject

    private val _activeTimer = MutableStateFlow(0)
    val activeTimer : StateFlow<Int> = _activeTimer

    private val _timerOptioin  = MutableStateFlow(TimerOption())
    val timerOption : StateFlow<TimerOption> = _timerOptioin

    private val _time = MutableStateFlow(0)
    val time : StateFlow<Int> = _time

    private val _stopwatchRunning = MutableStateFlow(false)
    val stopwatchRunning: StateFlow<Boolean> = _stopwatchRunning

    // 타이머가 정지된 상태인지 (초기 상태, 서비스 종료 상태)
    private val _isStopped = MutableStateFlow(true)
    val isStopped: StateFlow<Boolean> = _isStopped

    private val _isTextVisible = MutableStateFlow(true)
    val isTextVisible: StateFlow<Boolean> = _isTextVisible

    var description : String = ""
    //todo 데모용 나중에 지울예정
    private val _mul = MutableStateFlow(1)
    val mul : StateFlow<Int> = _mul

    fun updateMul(){
        if(mul.value ==1)
        _mul.value = 60
        else _mul.value = 1
    }

    //현재 타이머 정보
    fun loadSubjects() {
        viewModelScope.launch {
            loadTimerSettingsFireBase(
                onSuccess = { settings ->
                    _subjects.value = settings
                    Log.i("firebase", "loadTimerSettings: ${settings}")
                },
                onFailure = { e ->
                    Log.e("firebaseError", "loadTimerSettings: ${e.message}",)
                })
        }
    }

    fun editSubject(newSetting : MySubject){

        viewModelScope.launch {
            updateTimerSetting(
                id = newSetting.id,
                newSetting = newSetting,
                )
        }
    }

    fun addSubject(newSetting : MySubject){
        //리스트에 추가
        newSetting.id = _subjects.value.size
        _subjects.value += newSetting
        Log.i("ViewModel", "addSubject: ${newSetting.id}")
        Log.i("ViewModel", "addSubject: ${_subjects.value}")

        //새 id 부여
        viewModelScope.launch {
            updateTimerSetting(
                id = newSetting.id,
                newSetting = newSetting,
            )
        }
    }
    fun delectSubject(id : Int){
        //id삭제
        _subjects.value = _subjects.value.filter { it.id != id }
        //id outo increase 부여
        _subjects.value = _subjects.value.mapIndexed { index, subject ->
            subject.apply { this.id = index }
        }
        Log.i("ViewModel", "delectSubject: $id")
        Log.i("ViewModel", "delectSubject: ${_subjects.value}")
        viewModelScope.launch {
            setNewTimerSetting(subjects = subjects.value)
        }
    }
    fun setOption(option: TimerOption){
        _timerOptioin.value = option;
    }
    // 서비스 시작 시 호출
    fun startTimer() {
        _stopwatchRunning.value = true
        _isStopped.value = false
    }

    // 서비스 정지 시 호출
    fun stopTimer() {
        _stopwatchRunning.value = false
        _isStopped.value = true
    }



    fun setTimer(newData : Timer){
        description = ""
        _time.value = newData.time
        _isStopped.value = true
        _stopwatchRunning.value = false
        _isTextVisible.value = true
        _activeTimer.value = newData.activeTimer
        _currentMySubject.value = newData.MySubject
        val subject = newData.MySubject
        if(newData.MySubject.selectedTimer != -1)
            _timerOptioin.value = TimerOptions.list[subject.selectedTimer]
        else
            _timerOptioin.value = TimerOptions.list[subject.recomendTimer]
    }

    fun setActiveTimer(value : Int){
        Log.i("ViewModel", "setActiveTimer: ${activeTimer.value}")
        _activeTimer.value = value
        //_watchInfo.value.activeTimer = if(_watchInfo.value.activeTimer == 1) 2 else 1
    }

    fun increaseTimer(second : Int = 1){
        _time.value += mul.value
    }

    fun resetTimer(){
        _time.value = 0
    }

    fun setTextVisible(visible : Boolean){
        _isTextVisible.value = visible
    }
    companion object {
        // 싱글톤 인스턴스
        private var instance: TimerViewModel? = null
        // 인스턴스 가져오기
        fun getInstance(): TimerViewModel {
            if (instance == null) {
                instance = TimerViewModel()
            }
            return instance!!
        }
    }

}