package com.daisy.game

import java.util.concurrent.ConcurrentHashMap

object RoomManager {
    private val rooms = ConcurrentHashMap<String, Room>()

    fun createRoom(): Room {
        val room = Room()
        rooms[room.id] = room
        return room
    }

    fun removeRoom(roomId: String) {
        rooms.remove(roomId)
    }

    fun getRoom(roomId: String): Room? = rooms[roomId]
}