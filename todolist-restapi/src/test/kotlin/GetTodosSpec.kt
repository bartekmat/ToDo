package com.rocksolidknowledge.todolist.restapi

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.rocksolidknowledge.todolist.shared.Importance
import com.rocksolidknowledge.todolist.shared.TodoItem
import io.ktor.config.MapApplicationConfig
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.TestApplicationEngine
import io.ktor.server.testing.createTestEnvironment
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.setBody
import org.amshove.kluent.*
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import java.time.LocalDate


/**
 * This example creates the TestApplicationEngine engine directly and then uses that engine
 */
object GetTodosSpec : Spek({

    val todo = TodoItem(
        1,
        "Add database processing",
        "Add backend support to this code",
        "Kevin",
        LocalDate.of(2018, 12, 18),
        Importance.HIGH
    )



    describe("Get the todos") {
        val engine = TestApplicationEngine(createTestEnvironment())
        engine.start(wait = false) // for now we can't eliminate it

        // Initialise testing environment
        // equivalent to 'application.conf'
        with(engine) {
            (environment.config as MapApplicationConfig).apply {
                // Set here the properties
                put("ktor.environment", "test")
            }
        }


        engine.application.module() // our main module function
        val mapper = jacksonObjectMapper()
            .registerModule(JavaTimeModule()) //it is important to set this beacuse we have dates in our object

        with(engine) {

            it("should be OK to get the list of todos") {
                handleRequest(HttpMethod.Get, "/api/todos").apply {
                    response.status().`should be`(HttpStatusCode.OK)
                }
            }
            /**
             * These three tests are equivalent ways of doing the same thing
             * They use @see[apply],  @see[let] and @see[with] to run the test case
             *
             * Not sure which is idiomatic (yet)
             */
            it("should get todos"){
                handleRequest(HttpMethod.Get, "/api/todos").apply {
                    response.content
                        .shouldNotBeNull()
                        .shouldContain("database")
                }
            }
            it("should create a todo") {
                with(handleRequest(HttpMethod.Post, "/api/todos") {
                    addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                    setBody(mapper.writeValueAsString(todo))
                }){
                    response.status().`should be`(HttpStatusCode.Created)
                }
            }
            it("should update the todos"){
                with(handleRequest(HttpMethod.Put, "/api/todos/1") {
                    addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                    setBody(mapper.writeValueAsString(todo))
                }) {
                    response.status().`should be`(HttpStatusCode.NoContent)
                }
            }
            it("should delete the todo"){
                handleRequest(HttpMethod.Delete, "/api/todos/1").apply {
                    response.status().`should be`(HttpStatusCode.NoContent)
                }
            }
            it("should get the existing todo"){
                handleRequest(HttpMethod.Get, "/api/todos/1").apply {
                    response.content
                        .shouldNotBeNull()
                        .shouldContain("database")
                        .shouldContain("Kevin")
                }
            }
            it("should return error for non existing id get request"){
                handleRequest(HttpMethod.Get, "/api/todos/55").apply {
                    response.status().`should be`(HttpStatusCode.NotFound)
                }
            }
        }
    }
})

