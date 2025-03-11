package com.daisy.plugins

import com.daisy.game.GameService
import com.daisy.route.roomRoute
import com.daisy.route.ticTacToeSocket
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureRouting(session: GameService) {
    routing {
        roomRoute()
        ticTacToeSocket(session)
    }
}
