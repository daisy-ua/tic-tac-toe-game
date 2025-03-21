package com.daisy.game

import com.daisy.model.GameState
import com.daisy.model.Player
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class GameManager {
    private val _state: MutableStateFlow<GameState> = MutableStateFlow(GameState())
    val state: StateFlow<GameState> get() = _state

    private val currentState: GameState get() = _state.value

    private val gameScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var delayNewSessionJob: Job? = null

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

        _state.update {
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

    fun switchPlayer(current: Player): Player {
        return if (current == Player.X) Player.O else Player.X
    }

    fun checkWinner(): Player? {
        val board = currentState.board

        for (i in board.indices) {
            if (board[i].all { it != null && it == board[i][0] }) {
                return board[i][0]
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

    fun startNewSessionDelayed(timeInMillis: Long = 5000L) {
        delayNewSessionJob?.cancel()
        delayNewSessionJob = gameScope.launch {
            delay(timeInMillis)
            _state.update {
                GameState(connectedPlayers = it.connectedPlayers)
            }
        }
    }

    fun updateState(update: GameState.() -> GameState) {
        _state.update(update)
    }

    fun findAvailablePlayer(): Player? {
        val player = if (currentState.connectedPlayers.contains(Player.X)) Player.O else Player.X

        return if (currentState.connectedPlayers.contains(player)) {
            null
        } else player
    }
}