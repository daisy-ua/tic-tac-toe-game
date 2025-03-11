package com.daisy

import com.daisy.game.GameService
import com.daisy.plugins.configureRouting
import com.daisy.plugins.configureSerialization
import com.daisy.plugins.configureSockets
import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    val gameSession = GameService()

    configureSerialization()
    configureSockets()
    configureRouting(gameSession)
}
