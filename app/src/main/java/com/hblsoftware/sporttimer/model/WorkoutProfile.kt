package com.hblsoftware.sporttimer.model

import java.util.UUID

data class WorkoutProfile(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val preparationSeconds: Int,
    val workSeconds: Int,
    val restSeconds: Int,
    val rounds: Int
) {
    val totalSeconds: Int
        get() = preparationSeconds + rounds * (workSeconds + restSeconds)
}