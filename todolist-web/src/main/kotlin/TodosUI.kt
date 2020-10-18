package com.rocksolidknowledge.todolist.web

import com.rocksolidknowledge.dataaccess.shared.TodoService
import com.rocksolidknowledge.todolist.web.viewmodels.TodoVM
import io.ktor.application.*
import io.ktor.mustache.*
import io.ktor.response.*
import io.ktor.routing.*

fun Routing.todos(todoService: TodoService) {
    //authenticate(oauthAuthentication) {
    get("/todos") {
        val todos = todoService.getAll()
        val todoVm = TodoVM(todos)
        call.respond(
            MustacheContent("todos.hbs", mapOf("todos" to todoVm))
        )
    }
    // }
}