package com.rocksolidknowledge.todolist.web

import io.ktor.application.*
import io.ktor.mustache.*
import io.ktor.response.*
import io.ktor.routing.*

fun Routing.todos(){
    get("/todos") {
        call.respond(
            MustacheContent("todos.hbs")
        )
    }
}