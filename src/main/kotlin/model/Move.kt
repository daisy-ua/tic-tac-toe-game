package com.daisy.models

import kotlinx.serialization.Serializable

@Serializable
data class Move(
    val row: Int,
    val col: Int,
)
