package com.hblsoftware.sporttimer.data

import com.hblsoftware.sporttimer.model.WorkoutPhase
import com.hblsoftware.sporttimer.model.WorkoutProfile
import org.junit.Assert.assertEquals
import org.junit.Test

class WorkoutSessionEngineTest {
    @Test
    fun buildTimeline_includesPreparationWorkAndRestForEachRound() {
        val profile = WorkoutProfile(
            name = "Intervals",
            preparationSeconds = 5,
            workSeconds = 20,
            restSeconds = 10,
            rounds = 2
        )

        val timeline = WorkoutSessionEngine.buildTimeline(profile)

        assertEquals(5, timeline.size)
        assertEquals(WorkoutPhase.PREPARATION, timeline[0].phase)
        assertEquals(WorkoutPhase.WORK, timeline[1].phase)
        assertEquals(1, timeline[1].round)
        assertEquals(WorkoutPhase.REST, timeline[2].phase)
        assertEquals(WorkoutPhase.WORK, timeline[3].phase)
        assertEquals(2, timeline[3].round)
        assertEquals(WorkoutPhase.REST, timeline[4].phase)
    }

    @Test
    fun buildTimeline_skipsZeroLengthSegments() {
        val profile = WorkoutProfile(
            name = "No Rest",
            preparationSeconds = 0,
            workSeconds = 30,
            restSeconds = 0,
            rounds = 2
        )

        val timeline = WorkoutSessionEngine.buildTimeline(profile)

        assertEquals(2, timeline.size)
        assertEquals(WorkoutPhase.WORK, timeline[0].phase)
        assertEquals(WorkoutPhase.WORK, timeline[1].phase)
    }

    @Test
    fun buildTimeline_fallsBackToSingleWorkSecond_whenAllDurationsAreZero() {
        val profile = WorkoutProfile(
            name = "Fallback",
            preparationSeconds = 0,
            workSeconds = 0,
            restSeconds = 0,
            rounds = 0
        )

        val timeline = WorkoutSessionEngine.buildTimeline(profile)

        assertEquals(1, timeline.size)
        assertEquals(WorkoutPhase.WORK, timeline.single().phase)
        assertEquals(1, timeline.single().round)
        assertEquals(1, timeline.single().durationSeconds)
    }

    @Test
    fun phaseLabel_mapsAllPhasesToExpectedShortText() {
        assertEquals("PREP", WorkoutSessionEngine.phaseLabel(WorkoutPhase.PREPARATION))
        assertEquals("WORK", WorkoutSessionEngine.phaseLabel(WorkoutPhase.WORK))
        assertEquals("REST", WorkoutSessionEngine.phaseLabel(WorkoutPhase.REST))
        assertEquals("DONE", WorkoutSessionEngine.phaseLabel(WorkoutPhase.FINISHED))
    }

    @Test
    fun phaseDescription_mapsAllPhasesToExpectedDisplayText() {
        assertEquals("Preparation", WorkoutSessionEngine.phaseDescription(WorkoutPhase.PREPARATION))
        assertEquals("Work Interval", WorkoutSessionEngine.phaseDescription(WorkoutPhase.WORK))
        assertEquals("Rest Period", WorkoutSessionEngine.phaseDescription(WorkoutPhase.REST))
        assertEquals("Workout Complete", WorkoutSessionEngine.phaseDescription(WorkoutPhase.FINISHED))
    }
}
