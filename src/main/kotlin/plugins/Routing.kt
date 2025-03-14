package com.daisy.plugins

import com.daisy.route.gameRoute
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        gameRoute()
    }
}
