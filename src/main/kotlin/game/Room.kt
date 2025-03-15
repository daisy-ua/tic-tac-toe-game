package com.daisy.game

import com.daisy.model.GameState
import com.daisy.model.Move
import com.daisy.model.Player
import io.ktor.websocket.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.serialization.json.Json
import java.util.*
import java.util.concurrent.ConcurrentHashMap

class Room(
    val id: String = generateRoomId(),
    private val gameManager: GameManager = GameManager(),
) {
    private val players: ConcurrentHashMap<Player, WebSocketSession> = ConcurrentHashMap<Player, WebSocketSession>()
    private val gameScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    val isEmpty: Boolean
        get() = players.size == 0

    init {
        gameManager.state.onEach(::broadcast).launchIn(gameScope)
    }

    fun addPlayer(session: WebSocketSession): Player? {
        val player = gameManager.findAvailablePlayer() ?: return null

        gameManager.updateState {
            copy(
                connectedPlayers = connectedPlayers + player
            )
        }

        players[player] = session

        return player
    }

    fun removePlayer(player: Player) {
        players.remove(player)
        gameManager.updateState {
            copy(
                connectedPlayers = connectedPlayers - player
            )
        }
    }

    fun handleAction(player: Player, action: Move) {
        gameManager.makeMove(player, action.row, action.col)
    }

    suspend fun broadcast(state: GameState) {
        players.values.forEach { socket ->
            socket.send(
                Json.encodeToString(state)
            )
        }
    }

    companion object {
        private fun generateRoomId(length: Int = 8): String {
            return UUID.randomUUID().toString().replace("-", "").take(length)        }
    }
}