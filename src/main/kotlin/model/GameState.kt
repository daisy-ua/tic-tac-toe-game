package com.daisy.models

import kotlinx.serialization.Serializable

@Serializable
data class GameState(
    val currentPlayer: Player = Player.X,
    val board: Array<Array<Player?>> = emptyBoard,
    val isBoardFull: Boolean = false,
    val winnerPlayer: Player? = null,
    val connectedPlayers: List<Player> = emptyList(),
) {
    val isGameEnded: Boolean
        get() {
            return winnerPlayer != null || isBoardFull
        }

    companion object {
        val emptyBoard: Array<Array<Player?>>
            get() = Array(3) { Array(3) { null } }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as GameState

        if (isBoardFull != other.isBoardFull) return false
        if (currentPlayer != other.currentPlayer) return false
        if (!board.contentDeepEquals(other.board)) return false
        if (winnerPlayer != other.winnerPlayer) return false
        if (connectedPlayers != other.connectedPlayers) return false

        return true
    }

    override fun hashCode(): Int {
        var result = isBoardFull.hashCode()
        result = 31 * result + currentPlayer.hashCode()
        result = 31 * result + board.contentDeepHashCode()
        result = 31 * result + (winnerPlayer?.hashCode() ?: 0)
        result = 31 * result + connectedPlayers.hashCode()
        return result
    }
}
