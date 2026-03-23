package com.hblsoftware.sporttimer.data

import com.hblsoftware.sporttimer.model.WorkoutProfile
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class InMemoryProfilesRepositoryTest {

    @Test
    fun upsert_insertsAndReplaces_andFindByIdReflectsLatest() {
        val repository = InMemoryProfilesRepository()
        val initial = profile(id = "p1", name = "Sprint", workSeconds = 20)
        val updated = profile(id = "p1", name = "Sprint Updated", workSeconds = 30)

        repository.upsert(initial)
        repository.upsert(updated)

        assertEquals(1, repository.profiles.value.size)
        assertEquals(updated, repository.profiles.value.single())
        assertEquals(updated, repository.findById("p1"))
        assertNull(repository.findById("missing"))
    }

    @Test
    fun profilesStateFlow_emitsInitialThenInsertThenReplace() = runTest {
        val repository = InMemoryProfilesRepository()
        val first = profile(id = "p1", name = "A")
        val replaced = profile(id = "p1", name = "A v2", workSeconds = 45)
        val emissions = mutableListOf<List<WorkoutProfile>>()

        val collectorJob = backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            repository.profiles.take(3).toList(emissions)
        }
        runCurrent()

        repository.upsert(first)
        repository.upsert(replaced)
        collectorJob.join()

        assertEquals(emptyList<WorkoutProfile>(), emissions[0])
        assertEquals(listOf(first), emissions[1])
        assertEquals(listOf(replaced), emissions[2])
    }

    private fun profile(
        id: String,
        name: String,
        preparationSeconds: Int = 10,
        workSeconds: Int = 20,
        restSeconds: Int = 10,
        rounds: Int = 8
    ): WorkoutProfile = WorkoutProfile(
        id = id,
        name = name,
        preparationSeconds = preparationSeconds,
        workSeconds = workSeconds,
        restSeconds = restSeconds,
        rounds = rounds
    )
}



