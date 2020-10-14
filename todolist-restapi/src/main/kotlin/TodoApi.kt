package com.rocksolidknowledge.todolist.restapi

import com.rocksolidknowledge.todolist.shared.Importance
import com.rocksolidknowledge.todolist.shared.TodoItem
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.*
import java.time.LocalDate


fun Routing.todoApi() {
    route("/api") {

        accept(TodoContentV1) {
            get("/todos") {
                call.respond(todos)
            }
        }

        get("/todos") {
            call.respond(todos)
        }

        get("todos/{id}") {
            val id = call.parameters["id"]!!
            try {
                val todo = todos[id.toInt()]
                call.respond(todo)
            } catch (e: Throwable) {
                call.respond(HttpStatusCode.NotFound)
            }
        }

        post("todos") {
            val headers = call.request.headers
            val todo = call.receive<TodoItem>()
            val newTodo =
                TodoItem(todos.size + 1, todo.title, todo.details, todo.assignedTo, todo.dueDate, todo.importance)
            todos = todos + newTodo
            call.respond(HttpStatusCode.Created, todos)
        }

        put("todos/{id}") {
            val id = call.parameters["id"]
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest)
                return@put
            }

            val foundItem = todos.getOrNull(id.toInt())
            if (foundItem == null) {
                call.respond(HttpStatusCode.NotFound)
                return@put
            }

            val todo = call.receive<TodoItem>()

            todos = todos.filter { it.id != todo.id }
            todos = todos + todo

            call.respond(HttpStatusCode.NoContent)
        }

        delete("todos/{id}") {
            val id = call.parameters["id"]
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest)
                return@delete
            }

            val foundItem = todos.getOrNull(id.toInt())
            if (foundItem == null) {
                call.respond(HttpStatusCode.NotFound)
                return@delete
            }

            todos = todos.filter { it.id != id.toInt() }
            call.respond(HttpStatusCode.NoContent)
        }
    }
}

val todo1 = TodoItem(
    1,
    "Add RestAPI",
    "Add data support",
    "Me",
    LocalDate.of(2018, 12, 18),
    Importance.HIGH
)

val todo2 = TodoItem(
    2,
    "Add database processing",
    "Add a service to get the data",
    "Me",
    LocalDate.of(2018, 12, 18),
    Importance.HIGH
)

var todos = listOf(todo1, todo2)