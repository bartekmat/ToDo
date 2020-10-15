package com.rocksolidknowledge.todolist.restapi

import com.fasterxml.jackson.databind.SerializationFeature
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.jackson.*
import io.ktor.response.*
import io.ktor.routing.*

fun main(args: Array<String>): Unit = io.ktor.server.cio.EngineMain.main(args)

val TodoContentV1 = ContentType("application", "vnd.todoapi.v1+json")


@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {


    install(Routing) {
        todoApi()
    }

    install(StatusPages) {
        this.exception<Throwable> { e ->
            call.respondText(e.localizedMessage, ContentType.Text.Plain, HttpStatusCode.InternalServerError)
            throw e
        }
    }

    install(ContentNegotiation) {
        // part 1 - simple converter
        // register(TodoContentV1, JacksonConverter())

        // part 2
        jackson(TodoContentV1) {
            enable(SerializationFeature.INDENT_OUTPUT)
            disableDefaultTyping()
        }

        jackson {
            enable(SerializationFeature.INDENT_OUTPUT)
            disableDefaultTyping()
        }
    }
}


