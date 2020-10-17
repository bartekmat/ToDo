package com.rocksolidknowledge.todolist.web

import com.github.mustachejava.DefaultMustacheFactory
import com.rocksolidknowledge.dataaccess.shared.TodoService
import com.rocksolidknowledge.dataaccess.shared.TodoServiceAPICall
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.mustache.Mustache
import org.koin.dsl.module.module
import org.koin.ktor.ext.inject
import org.koin.standalone.StandAloneContext

val todoAppModule = module {
    single <TodoService> { TodoServiceAPICall() }
}

fun main (args: Array<String>) {
    StandAloneContext.startKoin(listOf(todoAppModule))
    embeddedServer(Netty, commandLineEnvironment(args)).start()
}

@Suppress("unused")
fun Application.module() {
    val todoService: TodoService by inject()
    moduleWithDependencies(todoService)
}
fun Application.moduleWithDependencies(todoService: TodoService) {
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

    install(Mustache){
        mustacheFactory = DefaultMustacheFactory("templates")
    }

    install(Routing){
        if (isDev) trace {
            application.log.trace(it.buildText())
        }
        todos(todoService)
        staticResources()
    }
}

val Application.environmentKind get() = environment.config.property("ktor.environment").getString()
val Application.isDev get() = environmentKind == "dev"
val Application.isTest get() = environmentKind == "test"
val Application.isProd get() = environmentKind != "dev" && environmentKind != "test"