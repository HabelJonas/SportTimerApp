package com.hblsoftware.sporttimer.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface ProfilesDao {
    @Query("SELECT * FROM workout_profiles ORDER BY name COLLATE NOCASE ASC")
    fun observeProfiles(): Flow<List<WorkoutProfileEntity>>

    @Upsert
    suspend fun upsert(profile: WorkoutProfileEntity)

    @Query("SELECT * FROM workout_profiles WHERE id = :id LIMIT 1")
    suspend fun findById(id: String): WorkoutProfileEntity?
}

