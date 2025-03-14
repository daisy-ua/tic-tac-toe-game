package com.daisy.route

import com.daisy.game.RoomManager
import com.daisy.utils.extractAction
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.consumeEach

fun Route.gameRoute() {

    post("/room/create") {
        val room = RoomManager.createRoom()
        call.respond((mapOf("roomId" to room.id)))
    }

    webSocket("room/{roomId}/game") {
        val roomId = call.parameters["roomId"] ?: return@webSocket close(
            CloseReason(
                CloseReason.Codes.CANNOT_ACCEPT,
                "Missing room ID"
            )
        )

        val room = RoomManager.getRoom(roomId) ?: return@webSocket close(
            CloseReason(
                CloseReason.Codes.CANNOT_ACCEPT,
                "Room ID is invalid"
            )
        )

        val player = room.addPlayer(this) ?: return@webSocket close(
            CloseReason(
                CloseReason.Codes.VIOLATED_POLICY,
                "Room is full"
            )
        )

        try {
            incoming.consumeEach { frame ->
                if (frame is Frame.Text) {
                    val action = extractAction(frame.readText())
                    room.handleAction(player, action)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            room.removePlayer(player)
            if (room.isEmpty) {
                RoomManager.removeRoom(roomId)
            }
        }
    }
}