package com.github.bugscatcher.web

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.github.bugscatcher.model.User
import com.github.bugscatcher.service.UsersService
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.route
import java.util.*

fun Route.user(userService: UsersService) {
    route("/users") {
        get("/{id}") {
            val user = userService.getUser(UUID.fromString(call.parameters["id"]))
            if (user == null) call.respond(HttpStatusCode.NotFound)
            else call.respond(user)
        }

        post("/") {
            val user = call.receive<User>()
            call.respond(HttpStatusCode.Created, userService.addUser(user))
        }
    }

    val mapper = jacksonObjectMapper().apply {
        setSerializationInclusion(JsonInclude.Include.NON_NULL)
    }

//    webSocket("/updates") {
//        try {
//            widgetService.addChangeListener(this.hashCode()) {
//                outgoing.send(Frame.Text(mapper.writeValueAsString(it)))
//            }
//            while(true) {
//                incoming.receiveOrNull() ?: break
//            }
//        } finally {
//            widgetService.removeChangeListener(this.hashCode())
//        }
//    }
}