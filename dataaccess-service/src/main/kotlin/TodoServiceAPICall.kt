package com.rocksolidknowledge.dataaccess.shared

import com.rocksolidknowledge.todolist.shared.TodoItem
import io.ktor.client.*
import io.ktor.client.features.json.*
import io.ktor.client.request.*

class TodoServiceAPICall : TodoService {

    val client = HttpClient(){
        install(JsonFeature){
            serializer = JacksonSerializer()
        }
    }

    val apiEndpoint = "http://localhost:8081/api/todos"

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
        return client.get(apiEndpoint)
    }

    override suspend fun getTodo(id: Int): TodoItem {
        return client.get("$apiEndpoint/1")
    }

}