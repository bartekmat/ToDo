package com.rocksolidknowledge.todolist.restapi

import com.auth0.jwk.JwkProviderBuilder
import com.fasterxml.jackson.databind.SerializationFeature
import com.knowledgespike.todolist.TodoListRepository
import com.knowledgespike.todolist.TodoListRepositorySql
import com.rocksolidknowledge.dataaccess.shared.TodoService
import com.rocksolidknowledge.dataaccess.shared.TodoServiceDBCall
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.jackson.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import org.koin.dsl.module.module
import org.koin.ktor.ext.inject
import org.koin.standalone.StandAloneContext
import java.net.URL
import java.util.concurrent.TimeUnit

/*
    Here below KOIN configuration for dependency injection
*/
val todoAppModule = module {
    single<TodoService> { TodoServiceDBCall(get()) } //look here get() takes repository instance declared one line below
    single<TodoListRepository> { TodoListRepositorySql() }
}

fun main(args: Array<String>): Unit {
    StandAloneContext.startKoin(listOf(todoAppModule)) //here we pass the function with bean initialization
    embeddedServer(CIO, commandLineEnvironment(args)).start(wait = true)
}

val TodoContentV1 = ContentType("application", "vnd.todoapi.v1+json")


@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    val todoService: TodoService by inject()
    moduleWithDependencies(todoService)
}

fun Application.moduleWithDependencies(todoService: TodoService) {

    val jwkIssuer = environment.config.property("jwt.jwkIssuer").getString()
    val jwksUrl: URL = URL(environment.config.property("jwt.jwksUrl").getString())
    val jwkRealm = environment.config.property("jwt.jwkRealm").getString()
    val jwkProvider = JwkProviderBuilder(jwksUrl)
        .cached(10,24, TimeUnit.HOURS)
        .rateLimited(10,1,TimeUnit.MINUTES)
        .build()
    val audience = "todolistAPI"

    install(Authentication){
        jwt("jwt") {
            verifier(jwkProvider, jwkIssuer)
            var cred : JWTCredential
            var principal : JWTPrincipal
            validate { credentials ->
                cred = credentials
                log.debug("Credentials audience: ${credentials.payload.audience}")
                log.debug("audience: $audience")

                if (credentials.payload.audience.contains(audience)){
                    principal = JWTPrincipal(credentials.payload)
                    principal
                } else {
                    null
                }
            }
        }
    }

    install(Routing) {
        todoApi(todoService)
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


