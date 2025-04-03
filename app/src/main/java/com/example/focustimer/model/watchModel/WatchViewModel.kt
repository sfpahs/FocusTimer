package com.example.focustimer.watchModel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.focustimer.model.TimerSetting
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class WatchViewModel : ViewModel() {

    private val _setting = MutableStateFlow(TimerSetting())
    val setting : StateFlow<TimerSetting> = _setting
    private val _activeTimer = MutableStateFlow(0)
    val activeTimer : StateFlow<Int> = _activeTimer
    private val _time = MutableStateFlow(0)
    val time : StateFlow<Int> = _time

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
    fun updateWatchInfo(newData : WatchData){

        _time.value = newData.time
        _activeTimer.value = newData.activeTimer
        _setting.value = newData.timerSetting
    }
    fun setActiveTimer(value : Int){
        _activeTimer.value = value
        //_watchInfo.value.activeTimer = if(_watchInfo.value.activeTimer == 1) 2 else 1
    }
    fun increaceTimer(){
        _time.value = time.value + 1
        Log.d("TAG", "increaceTimer: ${time.value}")
    }
    fun resetTimer(){
        _time.value = 0
    }

}