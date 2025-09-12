package com.example.focustimer.test

import android.util.Log
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun TodoListSection(
) {
    val todoListViewModel : TodoListViewModel = viewModel()
    LaunchedEffect(Unit) {
        todoListViewModel.loadTodos()
    }
    val list = todoListViewModel.todoDocument.items

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
                        todoListViewModel.saveTodo(todoItem = TodoItem(text = todoText))
                        todoText = ""
                    }
                }
            ) {
                Text("+")
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        // Todo 리스트 표시
        list.forEach { item ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Checkbox(
                    checked = item.isChecked,
                    onCheckedChange = { checked -> todoListViewModel.checkTodo(item.id)
                        Log.i("todos", "TodoListSection: ${list}")}
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

    // 실제 화면 호출
    TodoListSection()
}




