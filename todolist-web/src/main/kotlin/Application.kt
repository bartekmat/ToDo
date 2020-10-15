package com.rocksolidknowledge.todolist.web

import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*

fun main (args: Array<String>) {
    embeddedServer(Netty, commandLineEnvironment(args)).start()
}

@Suppress("unused")
fun Application.module() {
    install(StatusPages){
        when {
            isDev -> {
                this.exception<Throwable> { e ->
                    call.respondText(e.localizedMessage, ContentType.Text.Plain, HttpStatusCode.InternalServerError)
                    throw e
                }
            }
            isTest -> {
                    this.exception<Throwable> {e ->
                        call.response.status(HttpStatusCode.InternalServerError)
                    }
            }
            isProd -> {
                    this.exception<Throwable> {e ->
                        call.respondText(e.localizedMessage, ContentType.Text.Plain, HttpStatusCode.InternalServerError)
                        throw e
                    }
            }
        }

    }
    install(Routing){
        if (isDev) trace {
            application.log.trace(it.buildText())
        }
        staticResources()
    }
}

val Application.environmentKind get() = environment.config.property("ktor.environment").getString()
val Application.isDev get() = environmentKind == "dev"
val Application.isTest get() = environmentKind == "test"
val Application.isProd get() = environmentKind != "dev" && environmentKind != "test"