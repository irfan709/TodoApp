package com.example.todoapp.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.todoapp.enums.Priority

@Dao
interface TodoDao {

    @Insert
    suspend fun insertTodo(todo: `TodoModel`)

    @Update
    suspend fun updateTodo(todo: `TodoModel`)

    @Delete
    suspend fun deleteTodo(todo: `TodoModel`)

    @Query("SELECT * FROM todo_table ORDER BY id DESC")
    fun getAllTodo(): LiveData<List<`TodoModel`>>

    @Query("UPDATE todo_table SET todo_status = :status WHERE id = :todoId")
    suspend fun updateCompletionStatus(todoId: Int, status: Boolean): Int

    @Query("UPDATE todo_table SET todo_priority = :priority WHERE id = :todoId")
    suspend fun setTodoPriority(todoId: Int, priority: `Priority`)

}