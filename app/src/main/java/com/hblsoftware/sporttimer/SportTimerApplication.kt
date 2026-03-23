package com.hblsoftware.sporttimer

import android.app.Application
import android.os.SystemClock
import androidx.room.Room
import com.hblsoftware.sporttimer.data.ProfilesRepository
import com.hblsoftware.sporttimer.data.RoomProfilesRepository
import com.hblsoftware.sporttimer.data.WorkoutSessionManager
import com.hblsoftware.sporttimer.data.WorkoutSessionState
import com.hblsoftware.sporttimer.data.local.SportTimerDatabase
import com.hblsoftware.sporttimer.service.WorkoutSessionForegroundService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SportTimerApplication : Application() {
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private var lastServiceStartAttemptElapsedRealtime = 0L

    private val database: SportTimerDatabase by lazy {
        Room.databaseBuilder(
            applicationContext,
            SportTimerDatabase::class.java,
            "sport_timer.db"
        )
            .addMigrations(SportTimerDatabase.MIGRATION_1_2)
            .build()
    }

    val profilesRepository: ProfilesRepository by lazy {
        RoomProfilesRepository(database.profilesDao())
    }

    val workoutSessionManager: WorkoutSessionManager by lazy { WorkoutSessionManager() }

    override fun onCreate() {
        super.onCreate()
        observeSessionStateForForegroundService()
    }

    private fun observeSessionStateForForegroundService() {
        applicationScope.launch {
            var wasActive = false
            workoutSessionManager.sessionState.collectLatest { state ->
                val isActive = state is WorkoutSessionState.Active
                if (isActive) {
                    val now = SystemClock.elapsedRealtime()
                    val shouldRetryStart = now - lastServiceStartAttemptElapsedRealtime >= START_RETRY_INTERVAL_MS
                    if (!wasActive || shouldRetryStart) {
                        WorkoutSessionForegroundService.start(applicationContext)
                        lastServiceStartAttemptElapsedRealtime = now
                    }
                }

                wasActive = isActive
            }
        }
    }

    private companion object {
        private const val START_RETRY_INTERVAL_MS = 5_000L
    }
}
