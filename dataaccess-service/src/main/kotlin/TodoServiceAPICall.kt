package com.rocksolidknowledge.dataaccess.shared

import com.beust.klaxon.Parser
import com.rocksolidknowledge.todolist.IOAuthClient
import com.rocksolidknowledge.todolist.TokenResponse
import com.rocksolidknowledge.todolist.shared.TodoItem
import io.ktor.client.*
import io.ktor.client.features.json.*
import io.ktor.client.request.*

class TodoServiceAPICall(val oauthClient: IOAuthClient) : TodoService {

    val client = HttpClient() {
        install(JsonFeature) {
            serializer = JacksonSerializer()
        }
    }

    val apiEndpoint = "http://localhost:8082/api/todos"

    override fun update(id: Int, todo: TodoItem): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun create(todo: TodoItem): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun delete(id: Int): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun getAll(): List<TodoItem> {
        val token: TokenResponse? = oauthClient.getClientCredential(
            "http://localhost:5000/connect/token",
            "todolistClient",
            "superSecretPassword",
            listOf("todolistAPI.read", "todolistAPI.write")
        )
        val res = token?.let {
            oauthClient.callApi(apiEndpoint, it.tokenType, it.token)
        }
        val items = res?.let {
            val parser = Parser.default()
            parser.parse(StringBuilder(it)) as List<TodoItem>
        }

        return if (items != null) {
            items
        } else {
            listOf()
        }
    }

    override suspend fun getTodo(id: Int): TodoItem {
        return client.get("$apiEndpoint/1")
    }

}