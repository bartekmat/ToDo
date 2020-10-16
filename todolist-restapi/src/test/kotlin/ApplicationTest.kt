package com.rocksolidknowledges.todolist.restapi

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.rocksolidknowledge.todolist.restapi.module
import com.rocksolidknowledge.todolist.shared.Importance
import com.rocksolidknowledge.todolist.shared.TodoItem
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.TestApplicationEngine
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.setBody
import io.ktor.server.testing.withTestApplication
import org.amshove.kluent.`should be`
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldContain
import org.amshove.kluent.shouldNotBeNull
import org.junit.Test
import java.time.LocalDate

//this class contains testing done in a standard wy - run by jUnit5 - In my opinion this is more user friendly than spec
class ApplicationTest {

    val mapper = jacksonObjectMapper()
        .registerModule(JavaTimeModule())


    @Test
    fun testGetTodos() {
        testApp {
            // can handle tests this way...
            handleRequest(HttpMethod.Get, "/api/todos").apply {
                response.status().`should be`(HttpStatusCode.OK)
            }

            // or using with
            with(handleRequest(HttpMethod.Get, "/api/todos")) {
                response.content
                        .shouldNotBeNull()
                        .shouldContain("database")
            }

            with(handleRequest(HttpMethod.Get, "/api/todos/1")) {
                val mapper = jacksonObjectMapper()
                val item = mapper.readValue<TodoItem>(response.content!!)
                item.title.shouldBeEqualTo("Add database processing")
            }
        }
    }

    @Test
    fun testCreatingTodos() {
        testApp {

            val json = mapper.writeValueAsString(testTodoItem)

            var call = handleRequest(HttpMethod.Post, "/api/todos"){
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                setBody(json)
            }

            with(call) {

                response.status().`should be`(HttpStatusCode.Created)
            }
        }
    }

    @Test
    fun testUpdatingTodos() {
        testApp {

            val json = mapper.writeValueAsString(testTodoItem)

            var call = handleRequest(HttpMethod.Put, "/api/todos/1"){
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                setBody(json)
            }

            with(call) {
                response.status().`should be`(HttpStatusCode.NoContent)
            }
        }
    }

    @Test
    fun testDeletingTodos() {
        testApp {

            with(handleRequest(HttpMethod.Delete, "/api/todos/1")) {

                response.status().`should be`(HttpStatusCode.NoContent)
            }

//            with(handleRequest(HttpMethod.Delete, "/api/todos/")) {
//                response.status().`should be`(HttpStatusCode.NotFound)
//            }
        }
    }

    private fun testApp(callback: TestApplicationEngine.() -> Unit) {
        withTestApplication({ module() }) { callback() }
    }
}

val testTodoItem = TodoItem(3, "", "", "", LocalDate.of(2018, 12, 18), Importance.HIGH)