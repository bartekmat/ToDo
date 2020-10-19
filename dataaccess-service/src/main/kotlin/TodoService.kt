package com.rocksolidknowledge.dataaccess.shared

import com.rocksolidknowledge.todolist.shared.TodoItem

interface TodoService {
    suspend fun getAll(): List<TodoItem>
    suspend fun getTodo(id: Int): TodoItem
    fun delete(id: Int): Boolean
    fun create(todo: TodoItem): Boolean
    fun update(id: Int, todo: TodoItem): Boolean
}