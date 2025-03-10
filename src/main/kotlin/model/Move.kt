package com.daisy.model

import kotlinx.serialization.Serializable

@Serializable
data class Move(
    val row: Int,
    val col: Int,
)
