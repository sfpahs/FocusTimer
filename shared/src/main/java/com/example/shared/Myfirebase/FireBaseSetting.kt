package com.example.shared.Myfirebase

import android.content.Context
import android.util.Log
import com.example.shared.R
import com.example.shared.model.MySubject
import com.example.shared.model.toMap

fun saveDefaultUserSettingFireBase(name : String, context: Context) {
    val updates = HashMap<String, Any>()
    val MySubjects = listOf(
        MySubject(
            0,
            "국어",
            context.getColor(R.color.myCategory1).toLong(),
            recomendTimer = 0,
        ),
        MySubject(
            1,
            "영어",
            context.getColor(R.color.myCategory2).toLong(),
            recomendTimer = 2,
        ),
        MySubject(
            2,
            "수학",
            context.getColor(R.color.myCategory3).toLong(),
            recomendTimer = 3,
        )
    )

    MySubjects.forEach { setting ->
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

fun loadTimerSettingsFireBase(onSuccess: (List<MySubject>) -> Unit, onFailure: (Exception) -> Unit) {
    MyFireBase.getUserRef()
    .child("timersettings")

    .get()
    .addOnCompleteListener { task ->
        if (task.isSuccessful) {
            val result = task.result
            Log.i("firebase", "loadTimerSettingsFireBase: ${result}")
            if (result.exists()) {
                val MySubjects = mutableListOf<MySubject>()
                for (snapshot in result.children) {
                    val setting = snapshot.getValue(MySubject::class.java)
                    setting?.let { MySubjects.add(it) }
                }
                onSuccess(MySubjects)
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

fun updateTimerSetting(id : Int, newSetting: MySubject){
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
fun setNewTimerSetting(subjects : List<MySubject>){
    val ref = MyFireBase.getUserRef().child("timersettings")
    // 1. 기존 데이터 삭제
    ref.setValue(null)
        .addOnSuccessListener {
            Log.d("firebase", "setNewTimerSetting: 기존 데이터 삭제 성공")
            // 2. 새 데이터 저장
            subjects.forEach { subject ->
                // subject.id가 고유값(예: Int)라고 가정
                ref.child(subject.id.toString()).setValue(subject)
                    .addOnSuccessListener {
                        Log.d("firebase", "${subject.id} 저장 성공")
                    }
                    .addOnFailureListener { e ->
                        Log.e("firebase", "${subject.id} 저장 실패: ${e.message}")
                    }
            }
        }
        .addOnFailureListener { e ->
            Log.e("firebase", "setNewTimerSetting: 기존 데이터 삭제 실패 ${e.message}")
        }
}
