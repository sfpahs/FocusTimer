package com.example.shared.Myfirebase

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.shared.R
import com.example.shared.model.CronoTime
import com.example.shared.model.TimerSetting
import com.example.shared.model.toMap
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class MyFireBase(){

    companion object{
        private val _dataBase = Firebase.database
        @RequiresApi(Build.VERSION_CODES.O)
        fun getTodayRef() : DatabaseReference{
            val currentTime = LocalDateTime.now(ZoneId.of("Asia/Seoul"))
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val formattedDate = currentTime.format(formatter)
            //todo 이후 유저 로그인시 유저이름으로 패스정하기
            val historyRef = getDataBase()
                .child("history")
                .child(formattedDate)
            return historyRef
        }

        fun getDataBase() : DatabaseReference{
            val user = FirebaseAuth.getInstance().currentUser
             return user!!.uid.let { _dataBase.getReference("users").child(it) }
        }
    }
}


fun saveDefaultUserSettingFireBase(name : String, context: Context) {

    val userRef  = MyFireBase.getDataBase()
    val timerSettingsRef = userRef
        .child("timersettings")
       // .child("timer")

    val timerSettings = listOf(
        TimerSetting(
            0,
            "암기",
            context.getColor(R.color.myCategory1).toLong(),
            50 * 60,
            10 * 60
        ),
        TimerSetting(
            1,
            "연산",
            context.getColor(R.color.myCategory2).toLong(),
            3 * 60,
            1 * 60
        ),
        TimerSetting(
            2,
            "이해",
            context.getColor(R.color.myCategory3).toLong(),
            55 * 60,
            5 * 60
        )
    )

    val updates = HashMap<String, Any>()
    timerSettings.forEach { setting ->
        updates["${setting.category}"] = setting.toMap()
    }

    userRef.child("name").setValue(name)
    timerSettingsRef.updateChildren(updates)
        .addOnSuccessListener {
            Log.i("firebase", "saveDefaultUserSettingFireBase: saveName")

        }
        .addOnFailureListener { e ->
            Log.e("firebaseError", "saveDefaultUserSettingFireBase: ${e.message}", )
        }
}

fun loadTimerSettingsFireBase(uid: String, onSuccess: (List<TimerSetting>) -> Unit, onFailure: (Exception) -> Unit) {
    Firebase.database.reference
        .child("users")
        .child(uid)
        .child("timersettings")
        //.child("timer")
        .get()
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val result = task.result
                Log.i("firebase", "loadTimerSettingsFireBase: ${result}")
                if (result.exists()) {
                    val timerSettings = mutableListOf<TimerSetting>()
                    for (snapshot in result.children) {
                        val setting = snapshot.getValue(TimerSetting::class.java)
                        setting?.let { timerSettings.add(it) }
                    }
                    onSuccess(timerSettings)
                } else {
                    onSuccess(emptyList()) // 데이터가 없을 경우 빈 리스트 반환
                }
            } else {
                onFailure(task.exception ?: Exception("알 수 없는 오류"))
            }
        }
}

fun clearLocalData(context: Context) {
    // SharedPreferences 초기화
    val sharedPreferences = context.getSharedPreferences("APP_PREFERENCES", Context.MODE_PRIVATE)
    sharedPreferences.edit().clear().apply()
}
