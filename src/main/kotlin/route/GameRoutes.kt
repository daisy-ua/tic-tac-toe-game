package com.daisy.route

import com.daisy.game.GameService
import com.daisy.game.RoomManager
import com.daisy.model.Move
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.consumeEach
import kotlinx.serialization.json.Json

fun Route.ticTacToeSocket(session: GameService) {
    webSocket("rooms/{roomId}/game") {
        val player = session.connectPlayer(this)
        if (player == null) {
            close(CloseReason(CloseReason.Codes.CANNOT_ACCEPT, "The game is already full."))
            return@webSocket
        }

        try {
            incoming.consumeEach { frame ->
                if (frame is Frame.Text) {
                    val action = extractAction(frame.readText())
                    session.makeMove(player, action.row, action.col)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            session.disconnectPlayer(player)
        }
    }
}

private fun extractAction(message: String): Move {
    val type = message.substringBefore("#")
    val body = message.substringAfter("#")
    return when (type) {
        "move" -> Json.decodeFromString(body)
        else -> Move(-1, -1)
    }
}