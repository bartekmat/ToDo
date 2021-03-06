package com.rocksolidknowledge.dataaccess.shared

import com.knowledgespike.todolist.TodoListRepository
import com.rocksolidknowledge.todolist.shared.Importance
import com.rocksolidknowledge.todolist.shared.TodoItem
import java.time.LocalDate

val todo1 = TodoItem(
    1,
    "Add database processing 1",
    "Add backend support to this code",
    "Kevin",
    LocalDate.of(2018, 12, 18),
    Importance.HIGH
)

val todo2 = TodoItem(
    2,
    "Add database processing 2",
    "Add backend support to this code",
    "Kevin",
    LocalDate.of(2018, 12, 18),
    Importance.HIGH
)

val todos = listOf(todo1, todo2)

class TodoServiceDBCall(val todoListRepository: TodoListRepository) : TodoService {
    override fun update(id: Int, todo: TodoItem): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun create(todo: TodoItem): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun delete(id: Int): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun getAll(): List<TodoItem> {
        return todos
    }

    override suspend fun getTodo(id: Int): TodoItem {
        return todos[id]
    }

}