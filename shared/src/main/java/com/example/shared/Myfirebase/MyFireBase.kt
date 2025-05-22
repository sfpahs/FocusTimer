package com.example.shared.Myfirebase

import android.os.Build
import androidx.annotation.RequiresApi
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

object MyFireBase{
    private val _dataBase = Firebase.database
    @RequiresApi(Build.VERSION_CODES.O)
    fun getDayRef(date : LocalDateTime = LocalDateTime.now(ZoneId.of("Asia/Seoul"))) : DatabaseReference{
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val formattedDate = date.format(formatter)
        val historyRef = getUserRef()
            .child("history")
            .child(formattedDate)
        return historyRef
    }

    fun getUserRef() : DatabaseReference{
        val user = FirebaseAuth.getInstance().currentUser
         return user!!.uid.let { _dataBase.getReference("users").child(it) }
    }
}


