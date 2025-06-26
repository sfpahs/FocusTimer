package com.example.focustimer.test

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import co.yml.charts.common.extensions.isNotNull
import com.example.shared.model.DateTimeWrapper
import java.time.LocalDate
import java.time.LocalDateTime

data class TodoItem(
    var id: Int = -1,
    val text: String,
    var isChecked: Boolean = false
)
data class TodoDocument(
    var lastUpdate : DateTimeWrapper = DateTimeWrapper(LocalDateTime.MIN),
    var items: List<TodoItem> = emptyList()
)
class TodoListViewModel : ViewModel() {
    // Compose가 감지할 수 있도록 mutableStateOf로 상태 관리
    var todoDocument by mutableStateOf(TodoDocument())
        private set

    // 할 일 추가
    fun saveTodo(todoItem: TodoItem) {
        todoItem.id = (todoDocument.items.maxOfOrNull { it.id } ?: -1) + 1
        todoDocument = todoDocument.copy(items = todoDocument.items + todoItem)
        saveTodosFirebase(todoDocument = todoDocument )
    }

    // Firestore에 업데이트(추가/수정/삭제)
    fun loadTodos() {
        loadTodosFirebase { data ->
            if (data != null) {
                todoDocument = data
            }
        }
    }
    // 특정 Todo의 체크 상태 변경
    fun checkTodo(id: Int) {
        //로컬 변경
        val updatedItems = todoDocument.items.map {
            if (it.id == id) it.copy(isChecked = true) else it
        }
        todoDocument = todoDocument.copy(items = updatedItems)
        // Firestore에 저장
        checkTodoFirebase(id = id)
    }

    fun deleteIfNotToday() {
        loadTodosFirebase { data ->
            if (data != null && data.lastUpdate.dateTime != null) {
                val lastUpdateDate = LocalDate.parse(data.lastUpdate.dateTime!!.substring(0, 10))
                val todayDate = LocalDate.now()
                if (lastUpdateDate != todayDate) {
                    deleteTodosFirebase()
                }
            }
        }
    }

}