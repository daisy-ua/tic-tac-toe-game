package com.daisy.plugins

import com.daisy.game.GameSession
import com.daisy.route.ticTacToeSocket
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureRouting(session: GameSession) {
    routing {
        ticTacToeSocket(session)
    }
}
