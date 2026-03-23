package com.hblsoftware.sporttimer.data

import com.hblsoftware.sporttimer.model.WorkoutProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

interface ProfilesRepository {
    val profiles: StateFlow<List<WorkoutProfile>>

    fun upsert(profile: WorkoutProfile)
    fun findById(id: String): WorkoutProfile?
}

class InMemoryProfilesRepository : ProfilesRepository {
    private val _profiles = MutableStateFlow<List<WorkoutProfile>>(emptyList())
    override val profiles: StateFlow<List<WorkoutProfile>> = _profiles.asStateFlow()

    override fun upsert(profile: WorkoutProfile) {
        _profiles.update { current ->
            val index = current.indexOfFirst { it.id == profile.id }
            if (index == -1) {
                current + profile
            } else {
                current.toMutableList().also { it[index] = profile }
            }
        }
    }

    override fun findById(id: String): WorkoutProfile? =
        profiles.value.firstOrNull { it.id == id }
}
