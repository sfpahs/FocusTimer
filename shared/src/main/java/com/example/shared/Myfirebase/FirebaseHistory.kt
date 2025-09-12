package com.example.shared.Myfirebase

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.shared.model.CronoTime
import com.example.shared.model.HistoryData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine



@RequiresApi(Build.VERSION_CODES.O)
fun saveHistoryData(saveData : HistoryData){
    val historyRef = MyFireBase.getDayRef().push()
    historyRef.setValue(saveData)
        .addOnSuccessListener { Log.i("firebase", "saveHistoryData: save Success") }
        .addOnFailureListener {e -> Log.e("firebaseError", "saveHistoryData: ${e.message}") }
    updateTodayHistoryData(saveData)
}

@SuppressLint("NewApi")
suspend fun loadDayHistoryData(date : LocalDate) : List<HistoryData>{
    return suspendCoroutine { continuation ->
        val dataList = mutableListOf<HistoryData>()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val fomattedDate = date.format(formatter)

        MyFireBase.getUserRef()
            .child("history")
            .child(fomattedDate)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    try{
                        dataList.clear()
                        for (childSnapshot in snapshot.children) {
                            if (childSnapshot.key != "totalData") {
                                val data = childSnapshot.getValue(HistoryData::class.java)
                                if (data != null && data.category >= 0) {
                                    dataList.add(data)
                                }
                            }
                        }
                        if(dataList.isNotEmpty())
                            continuation.resume(dataList)
                        else continuation.resume(emptyList())
                    }catch (e : Exception){
                        continuation.resume(dataList)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("firebase", "onCancelled: ${error.message}")
                    continuation.resume(emptyList())
                }
            })
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun updateTodayHistoryData(data: HistoryData){
    val historyRef = MyFireBase.getDayRef()
        .child("totalData")
        .child("${data.category}")

    var loadData : Pair<Int, Int> = Pair(0,0)
    loadTodayHistoryData(historyRef) { x ->
        loadData = x
        val saveData = Pair(loadData.first + data.workingMinute, loadData.second + data.restMinute)
        historyRef.setValue(saveData)
            .addOnSuccessListener { Log.i("firebase", "saveHistoryData: save Success") }
            .addOnFailureListener { e -> Log.e("firebaseError", "saveHistoryData: ${e.message}") }
    }
}

//todo 왜사용하는지 모르겠음 나중에 loadDayHistoryData로 옮길것
fun loadTodayHistoryData(historyRef : DatabaseReference, callback : (Pair<Int, Int>) -> Unit){
    var saveData = Pair(0,0)
    historyRef.get().addOnCompleteListener { task ->
        if (task.isSuccessful) {
            val snapshot = task.result
            if (snapshot.exists()) {
                val data = snapshot.value as Map<*, *>
                val workingMinute = data.get("first") as? Long ?: 0
                val restMinute = data.get("second") as? Long ?: 0
                Log.i(
                    "firebase",
                    "loadTodayHistoryData: data - $data work - $workingMinute, rest - $restMinute"
                )
                saveData = Pair(workingMinute.toInt(), restMinute.toInt())
                callback(saveData)
            } else {
                //println("No data available")
                callback(saveData)
            }
        } else {
            println("Error getting data: ${task.exception}")
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun loadWeekHistoryData(
    startMonday: LocalDateTime,
    callback: (List<List<Pair<Int, Int>>>) -> Unit
) {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val date = startMonday.format(formatter)
    val weekDates = calcWeekDate(date)

    // 요일별 데이터를 저장할 맵 (인덱스를 키로 사용)
    val weekDataMap = mutableMapOf<Int, List<Pair<Int, Int>>>()
    var completedRequests = 0

    for (i in weekDates.indices) {
        val weekDate = weekDates[i]
        val dateHistoryRef = MyFireBase
            .getUserRef()
            .child("history")
            .child(weekDate)
            .child("totalData")

        loadDateHistoryData(dateHistoryRef) { data ->
            weekDataMap[i] = data
            completedRequests++

            if (completedRequests == weekDates.size) {
                // 모든 요청이 완료되면 인덱스 순으로 정렬된 리스트 생성
                val sortedWeekData = (0 until weekDates.size).map {
                    weekDataMap[it] ?: listOf(Pair(0, 0))
                }
                callback(sortedWeekData)
            }
        }
    }
}

//todo history 페이지에서 사용중 다른 함수로 가공할 것
fun loadDateHistoryData(historyRef: DatabaseReference, callback: (List<Pair<Int, Int>>) -> Unit) {
    val saveData = mutableListOf<Pair<Int, Int>>()
    historyRef.get().addOnCompleteListener { task ->
        if (task.isSuccessful) {
            val snapshot = task.result
            if (snapshot.exists()) {
                val data = snapshot.value
                if (data is Map<*, *>) {
                    val entries = data.entries
                    for (entry in entries) {
                        val value = entry.value as Map<*, *>
                        val workingMinute = value.get("first") as? Long ?: 1
                        val restMinute = value.get("second") as? Long ?: 1
                        saveData.add(Pair(workingMinute.toInt(), restMinute.toInt()))
                    }
                } else if (data is List<*>) {
                    for (item in data) {
                        if (item is Map<*, *>) {
                            val workingMinute = item.get("first") as? Long ?: 1
                            val restMinute = item.get("second") as? Long ?: 1
                            saveData.add(Pair(workingMinute.toInt(), restMinute.toInt()))
                        }
                    }
                }
                callback(saveData)
            } else {
                saveData.add(Pair(1, 1))
                //println("No data available")
                callback(saveData)
            }
        } else {
            saveData.add(Pair(1, 1))
            Log.e("firebaseError", "loadDateHistoryData: ${task.exception?.message}")
            callback(saveData)
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun calcWeekDate(inputDate: String): List<String> {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val date = LocalDate.parse(inputDate, formatter)

    val startOfWeek = date.minusDays(date.dayOfWeek.value.toLong() - 1)

    val weekDates = mutableListOf<String>()
    for (i in 0 until 7) {
        weekDates.add((startOfWeek.plusDays(i.toLong())).format(formatter))
    }
    return weekDates
}

fun saveSurveyData(x : CronoTime){
    val ref = MyFireBase
        .getUserRef()
        .child("info")
    ref.setValue(x)
        .addOnSuccessListener {
            Log.d("FireBase", "saveSurveyData: 저장완료")
        }
        .addOnFailureListener {e ->
            Log.e("FireBase", "saveSurveyData: 저장실패${e.message}", )
        }

}
