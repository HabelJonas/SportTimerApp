package com.hblsoftware.sporttimer.data

import com.hblsoftware.sporttimer.data.local.ProfilesDao
import com.hblsoftware.sporttimer.data.local.toDomainModel
import com.hblsoftware.sporttimer.data.local.toEntity
import com.hblsoftware.sporttimer.model.WorkoutProfile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class RoomProfilesRepository(
    private val profilesDao: ProfilesDao,
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
) : ProfilesRepository {

    override val profiles: StateFlow<List<WorkoutProfile>> = profilesDao.observeProfiles()
        .map { entities -> entities.map { entity -> entity.toDomainModel() } }
        .stateIn(
            scope = scope,
            started = SharingStarted.Eagerly,
            initialValue = emptyList()
        )

    override fun upsert(profile: WorkoutProfile) {
        scope.launch {
            profilesDao.upsert(profile.toEntity())
        }
    }

    override fun findById(id: String): WorkoutProfile? =
        profiles.value.firstOrNull { it.id == id }
}

