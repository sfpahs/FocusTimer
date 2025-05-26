package com.example.shared.Myfirebase

import android.content.Context
import android.util.Log
import com.example.shared.R
import com.example.shared.model.subject
import com.example.shared.model.toMap

fun saveDefaultUserSettingFireBase(name : String, context: Context) {
    val updates = HashMap<String, Any>()
    val subjects = listOf(
        subject(
            0,
            "암기",
            context.getColor(R.color.myCategory1).toLong(),
            recomendTimer = 0,
        ),
        subject(
            1,
            "연산",
            context.getColor(R.color.myCategory2).toLong(),
            recomendTimer = 2,
        ),
        subject(
            2,
            "이해",
            context.getColor(R.color.myCategory3).toLong(),
            recomendTimer = 3,
        )
    )

    subjects.forEach { setting ->
        updates["${setting.id}"] = setting.toMap()
    }

    val userRef  = MyFireBase.getUserRef()
    val timerSettingsRef = userRef
        .child("timersettings")

    userRef.child("name")
        .setValue(name)

    timerSettingsRef
        .updateChildren(updates)
        .addOnSuccessListener {
            Log.i("firebase", "saveDefaultUserSettingFireBase: saveName")

        }
        .addOnFailureListener { e ->
            Log.e("firebaseError", "saveDefaultUserSettingFireBase: ${e.message}",)
        }
}

fun loadTimerSettingsFireBase(onSuccess: (List<subject>) -> Unit, onFailure: (Exception) -> Unit) {
    MyFireBase.getUserRef()
    .child("timersettings")

    .get()
    .addOnCompleteListener { task ->
        if (task.isSuccessful) {
            val result = task.result
            Log.i("firebase", "loadTimerSettingsFireBase: ${result}")
            if (result.exists()) {
                val subjects = mutableListOf<subject>()
                for (snapshot in result.children) {
                    val setting = snapshot.getValue(subject::class.java)
                    setting?.let { subjects.add(it) }
                }
                onSuccess(subjects)
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

fun updateTimerSetting(id : Int, newSetting: subject){
    val ref = MyFireBase.getUserRef()
        .child("timersettings")
        .child("$id")
    ref.setValue(newSetting)
        .addOnSuccessListener {
            Log.d("firebase", "updateTimerSetting: 과목업뎃 성공")
        }
        .addOnFailureListener { e ->
            Log.e("firebase", "updateTimerSetting: 실패 ${e.message}")
        }
}