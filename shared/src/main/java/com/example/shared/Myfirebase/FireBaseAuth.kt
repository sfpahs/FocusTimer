package com.example.shared.Myfirebase

import android.content.Context
import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException

fun signInWithEmailAndPassword(email: String, password: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
    val auth = FirebaseAuth.getInstance()

    auth.signInWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                onSuccess()
            } else {
                // 로그인 실패
                Log.e("firebaseError", "signInWithEmailAndPassword: ${task.exception}",)
                when (task.exception){
                    is FirebaseAuthInvalidUserException -> onError("존재하지 않는 계정입니다.")
                    is FirebaseAuthInvalidCredentialsException -> onError("이메일 또는 비밀번호가 올바르지 않습니다.")
                    else -> onError("기타오류입니다")
                }
            }
        }
}

fun signUp(context : Context, name : String, email: String, password: String, onSuccessCallback: () -> Unit, onFailCallback : (Task<AuthResult>) -> Unit){
    var auth = FirebaseAuth.getInstance()
    auth.createUserWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.i("firebase", "signUp: success")
                    val user = auth.currentUser
                user?.let { saveDefaultUserSettingFireBase(name, context = context) }
                Log.d("login", "signUp: ${user?.uid}")
                onSuccessCallback()
            } else {
                Log.e("firebaseError", "signUp: ${task.exception}",)
                onFailCallback(task)
            }
        }
}

fun checkEmailExists(email: String, callback: (Boolean) -> Unit) {
    val auth = FirebaseAuth.getInstance()

    auth.fetchSignInMethodsForEmail(email)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val signInMethods = task.result?.signInMethods ?: emptyList()
                callback(signInMethods.isNotEmpty()) // true면 이미 존재하는 이메일
            } else {
                callback(false) // 오류 발생 시 false 반환
            }
        }
}

fun loadUserName(onComplete: (String?) -> Unit) {
    val userRef = MyFireBase.getUserRef()
    userRef.child("name").get()
        .addOnSuccessListener { dataSnapshot ->
            val name = dataSnapshot.value as? String
            onComplete(name)
            //Log.i("firebase", "loadUserName: 성공적으로 이름을 로드했습니다. ${name}")
        }
        .addOnFailureListener { e ->
            Log.e("firebaseError", "loadUserName: ${e.message}")
            onComplete(null)
        }
}

fun logOut(context : Context){
    // 1. Firebase 로그아웃
    FirebaseAuth.getInstance().signOut()

    // 2. 로컬 데이터 초기화
    clearLocalData(context)

    // 로그 출력
    println("로그아웃 완료")
}