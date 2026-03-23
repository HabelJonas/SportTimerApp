package com.hblsoftware.sporttimer.data

import com.hblsoftware.sporttimer.model.WorkoutPhase
import com.hblsoftware.sporttimer.model.WorkoutProfile
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class WorkoutSessionManagerTest {

    @Test
    fun start_publishesFirstPhaseWithExpectedRemainingAndProgress() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        val manager = WorkoutSessionManager(
            nowProvider = { testScheduler.currentTime },
            dispatcher = dispatcher,
            tickMillis = 100L
        )

        val profile = profile(preparationSeconds = 2, workSeconds = 4, restSeconds = 1, rounds = 1)

        manager.start(profile)

        val state = manager.sessionState.value as WorkoutSessionState.Active
        assertEquals(WorkoutPhase.PREPARATION, state.currentEntry.phase)
        assertEquals(2, state.remainingSeconds)
        assertEquals(2, state.phaseDurationSeconds)
        assertEquals(WorkoutPhase.WORK, state.nextEntry?.phase)
        assertEquals(0f, state.progress, 0.001f)
        assertFalse(state.isPaused)
    }

    @Test
    fun pause_and_resume_keepThenContinueTimeProgression() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        val manager = WorkoutSessionManager(
            nowProvider = { testScheduler.currentTime },
            dispatcher = dispatcher,
            tickMillis = 100L
        )

        manager.start(profile(preparationSeconds = 0, workSeconds = 3, restSeconds = 0, rounds = 1))
        runCurrent()

        testScheduler.advanceTimeBy(1_200L)
        runCurrent()

        manager.togglePause()
        val pausedState = manager.sessionState.value as WorkoutSessionState.Active
        assertTrue(pausedState.isPaused)

        val pausedRemaining = pausedState.remainingSeconds
        val pausedProgress = pausedState.progress

        testScheduler.advanceTimeBy(3_000L)
        runCurrent()

        val stillPausedState = manager.sessionState.value as WorkoutSessionState.Active
        assertEquals(pausedRemaining, stillPausedState.remainingSeconds)
        assertEquals(pausedProgress, stillPausedState.progress, 0.001f)

        manager.togglePause()
        testScheduler.advanceTimeBy(1_000L)
        runCurrent()

        val resumedState = manager.sessionState.value as WorkoutSessionState.Active
        assertFalse(resumedState.isPaused)
        assertTrue(resumedState.remainingSeconds < pausedRemaining)
        assertTrue(resumedState.progress > pausedProgress)
    }

    @Test
    fun skipAndStop_driveExpectedStateTransitions() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        val manager = WorkoutSessionManager(
            nowProvider = { testScheduler.currentTime },
            dispatcher = dispatcher,
            tickMillis = 100L
        )

        val profile = profile(preparationSeconds = 1, workSeconds = 1, restSeconds = 0, rounds = 1)
        manager.start(profile)

        manager.skipToNextPhase()
        val secondPhase = manager.sessionState.value as WorkoutSessionState.Active
        assertEquals(WorkoutPhase.WORK, secondPhase.currentEntry.phase)

        manager.skipToNextPhase()
        assertEquals(WorkoutSessionState.Finished(profile), manager.sessionState.value)

        manager.stop()
        assertEquals(WorkoutSessionState.Idle, manager.sessionState.value)
    }

    @Test
    fun session_finishesAutomatically_whenTimelineElapses() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        val manager = WorkoutSessionManager(
            nowProvider = { testScheduler.currentTime },
            dispatcher = dispatcher,
            tickMillis = 100L
        )

        val profile = profile(preparationSeconds = 1, workSeconds = 1, restSeconds = 0, rounds = 1)
        manager.start(profile)
        runCurrent()

        testScheduler.advanceTimeBy(2_300L)
        runCurrent()

        assertEquals(WorkoutSessionState.Finished(profile), manager.sessionState.value)
    }

    private fun profile(
        preparationSeconds: Int,
        workSeconds: Int,
        restSeconds: Int,
        rounds: Int
    ): WorkoutProfile = WorkoutProfile(
        id = "profile-1",
        name = "Intervals",
        preparationSeconds = preparationSeconds,
        workSeconds = workSeconds,
        restSeconds = restSeconds,
        rounds = rounds
    )
}


