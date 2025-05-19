package com.example.shared.model

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.shared.Myfirebase.MyFireBase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class CronoTimeViewModel : ViewModel() {

    // LiveData를 사용하여 UI에 데이터 노출
    private val _surveyData = MutableStateFlow(CronoTime())
    val surveyData : StateFlow<CronoTime> = _surveyData


    fun loadSurveyData() {

        val ref = MyFireBase
            .getDataBase()
            .child("info")

        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    val cronoTime = snapshot.getValue(CronoTime::class.java)
                    if (cronoTime != null) {
                        _surveyData.value = cronoTime
                        Log.d("FireBase", "loadSurveyData: 로드완료")
                    } else {
                        Log.e("FireBase", "loadSurveyData: 데이터 없음")
                    }
                } catch (e: Exception) {
                    Log.e("FireBase", "loadSurveyData: 데이터 파싱 오류", e)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FireBase", "loadSurveyData: 로드실패 ${error.message}")
            }
        })
    }


    companion object {
        // 싱글톤 인스턴스
        private var instance: CronoTimeViewModel? = null
        // 인스턴스 가져오기
        fun getInstance(): CronoTimeViewModel {
            if (instance == null) {
                instance = CronoTimeViewModel()
            }
            return instance!!
        }
    }

}
