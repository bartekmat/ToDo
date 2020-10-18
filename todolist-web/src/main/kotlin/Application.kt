package com.rocksolidknowledge.todolist.web

import com.github.mustachejava.DefaultMustacheFactory
import com.rocksolidknowledge.dataaccess.shared.TodoService
import com.rocksolidknowledge.dataaccess.shared.TodoServiceAPICall
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.client.*
import io.ktor.client.engine.apache.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.mustache.Mustache
import io.ktor.request.*
import io.ktor.sessions.*
import org.koin.dsl.module.module
import org.koin.ktor.ext.inject
import org.koin.standalone.StandAloneContext

val todoAppModule = module {
    single <TodoService> { TodoServiceAPICall() }
}

val logonProvider = OAuthServerSettings.OAuth2ServerSettings(
    name = "idsvr",
    authorizeUrl = "https://localhost:5443/connect/authorize",
    accessTokenUrl = "https://localhost:5443/connect/token",
    requestMethod = HttpMethod.Post,
    clientId = "todolistClient",
    clientSecret = "superSecretPassword",
    defaultScopes = listOf("todolistAPI.read", "todolistAPI.write")
)

fun main (args: Array<String>) {
    StandAloneContext.startKoin(listOf(todoAppModule))
    embeddedServer(Netty, commandLineEnvironment(args)).start()
}

@Suppress("unused")
fun Application.module() {
    val oauthHttpClient: HttpClient = HttpClient(Apache).apply {
        environment.monitor.subscribe(ApplicationStopping) {
            close()
        }
    }
    val todoService: TodoService by inject()
    moduleWithDependencies(todoService, oauthHttpClient)
}

const val oauthAuthentication = "oauthAuthentication"

fun Application.moduleWithDependencies(todoService: TodoService, oauthHttpClient: HttpClient) {
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

    install(Sessions){
        cookie<UserSession>("KTOR_SESSION", storage = SessionStorageMemory())
    }

    install(Authentication){
        oauth(oauthAuthentication){//here we assign name to this configuration so that it can be referenced in authenticate(){} block in routing
            client = oauthHttpClient
            providerLookup ={
                logonProvider
            }
            urlProvider = { redirectUrl("/todos") } //i guess this one could be as well hardcoded to http://localhost:9080/todos
        }
    }

    install(Routing){
        if (isDev) trace {
            application.log.trace(it.buildText())
        }
        todos(todoService)
        staticResources()
    }
}

data class UserSession(val id: String, val name: String)

val Application.environmentKind get() = environment.config.property("ktor.environment").getString()
val Application.isDev get() = environmentKind == "dev"
val Application.isTest get() = environmentKind == "test"
val Application.isProd get() = environmentKind != "dev" && environmentKind != "test"

private fun ApplicationCall.redirectUrl(path: String): String {
    val defaultPort = if (request.origin.scheme == "http") 80 else 443
    val hostPort = request.host()!! + request.port().let { port -> if (port == defaultPort) "" else ":$port" }
    val protocol = "https"
    return "${protocol}://$hostPort$path"
}