package com.rocksolidknowledge.todolist.web.viewmodels

import com.rocksolidknowledge.todolist.shared.TodoItem

data class TodoVM(private val todos: List<TodoItem>) {
    val todoItems = todos
    val userName = "Tim Buchalka"
}