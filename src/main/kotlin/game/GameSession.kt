package com.daisy.game

import com.daisy.models.GameState
import com.daisy.models.Player
import io.ktor.websocket.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.serialization.json.Json
import java.util.concurrent.ConcurrentHashMap

class GameSession {

    private val state: MutableStateFlow<GameState> = MutableStateFlow(GameState())
    private val currentState: GameState get() = state.value

    private val players = ConcurrentHashMap<Player, WebSocketSession>()

    private val gameScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var delayNewSessionJob: Job? = null

    init {
        state.onEach(::broadcast).launchIn(gameScope)
    }

    //    Multiplayer mode
    fun connectPlayer(session: WebSocketSession): Player? {
        val player = if (currentState.connectedPlayers.contains(Player.X)) Player.O else Player.X

        if (currentState.connectedPlayers.contains(player)) {
            return null
        }

        state.update {
            it.copy(
                connectedPlayers = it.connectedPlayers + player
            )
        }

        players[player] = session

        return player
    }

    fun disconnectPlayer(player: Player) {
        players.remove(player)
        state.update {
            it.copy(
                connectedPlayers = it.connectedPlayers - player
            )
        }
    }

    fun makeMove(player: Player, row: Int, col: Int) {
        val isMoveInvalid = currentState.run {
            (player != currentPlayer || board[row][col] != null || isGameEnded)
        }

        if (isMoveInvalid) return

        val newBoard = currentState.board.also { board ->
            board[row][col] = player
        }

        val isBoardFull = newBoard.flatten().all { it != null }
        if (isBoardFull) {
            println("Draw!")
            startNewSessionDelayed()
        }

        state.update {
            it.copy(
                currentPlayer = switchPlayer(it.currentPlayer),
                board = newBoard,
                isBoardFull = isBoardFull,
                winnerPlayer = checkWinner()?.also {
                    startNewSessionDelayed()
                }
            )
        }
    }

    suspend fun broadcast(state: GameState) {
        players.values.forEach { socket ->
            socket.send(
                Json.encodeToString(state)
            )
        }
    }

    private fun switchPlayer(current: Player): Player {
        return if (current == Player.X) Player.O else Player.X
    }

    private fun checkWinner(): Player? {
        val board = currentState.board

        for (i in board.indices) {
            if (board[i].all { it != null && it == board[i][0] }) {
                board[i][0]
            }

            if (board.all { it[i] != null && it[i] == board[0][i] }) {
                return board[0][i]
            }
        }

        if (board.indices.all { board[it][it] != null && board[it][it] == board[0][0] }) {
            return board[0][0]
        }

        if (board.indices.all {
                board[it][board.size - 1 - it] != null &&
                board[it][board.size - 1 - it] == board[0][board.size - 1]
            }) {
            return board[0][board.size - 1]
        }

        return null
    }

    private fun startNewSessionDelayed(timeInMillis: Long = 5000L) {
        delayNewSessionJob?.cancel()
        delayNewSessionJob = gameScope.launch {
            delay(timeInMillis)
            state.update {
                GameState(connectedPlayers = it.connectedPlayers)
            }
        }
    }
}