package com.daisy.route

import com.daisy.game.RoomManager
import com.daisy.model.Player
import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.roomRoute() {
//    create a new room
    post("/rooms") {
        val room = RoomManager.createRoom().also { RoomManager.joinRoom(it.id, Player.X) }
        call.respond((mapOf("roomId" to room.id)))
    }

    post("/room/{roomId}/join") {
        val roomId = call.parameters["roomId"] ?: return@post call.respond(HttpStatusCode.BadRequest, "Missing room ID")

        val player = Player.O
        val isSuccess = RoomManager.joinRoom(roomId, player)
        isSuccess?.let {
            if (isSuccess) {
                call.respond(HttpStatusCode.OK)
            } else {
                call.respond(HttpStatusCode.Forbidden, "Room is full")
            }
        } ?: call.respond(HttpStatusCode.NotFound, "Room ID is invalid")
    }
}