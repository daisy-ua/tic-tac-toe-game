package com.daisy.utils

import com.daisy.model.Move
import kotlinx.serialization.json.Json

fun extractAction(message: String): Move {
    val type = message.substringBefore("#")
    val body = message.substringAfter("#")
    return when (type) {
        "move" -> Json.decodeFromString(body)
        else -> Move(-1, -1)
    }
}