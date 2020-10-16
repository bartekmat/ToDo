package com.rocksolidknowledge.todolist.web

import com.rocksolidknowledge.todolist.shared.Importance
import com.rocksolidknowledge.todolist.shared.TodoItem
import com.rocksolidknowledge.todolist.web.viewmodels.TodoVM
import io.ktor.application.*
import io.ktor.mustache.*
import io.ktor.response.*
import io.ktor.routing.*
import java.time.LocalDate

val todo = TodoItem(
    1,
    "Add RestAPI",
    "Add data support",
    "Me",
    LocalDate.of(2018, 12, 18),
    Importance.HIGH
)

val todos = listOf<TodoItem>(todo)

fun Routing.todos(){
    get("/todos") {
        val todoVm = TodoVM(todos)
        call.respond(
            MustacheContent("todos.hbs", mapOf("todos" to todoVm))
        )
    }
}