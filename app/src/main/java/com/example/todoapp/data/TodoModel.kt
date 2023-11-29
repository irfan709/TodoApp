package com.example.todoapp.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.todoapp.enums.Priority
import java.io.Serializable

@Entity(tableName = "todo_table")
data class TodoModel(

    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,

    @ColumnInfo(name = "todo_name")
    var name: String = "",

    @ColumnInfo(name = "todo_description")
    var description: String = "",

    @ColumnInfo(name = "todo_date")
    var date: String = "",

    @ColumnInfo(name = "todo_priority")
    var priority: Priority? = null,

    @ColumnInfo(name = "todo_status")
    var status: Boolean = false


): Serializable
