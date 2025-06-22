package com.example.focustimer.test

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

data class TodoItem(
    val id: Int,
    val text: String,
    val isChecked: Boolean = false
)


@Composable
fun TodoListSection(
    todoItems: List<TodoItem>,
    onAddTodo: (String) -> Unit,
    onCheckTodo: (Int, Boolean) -> Unit
) {
    var todoText by remember { mutableStateOf("") }
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = todoText,
                onValueChange = { todoText = it },
                modifier = Modifier.weight(1f),
                singleLine = true,
                placeholder = { Text("할 일을 입력하세요") }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = {
                    if (todoText.isNotBlank()) {
                        onAddTodo(todoText)
                        todoText = ""
                    }
                }
            ) {
                Text("+")
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        // Todo 리스트 표시
        todoItems.forEach { item ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Checkbox(
                    checked = item.isChecked,
                    onCheckedChange = { checked -> onCheckTodo(item.id, checked) }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = item.text,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Preview
@Composable
fun PreviewHistoryWithTodoList() {
    // Todo 리스트 예시 데이터
    val sampleTodos = listOf(
        TodoItem(id = 0, text = "아침 회의 참석", isChecked = true),
        TodoItem(id = 1, text = "업무 메일 확인", isChecked = false),
        TodoItem(id = 2, text = "코드 리뷰", isChecked = false),
        TodoItem(id = 3, text = "점심 식사", isChecked = false)
    )

    // 실제 화면 호출
    TodoListSection(
        todoItems = sampleTodos,
        onAddTodo = {},
        onCheckTodo = { _, _ -> },
    )
}




