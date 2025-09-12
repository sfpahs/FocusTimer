package com.example.focustimer.test

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.shared.Myfirebase.MyFireBase
import com.example.shared.model.DateTimeWrapper
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import java.time.LocalDateTime


@RequiresApi(Build.VERSION_CODES.O)
fun saveTodosFirebase(todoDocument: TodoDocument){
    val dbRef = MyFireBase.getUserRef()
        .child("todos")

    // lastUpdate를 현재 시간으로 갱신
    val now = DateTimeWrapper(LocalDateTime.now())
    val dataMap = mapOf(
        "lastUpdate" to now,
        "items" to todoDocument.items
    )

    dbRef.setValue(dataMap)
        .addOnSuccessListener {
            Log.i("firebase", "Todos saved successfully")
        }
        .addOnFailureListener { e ->
            Log.e("firebaseError", "Failed to save todos: ${e.message}")
        }

}
fun loadTodosFirebase(callback: (TodoDocument?) -> Unit) {
    val dbRef = MyFireBase.getUserRef()
        .child("todos")

    dbRef.addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            if (snapshot.exists()) {
                val lastUpdateSnapshot = snapshot.child("lastUpdate")
                val lastUpdateWrapper = lastUpdateSnapshot.getValue(DateTimeWrapper::class.java) ?: DateTimeWrapper()

                val itemsSnapshot = snapshot.child("items")
                val items = mutableListOf<TodoItem>()
                for (itemSnap in itemsSnapshot.children) {
                    val id = itemSnap.child("id").getValue(Int::class.java) ?: 0
                    val text = itemSnap.child("text").getValue(String::class.java) ?: ""
                    val isChecked = itemSnap.child("isChecked").getValue(Boolean::class.java) ?: false
                    items.add(TodoItem(id, text, isChecked))
                }
                callback(TodoDocument(lastUpdateWrapper, items))
            } else {
                callback(null)
            }
        }

        override fun onCancelled(error: DatabaseError) {
            Log.e("firebaseError", "Failed to load todos: ${error.message}")
            callback(null)
        }
    })
}
fun checkTodoFirebase(id : Int){
    val dbRef = MyFireBase.getUserRef()
        .child("todos")
        .child("items")
        .child(id.toString())
        .child("isChecked")

    dbRef.setValue(true)
        .addOnSuccessListener {
            Log.i("firebase", "Todo isChecked set to true successfully")
        }
        .addOnFailureListener { e ->
            Log.e("firebaseError", "Failed to set isChecked: ${e.message}")
        }
}
fun deleteTodosFirebase(){
    val dbRef = MyFireBase.getUserRef()
        .child("todos")

    dbRef.removeValue()
        .addOnSuccessListener {
            Log.i("firebase", "Todos deleted successfully")
        }
        .addOnFailureListener { e ->
            Log.e("firebaseError", "Failed to delete todos: ${e.message}")
        }
}