package com.example.focustimer.watchModel

import androidx.lifecycle.ViewModel
import com.example.focustimer.model.AppTimerViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class WatchViewModel : ViewModel() {
    private val _watchInfo = MutableStateFlow(WatchData())
    val watchInfo: StateFlow<WatchData> = _watchInfo
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
        _watchInfo.value = newData
    }



}