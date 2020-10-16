package com.rocksolidknowledge.todolist.web

import io.ktor.http.content.*
import io.ktor.routing.*
import java.io.File

fun Routing.staticResources() {
    static {
        staticRootFolder = File("wwwroot")
        static("css"){
            files("css")
        }
        static("js"){
            files("js")
        }
        default("index.html")
    }
}