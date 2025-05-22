package com.example.shared.model

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shared.Myfirebase.loadTimerSettingsFireBase
import com.example.shared.Myfirebase.updateTimerSetting
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TimerViewModel : ViewModel() {
    private val database = FirebaseDatabase.getInstance().reference

    private val _timerSettings = MutableStateFlow<List<TimerSetting>>(emptyList())
    val timerSettings: StateFlow<List<TimerSetting>> = _timerSettings

    private val _currentSetting = MutableStateFlow(TimerSetting())
    val currentSetting : StateFlow<TimerSetting> = _currentSetting

    private val _activeTimer = MutableStateFlow(0)
    val activeTimer : StateFlow<Int> = _activeTimer

    private val _time = MutableStateFlow(0)
    val time : StateFlow<Int> = _time

    //todo 데모용 나중에 지울예정
    private val _mul = MutableStateFlow(1)
    val mul : StateFlow<Int> = _mul
    fun updateMul(){
        if(mul.value ==1)
        _mul.value = 60
        else _mul.value = 1
    }


    //현재 타이머 정보
    fun loadTimerSettings() {
        viewModelScope.launch {
            loadTimerSettingsFireBase(
                onSuccess = { settings ->
                    _timerSettings.value = settings
                    Log.i("firebase", "loadTimerSettings: ${settings}")
                },
                onFailure = { e ->
                    Log.e("firebaseError", "loadTimerSettings: ${e.message}",)
                })
        }
    }

    fun editTimerSetting(newSetting : TimerSetting){
        viewModelScope.launch {
            updateTimerSetting(
                id = newSetting.category,
                newSetting = newSetting,
                )
        }
    }

    fun setTimer(newData : Timer){
        _time.value = newData.time
        _activeTimer.value = newData.activeTimer
        _currentSetting.value = newData.timerSetting
    }
    fun setActiveTimer(value : Int){
        _activeTimer.value = value
        //_watchInfo.value.activeTimer = if(_watchInfo.value.activeTimer == 1) 2 else 1
    }
    fun increaseTimer(second : Int = 1){
        _time.value += mul.value
    }
    fun resetTimer(){
        _time.value = 0
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