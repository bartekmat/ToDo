package com.rocksolidknowledge.todolist.restapi

import com.rocksolidknowledge.dataaccess.shared.TodoService
import com.rocksolidknowledge.todolist.shared.TodoItem
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

fun Routing.todoApi(todoService: TodoService) {
    authenticate ("jwt") {
        route("/api") {

            accept(TodoContentV1) {
                get("/todos") {
                    call.respond(todoService.getAll())
                }
            }

            get("/todos") {
                call.respond(todoService.getAll())
            }

            get("todos/{id}") {
                val id = call.parameters["id"]!!
                try {
                    val todo = todoService.getTodo(id.toInt())
                    call.respond(todo)
                } catch (e: Throwable) {
                    call.respond(HttpStatusCode.NotFound)
                }
            }

            post("todos") {
                val todo = call.receive<TodoItem>()
                todoService.create(todo)
                call.respond(HttpStatusCode.Created)
            }

            put("todos/{id}") {
                val id = call.parameters["id"]?: throw IllegalArgumentException("Missing id")
                val todo = call.receive<TodoItem>()
                todoService.update(id.toInt(), todo)
                call.respond(HttpStatusCode.NoContent)
            }

            delete("todos/{id}") {
                val id = call.parameters["id"]?: throw IllegalArgumentException("Missing id")
                todoService.delete(id.toInt())
                call.respond(HttpStatusCode.NoContent)
            }
        }
    }
}