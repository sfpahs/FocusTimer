package com.example.shared.watchModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shared.loadTimerSettingsFireBase
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class WatchViewModel : ViewModel() {
    companion object {
        // 싱글톤 인스턴스
        private var instance: WatchViewModel? = null
        // 인스턴스 가져오기
        fun getInstance(): WatchViewModel {
            if (instance == null) {
                instance = WatchViewModel()
            }
            return instance!!
        }
    }

    private val _timerSettings = MutableStateFlow<List<TimerSetting>>(emptyList())
    val timerSettings: StateFlow<List<TimerSetting>> = _timerSettings
    private val _setting = MutableStateFlow(TimerSetting())
    val setting : StateFlow<TimerSetting> = _setting

    private val _activeTimer = MutableStateFlow(0)
    val activeTimer : StateFlow<Int> = _activeTimer
    private val _time = MutableStateFlow(0)
    val time : StateFlow<Int> = _time
    private val database = FirebaseDatabase.getInstance().reference


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
            val updates = settings.associateBy { it.category.toString() }
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

    fun updateWatchInfo(newData : WatchData){

        _time.value = newData.time
        _activeTimer.value = newData.activeTimer
        _setting.value = newData.timerSetting
    }
    fun setActiveTimer(value : Int){
        _activeTimer.value = value
        //_watchInfo.value.activeTimer = if(_watchInfo.value.activeTimer == 1) 2 else 1
    }
    fun increaseTimer(second : Int = 1){
        _time.value += second
    }
    fun resetTimer(){
        _time.value = 0
    }

}