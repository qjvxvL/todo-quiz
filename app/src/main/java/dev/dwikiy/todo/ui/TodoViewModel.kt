// file: ui/TodoViewModel.kt
package dev.dwikiy.todo.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dev.dwikiy.todo.data.DateFmt
import dev.dwikiy.todo.data.Todo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.*

class TodoViewModel : ViewModel() {
    private val db = Firebase.firestore
    private val todosCollection = db.collection("todos")

    private val _todos = MutableStateFlow<List<Todo>>(emptyList())
    val todos: StateFlow<List<Todo>> = _todos

    init {
        listenToTodos()
    }

    private fun listenToTodos() {
        todosCollection.addSnapshotListener { snapshot, error ->
            if (error != null) {
                return@addSnapshotListener
            }
            val list = snapshot?.documents?.mapNotNull { doc ->
                Todo.fromMap(doc.id, doc.data ?: emptyMap())
            } ?: emptyList()
            _todos.value = list
        }
    }

    fun addTodo(title: String, dueDate: Date) {
        viewModelScope.launch {
            val newTodo = Todo(
                title = title,
                dueAt = dueDate,
                done = false,
                createdAt = Date()
            )
            todosCollection.add(newTodo.toMap())
        }
    }

    fun toggleDone(todoId: String) {
        viewModelScope.launch {
            val todo = _todos.value.find { it.id == todoId } ?: return@launch
            todosCollection.document(todoId)
                .update("done", !todo.done)
        }
    }

    fun deleteTodo(todoId: String) {
        viewModelScope.launch {
            todosCollection.document(todoId).delete()
        }
    }
}