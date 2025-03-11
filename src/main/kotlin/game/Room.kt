package com.daisy.game

import com.daisy.model.Player
import java.util.*

class Room(
    val id: String = UUID.randomUUID().toString(),
    private val players: MutableList<Player> = mutableListOf(),
    private val gameService: GameService = GameService(),
) {
    val isEmpty: Boolean
        get() = players.size == 0

    private val isFull: Boolean
        get() = players.size == 2

    fun addPlayer(player: Player): Boolean {
        if (isFull) return false

        val playerSymbol = if (players.any { it == Player.X }) Player.O else Player.X

        if (players.any { it == playerSymbol }) {
            return false
        }

        players.add(player)
        return true
    }

    fun removePlayer(player: Player) {
        players.remove(player)
    }
}