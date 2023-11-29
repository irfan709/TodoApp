package com.example.todoapp.repository

import com.example.todoapp.data.TodoModel
import com.example.todoapp.database.TodoDatabase
import com.example.todoapp.enums.Priority


class TodoRepository(private val database: TodoDatabase) {

    fun getAllTodo() = database.todoDao().getAllTodo()

    suspend fun insertTodo(todo: TodoModel) = database.todoDao().insertTodo(todo)

    suspend fun updateTodo(todo: TodoModel) = database.todoDao().updateTodo(todo)

    suspend fun deleteTodo(todo: TodoModel) = database.todoDao().deleteTodo(todo)

    suspend fun updateCompletionStatus(todoId: Int, status: Boolean) =
        database.todoDao().updateCompletionStatus(todoId, status)

    suspend fun setTodoPriority(todoId: Int, priority: Priority) =
        database.todoDao().setTodoPriority(todoId, priority)

}