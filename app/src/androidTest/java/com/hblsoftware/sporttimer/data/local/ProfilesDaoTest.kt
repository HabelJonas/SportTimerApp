package com.hblsoftware.sporttimer.data.local

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ProfilesDaoTest {

    private lateinit var database: SportTimerDatabase
    private lateinit var dao: ProfilesDao

    @Before
    fun setup() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        database = Room.inMemoryDatabaseBuilder(context, SportTimerDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = database.profilesDao()
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun observeProfiles_ordersByName_caseInsensitiveAscending() = runBlocking {
        dao.upsert(entity(id = "3", name = "zeta"))
        dao.upsert(entity(id = "1", name = "Alpha"))
        dao.upsert(entity(id = "2", name = "beta"))

        val observed = dao.observeProfiles().first()

        assertEquals(listOf("Alpha", "beta", "zeta"), observed.map { it.name })
    }

    @Test
    fun upsert_replacesExistingEntity_andFindByIdReturnsUpdated() = runBlocking {
        dao.upsert(entity(id = "id-1", name = "Intervals", workSeconds = 20))
        dao.upsert(entity(id = "id-1", name = "Intervals v2", workSeconds = 30, rounds = 10))

        val found = dao.findById("id-1")

        assertNotNull(found)
        assertEquals("Intervals v2", found?.name)
        assertEquals(30, found?.workSeconds)
        assertEquals(10, found?.rounds)
    }

    private fun entity(
        id: String,
        name: String,
        preparationSeconds: Int = 10,
        workSeconds: Int = 20,
        restSeconds: Int = 10,
        rounds: Int = 8
    ): WorkoutProfileEntity = WorkoutProfileEntity(
        id = id,
        name = name,
        preparationSeconds = preparationSeconds,
        workSeconds = workSeconds,
        restSeconds = restSeconds,
        rounds = rounds
    )
}


