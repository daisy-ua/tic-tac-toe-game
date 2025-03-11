package com.daisy.game

import com.daisy.model.Player
import java.util.concurrent.ConcurrentHashMap

object RoomManager {
    private val rooms = ConcurrentHashMap<String, Room>()

    fun createRoom(): Room {
        val room = Room()
        rooms[room.id] = room
        return room
    }

    fun joinRoom(roomId: String, player: Player): Boolean? {
        val room = rooms[roomId]
        return room?.let {
            room.addPlayer(player)
        }
    }

    fun leaveRoom(roomId: String, player: Player) {
        rooms[roomId]?.let { room ->
            room.removePlayer(player)
            if (room.isEmpty) {
                rooms.remove(roomId)
            }
        }
    }

    fun getRoom(roomId: String): Room? = rooms[roomId]
}