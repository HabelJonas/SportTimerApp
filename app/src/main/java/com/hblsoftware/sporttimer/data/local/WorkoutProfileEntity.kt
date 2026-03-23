package com.hblsoftware.sporttimer.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.hblsoftware.sporttimer.model.WorkoutProfile

@Entity(tableName = "workout_profiles")
data class WorkoutProfileEntity(
    @PrimaryKey val id: String,
    val name: String,
    val preparationSeconds: Int,
    val workSeconds: Int,
    val restSeconds: Int,
    val rounds: Int
)

fun WorkoutProfileEntity.toDomainModel(): WorkoutProfile = WorkoutProfile(
    id = id,
    name = name,
    preparationSeconds = preparationSeconds,
    workSeconds = workSeconds,
    restSeconds = restSeconds,
    rounds = rounds
)

fun WorkoutProfile.toEntity(): WorkoutProfileEntity = WorkoutProfileEntity(
    id = id,
    name = name,
    preparationSeconds = preparationSeconds,
    workSeconds = workSeconds,
    restSeconds = restSeconds,
    rounds = rounds
)
