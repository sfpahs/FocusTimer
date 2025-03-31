package com.example.focustimer.model

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.focustimer.loadTimerSettingsFireBase
import com.example.pre_capstone.model.HistoryData
import com.example.pre_capstone.model.TimerSetting
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TimerViewModel : ViewModel() {
    private val _timerSettings = MutableStateFlow<List<TimerSetting>>(emptyList())
    val timerSettings: StateFlow<List<TimerSetting>> = _timerSettings
    private val _timerInfo = MutableStateFlow(HistoryData())
    val timerInfo: StateFlow<HistoryData> = _timerInfo
    private val _activateTimer = MutableStateFlow(0)
    val activateTimer: StateFlow<Int> = _activateTimer
    private val database = FirebaseDatabase.getInstance().reference

    private val _currentTimerSetting = MutableStateFlow(TimerSetting())
    val currentTimerSetting: StateFlow<TimerSetting> = _currentTimerSetting



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
    //유저 타이머 설정들

    //현재 타이머 정보
    fun loadTimerSettings(userId: String) {
        viewModelScope.launch {
            loadTimerSettingsFireBase(uid = userId,
                onSuccess = { settings ->
                    _timerSettings.value = settings
                    Log.i("firebase", "loadTimerSettings: ${settings}")
                },
                onFailure = { e ->
                    Log.e("firebaseError", "loadTimerSettings: ${e.message}",)
                })

        }
    }
    fun saveTimerSettings(userId: String, settings: List<TimerSetting>) {
        viewModelScope.launch {
            val updates = settings.associateBy { it.index.toString() }
            database.child("users").child(userId).child("settings").child("timer")
                .setValue(updates)
                .addOnSuccessListener {
                    _timerSettings.value = settings // 로컬 상태 업데이트
                }
                .addOnFailureListener { exception ->
                    Log.e("viewModelRoutin", "saveTimerSettings: ${exception.message}",)
                    // 실패 처리 (로그 출력 등)
                }
        }
    }


    fun updateTimerSetting ( newTimerSettings: List<TimerSetting> ){
        _timerSettings.value = newTimerSettings
    }
    fun updateCurrentTimer(newTimerSetting: TimerSetting){
        _currentTimerSetting.value = newTimerSetting
    }
    fun updateActiveTimer(newActiveTimer : Int){
        _activateTimer.value = newActiveTimer
    }







}

