package com.example.todoapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todoapp.data.TodoModel
import com.example.todoapp.enums.Priority
import com.example.todoapp.repository.TodoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TodoActivityViewModel(private val repository: TodoRepository) : ViewModel() {

    fun insertTodo(todo: TodoModel) = viewModelScope.launch(Dispatchers.IO) {
        repository.insertTodo(todo)
    }

    fun updateTodo(todo: TodoModel) = viewModelScope.launch(Dispatchers.IO) {
        repository.updateTodo(todo)
    }

    fun deleteTodo(todo: TodoModel) = viewModelScope.launch(Dispatchers.IO) {
        repository.deleteTodo(todo)
    }

    fun getAllTodo(): LiveData<List<TodoModel>> {
        return repository.getAllTodo()
    }

    fun updateCompletionStatus(todoId: Int, status: Boolean) =
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateCompletionStatus(todoId, status)
        }

    fun setTodoPriority(todoId: Int, priority: Priority) =
        viewModelScope.launch(Dispatchers.IO) {
            repository.setTodoPriority(todoId, priority)
        }
}